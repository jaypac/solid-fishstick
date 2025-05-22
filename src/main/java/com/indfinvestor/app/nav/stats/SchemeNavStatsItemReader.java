package com.indfinvestor.app.nav.stats;

import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsDto;
import com.indfinvestor.app.nav.model.dto.MfSchemeNavDto;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Slf4j
public class SchemeNavStatsItemReader implements ItemReader<MfSchemeDetailsDto> {

    @Value("${nav.stats.reader.query}")
    private String schemeNavDetailsQuery;

    private final MfSchemeDetailsDto mfSchemeDetails;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private boolean noInput = false;

    public SchemeNavStatsItemReader(
            MfSchemeDetailsDto mfSchemeDetails, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.mfSchemeDetails = mfSchemeDetails;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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
        Map<String, Object> params = new HashMap<>();
        params.put("schemeId", schemeId);

        return namedParameterJdbcTemplate.query(
                schemeNavDetailsQuery,
                params,
                (rs, rowNum) -> new MfSchemeNavDto(
                        new BigDecimal(rs.getString("netAssetValue")),
                        rs.getDate("navDate").toLocalDate()));
    }
}
