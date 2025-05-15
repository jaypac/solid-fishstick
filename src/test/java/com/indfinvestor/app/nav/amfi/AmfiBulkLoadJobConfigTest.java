package com.indfinvestor.app.nav.amfi;

import com.indfinvestor.app.nav.amfi.bulkload.config.AmfiBulkFileNavTransformerJobConfig;
import com.indfinvestor.app.nav.config.JobSetupTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({JobSetupTestConfiguration.class, AmfiBulkFileNavTransformerJobConfig.class})
@SpringBatchTest
@ActiveProfiles("test")
class AmfiBulkLoadJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeEach
    public void setup(@Autowired Job jobUnderTest) {
        this.jobLauncherTestUtils.setJob(jobUnderTest); // this is optional if the job is unique
        this.jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    public void testMyJob() throws Exception {
        String path =
                resourceLoader.getResource("classpath:nav/PPFAS.txt").getFile().getAbsolutePath();

        // given
        JobParameters jobParameters =
                new JobParametersBuilder().addString("filePath", path).toJobParameters();

        // when
        JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(jobParameters);

        // then
        Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}
