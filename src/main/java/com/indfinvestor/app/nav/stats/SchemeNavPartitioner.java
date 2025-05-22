package com.indfinvestor.app.nav.stats;

import com.indfinvestor.app.nav.contants.SchemeCategory;
import com.indfinvestor.app.nav.contants.SchemeSubCategory;
import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsDto;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
@StepScope
public class SchemeNavPartitioner implements Partitioner {

    @Value("${nav.stats.partition.query}")
    private String partitionSqlQuery;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<MfSchemeDetailsDto> allSchemes = findAll();
        Map<String, ExecutionContext> partitions = new HashMap<>(gridSize);
        for (int i = 0; i < allSchemes.size(); i++) {
            ExecutionContext context = new ExecutionContext();
            context.put("mfSchemeDetails", allSchemes.get(i));
            context.putInt("partitionIndex", i);
            partitions.put("partition" + i + "-" + allSchemes.get(i).getSchemeCode(), context);
        }

        return partitions;
    }

    private List<MfSchemeDetailsDto> findAll() {
        return namedParameterJdbcTemplate.query(
                partitionSqlQuery,
                Collections.emptyMap(),
                (rs, rowNum) -> new MfSchemeDetailsDto(
                        rs.getLong("id"),
                        rs.getString("schemeCode"),
                        rs.getString("schemeName"),
                        SchemeCategory.valueOf(rs.getString("category")),
                        SchemeSubCategory.valueOf(rs.getString("subCategory"))));
    }
}
