package com.indfinvestor.app.nav.amfi.bulkload;

import com.indfinvestor.app.nav.model.dto.MfNavDetails;
import com.indfinvestor.app.nav.service.MfFundHouseService;
import com.indfinvestor.app.nav.service.MfSchemeDetailsService;
import java.nio.charset.StandardCharsets;
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
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Slf4j
@Configuration
public class AmfiBulkFileNavTransformerJobConfig {

    @Bean(name = "amfiFilePartitioner")
    @StepScope
    public Partitioner getPartitioner(final @Value("#{jobParameters['filePath']}") String inputPath) {
        return new AmfiFilePartitioner(inputPath);
    }

    @Bean(name = "amfiFileItemReader")
    @StepScope
    public ItemReader<MfNavDetails> itemReader(final @Value("#{stepExecutionContext['fileName']}") String fileName) {
        log.info("Reading file {}", fileName);
        return new AmfiFileItemReader("itemReader", fileName, StandardCharsets.UTF_8);
    }

    @Bean(name = "amfiFileItemProcessor")
    @StepScope
    public AmfiFileItemProcessor itemProcessor() {
        return new AmfiFileItemProcessor();
    }

    @Bean
    public BeanValidatingItemProcessor<MfNavDetails> itemValidator() throws Exception {
        BeanValidatingItemProcessor<MfNavDetails> validator = new BeanValidatingItemProcessor<>();
        validator.setFilter(true);
        validator.afterPropertiesSet();
        return validator;
    }

    @Bean(name = "amfiFileItemWriter")
    @StepScope
    public AmfiFileItemWriter getItemWriter(
            final MfFundHouseService fundHouseService,
            final MfSchemeDetailsService mfSchemeDetailsService,
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new AmfiFileItemWriter(fundHouseService, mfSchemeDetailsService, namedParameterJdbcTemplate);
    }

    @Bean(name = "amfiBulkFileNavTransformerJob")
    public Job job(
            final JobRepository jobRepository,
            final JdbcTransactionManager jdbcTransactionManager,
            final @Qualifier("amfiFileItemReader") ItemReader<MfNavDetails> itemReader,
            final @Qualifier("amfiFileItemProcessor") ItemProcessor<MfNavDetails, MfNavDetails> itemProcessor,
            final @Qualifier("amfiFileItemWriter") ItemWriter<MfNavDetails> itemWriter,
            final @Qualifier("amfiFilePartitioner") Partitioner partitioner)
            throws Exception {

        var step1 = new StepBuilder("step1", jobRepository)
                .<MfNavDetails, MfNavDetails>chunk(5, jdbcTransactionManager)
                .reader(itemReader)
                .processor(itemValidator())
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();

        var partitionStep = new StepBuilder("partitionJobStep", jobRepository)
                .partitioner("step1", partitioner)
                .step(step1)
                .gridSize(4)
                .taskExecutor(new VirtualThreadTaskExecutor())
                .build();

        return new JobBuilder("amfiBulkNavTransformerJob", jobRepository)
                .start(partitionStep)
                .build();
    }
}
