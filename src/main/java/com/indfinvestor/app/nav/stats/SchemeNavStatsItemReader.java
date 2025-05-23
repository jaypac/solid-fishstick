package com.indfinvestor.app.nav.stats;

import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsDto;
import com.indfinvestor.app.nav.model.dto.MfSchemeNavDto;
import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Slf4j
public class SchemeNavStatsItemReader implements ItemReader<MfSchemeDetailsDto> {

    @Value("${nav.stats.reader.query}")
    private String schemeNavDetailsQuery;

    private final MfSchemeDetailsDto mfSchemeDetails;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final String startingYear;
    private boolean noInput = false;

    public SchemeNavStatsItemReader(
            MfSchemeDetailsDto mfSchemeDetails,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            String startingYear) {
        this.mfSchemeDetails = mfSchemeDetails;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.startingYear = startingYear;
    }

    @Override
    public MfSchemeDetailsDto read() {

        if (noInput) {
            return null;
        }

        noInput = true;
        mfSchemeDetails.setMfSchemeNavs(findAll(mfSchemeDetails.getId()));
        return mfSchemeDetails;
    }

    private List<MfSchemeNavDto> findAll(Long schemeId) {

        // Create parameter map
        LocalDate firstDayOfYear = LocalDate.of(Integer.parseInt(startingYear), Month.JANUARY, 1);

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("schemeId", schemeId, Types.BIGINT)
                .addValue("startingDate", firstDayOfYear, Types.DATE);

        return namedParameterJdbcTemplate.query(
                schemeNavDetailsQuery,
                mapSqlParameterSource,
                (rs, rowNum) -> new MfSchemeNavDto(
                        new BigDecimal(rs.getString("netAssetValue")),
                        rs.getDate("navDate").toLocalDate()));
    }
}
