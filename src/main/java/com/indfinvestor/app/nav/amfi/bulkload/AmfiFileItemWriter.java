package com.indfinvestor.app.nav.amfi.bulkload;

import com.indfinvestor.app.nav.contants.SchemeCategory;
import com.indfinvestor.app.nav.contants.SchemeSubCategory;
import com.indfinvestor.app.nav.model.dto.MfNavDetails;
import com.indfinvestor.app.nav.model.dto.MfNavRecord;
import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsRecord;
import com.indfinvestor.app.nav.model.entity.MfFundHouse;
import com.indfinvestor.app.nav.model.entity.MfSchemeDetails;
import com.indfinvestor.app.nav.service.MfFundHouseService;
import com.indfinvestor.app.nav.service.MfSchemeDetailsService;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

@Slf4j
@RequiredArgsConstructor
public class AmfiFileItemWriter implements ItemWriter<MfNavDetails> {

    private final MfFundHouseService fundHouseService;
    private final MfSchemeDetailsService mfSchemeDetailsService;

    @Value("${scheme.details.writer.insert}")
    private String schemeMfNavDetailsInsert;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private void convertToEntity(MfNavDetails newMfNavDetails) {
        MfFundHouse mfFundHouse = fundHouseService.getFundHouseByName(newMfNavDetails.getFundHouse());
        if (mfFundHouse == null) {
            log.error("Fund House not found for name {}", newMfNavDetails.getFundHouse());
            throw new RuntimeException("Fund House not found");
        }

        log.info("Started writing records for fund {} ...", mfFundHouse.getName());
        var startTime = System.currentTimeMillis();
        for (var entry : newMfNavDetails.getHistoricalNavData().entrySet()) {
            MfSchemeDetailsRecord key = entry.getKey();
            var schemeCode = key.schemeCode();
            var schemeName = key.schemeName();
            List<MfNavRecord> navRecords = entry.getValue();

            MfSchemeDetails schemeDetails = mfSchemeDetailsService.fetchSchemeDetailsByCode(schemeCode, schemeName);
            if (schemeDetails == null) {
                schemeDetails = new MfSchemeDetails();
                schemeDetails.setSchemeCode(schemeCode);
                schemeDetails.setSchemeName(schemeName);
                schemeDetails.setCategory(SchemeCategory.fromName(key.category()));
                schemeDetails.setSubCategory(SchemeSubCategory.fromName(key.subCategory()));
                schemeDetails.setFundHouse(mfFundHouse);
            }
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                    .withTableName("mf_scheme_details")
                    .usingGeneratedKeyColumns("id");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("scheme_code", schemeDetails.getSchemeCode());
            parameters.put("scheme_name", schemeDetails.getSchemeName());
            parameters.put("category", schemeDetails.getCategory().name());
            parameters.put("sub_category", schemeDetails.getSubCategory().name());
            parameters.put("fund_house_id", schemeDetails.getFundHouse().getId());

            Number returnKey = jdbcInsert.executeAndReturnKey(parameters);
            var schemeId = returnKey.longValue();

            List<MapSqlParameterSource> entries = new ArrayList<>();
            for (MfNavRecord record : navRecords) {
                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                        .addValue("navDate", record.getDate(), Types.DATE)
                        .addValue("netAssetValue", new BigDecimal(record.getNav()), Types.NUMERIC)
                        .addValue("schemeId", schemeId, Types.BIGINT);
                entries.add(mapSqlParameterSource);
            }
            MapSqlParameterSource[] array = entries.toArray(new MapSqlParameterSource[entries.size()]);
            namedParameterJdbcTemplate.batchUpdate(schemeMfNavDetailsInsert, array);
        }

        log.info(
                "Finished writing records for fund {} in {} ms",
                mfFundHouse.getName(),
                System.currentTimeMillis() - startTime);
    }

    @Override
    public void write(Chunk<? extends MfNavDetails> chunk) throws Exception {
        chunk.getItems().forEach(item -> {
            convertToEntity(item);
        });
    }
}
