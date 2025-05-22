package com.indfinvestor.app.nav.stats;

import com.indfinvestor.app.nav.model.dto.MfReturnStatsDto;
import com.indfinvestor.app.nav.model.dto.MfRollingReturns;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Slf4j
@RequiredArgsConstructor
public class SchemeNavStatsItemWriter implements ItemWriter<MfRollingReturns> {

    @Value("${nav.stats.writer.insert}")
    private String schemeNavStatsInsertQuery;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends MfRollingReturns> chunk) {

        List<MapSqlParameterSource> entries = new ArrayList<>();
        chunk.getItems().forEach(item -> {
            var schemeId = item.schemeId();
            var returnDtos = item.mfReturnStatsDtos();

            for (MfReturnStatsDto record : returnDtos) {
                MapSqlParameterSource entry = new MapSqlParameterSource()
                        .addValue("year", record.getYear(), Types.BIGINT)
                        .addValue("standardDeviation", record.getStandardDeviation(), Types.NUMERIC)
                        .addValue("mean", record.getMean(), Types.NUMERIC)
                        .addValue("percentile90", record.getPercentile90(), Types.NUMERIC)
                        .addValue("percentile95", record.getPercentile95(), Types.NUMERIC)
                        .addValue("negative", record.getNegative(), Types.NUMERIC)
                        .addValue("count5", record.getCount5(), Types.NUMERIC)
                        .addValue("count10", record.getCount10(), Types.NUMERIC)
                        .addValue("count15", record.getCount15(), Types.NUMERIC)
                        .addValue("count20", record.getCount20(), Types.NUMERIC)
                        .addValue("count25Plus", record.getCount25Plus(), Types.NUMERIC)
                        .addValue("totalCount", record.getTotalCount(), Types.NUMERIC)
                        .addValue("startingYear", record.getStartingYear(), Types.BIGINT)
                        .addValue("schemeId", schemeId, Types.BIGINT);
                entries.add(entry);
            }
        });
        MapSqlParameterSource[] array = entries.toArray(new MapSqlParameterSource[entries.size()]);
        jdbcTemplate.batchUpdate(schemeNavStatsInsertQuery, array);
    }
}
