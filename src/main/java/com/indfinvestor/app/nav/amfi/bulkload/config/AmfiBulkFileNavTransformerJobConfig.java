package com.indfinvestor.app.nav.amfi.bulkload.config;

import com.indfinvestor.app.nav.amfi.bulkload.AmfiFileItemProcessor;
import com.indfinvestor.app.nav.amfi.bulkload.AmfiFileItemReader;
import com.indfinvestor.app.nav.amfi.bulkload.AmfiFileItemWriter;
import com.indfinvestor.app.nav.amfi.bulkload.AmfiFilePartitioner;
import com.indfinvestor.app.nav.model.dto.MfNavDetails;
import com.indfinvestor.app.nav.service.MfFundHouseService;
import com.indfinvestor.app.nav.service.MfSchemeDetailsService;
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
import org.springframework.orm.jpa.JpaTransactionManager;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class AmfiBulkFileNavTransformerJobConfig {

    @Bean(name = "amfiFilePartitioner")
    @StepScope
    public Partitioner getPartitioner(@Value("#{jobParameters['filePath']}") String inputPath) {
        return new AmfiFilePartitioner(inputPath);
    }

    @Bean(name = "amfiFileItemReader")
    @StepScope
    public ItemReader<MfNavDetails> itemReader(@Value("#{stepExecutionContext['fileName']}") String fileName) {
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
            MfFundHouseService fundHouseService, MfSchemeDetailsService mfSchemeDetailsService) {

        return new AmfiFileItemWriter(fundHouseService, mfSchemeDetailsService);
    }

    @Bean(name = "amfiBulkFileNavTransformerJob")
    public Job job(
            JobRepository jobRepository,
            JpaTransactionManager jpaTransactionManager,
            @Qualifier("amfiFileItemReader") ItemReader<MfNavDetails> itemReader,
            @Qualifier("amfiFileItemProcessor") ItemProcessor<MfNavDetails, MfNavDetails> itemProcessor,
            @Qualifier("amfiFileItemWriter") ItemWriter<MfNavDetails> itemWriter,
            @Qualifier("amfiFilePartitioner") Partitioner partitioner)
            throws Exception {

        var step1 = new StepBuilder("step1", jobRepository)
                .<MfNavDetails, MfNavDetails>chunk(5, jpaTransactionManager)
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
