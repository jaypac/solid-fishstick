package com.indfinvestor.app.nav.stats;

import com.indfinvestor.app.nav.model.dto.MfRollingReturns;
import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class SchemeNavStatsGeneratorJobConfig {

    @Bean(name = "schemeNavStatsItemReader")
    @StepScope
    public ItemReader<MfSchemeDetailsDto> itemReader(
            final @Value("#{stepExecutionContext['mfSchemeDetails']}") MfSchemeDetailsDto mfSchemeDetails,
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new SchemeNavStatsItemReader(mfSchemeDetails, namedParameterJdbcTemplate);
    }

    @Bean(name = "schemeNavStatsProcessor")
    @StepScope
    public SchemeNavStatsProcessor itemProcessor(final @Value("#{jobParameters['startingYear']}") String startingYear) {
        return new SchemeNavStatsProcessor(startingYear);
    }

    @Bean(name = "schemeNavStatsItemWriter")
    @StepScope
    public SchemeNavStatsItemWriter getItemWriter(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new SchemeNavStatsItemWriter(namedParameterJdbcTemplate);
    }

    @Bean(name = "navStatsTaskExecutor")
    public TaskExecutor taskExecutor() {
        var taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(10000);
        return taskExecutor;
    }


    @Bean(name = "navStatsGeneratorJob")
    public Job job(
            final JobRepository jobRepository,
            final JdbcTransactionManager jdbcTransactionManager,
            final @Qualifier("schemeNavStatsItemReader") ItemReader<MfSchemeDetailsDto> itemReader,
            final @Qualifier("schemeNavStatsProcessor") ItemProcessor<MfSchemeDetailsDto, MfRollingReturns>
                    itemProcessor,
            final @Qualifier("schemeNavStatsItemWriter") ItemWriter<MfRollingReturns> itemWriter,
            final @Qualifier("schemeNavPartitioner") Partitioner partitioner,
            final @Qualifier("navStatsTaskExecutor") TaskExecutor taskExecutor) {

        var navStatsGenerationStep = new StepBuilder("navStatsGenerationStep", jobRepository)
                .<MfSchemeDetailsDto, MfRollingReturns>chunk(5, jdbcTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();

        var partitionStep = new StepBuilder("navStatsPartitionJobStep", jobRepository)
                .partitioner("navStatsGenerationStep", partitioner)
                .step(navStatsGenerationStep)
                .gridSize(5)
                .taskExecutor(taskExecutor)
                .build();

        return new JobBuilder("navStatsGeneratorJob", jobRepository)
                .start(partitionStep)
                .build();
    }
}
