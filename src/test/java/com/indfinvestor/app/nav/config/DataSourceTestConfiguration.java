package com.indfinvestor.app.nav.config;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@Configuration
public class DataSourceTestConfiguration {

    @Bean
    @BatchDataSource
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(H2)
                .addScript("/db/h2/migration/V1__Create_event_publication_table.sql")
                .addScript("/db/h2/migration/V2__Create_spring_batch_tables.sql")
                .addScript("/db/h2/migration/V3__Create_mf_tables.sql")
                .addScript("/db/h2/migration/V4__Insert_mf_fund_house_data.sql")
                .generateUniqueName(true)
                .build();
    }
}
