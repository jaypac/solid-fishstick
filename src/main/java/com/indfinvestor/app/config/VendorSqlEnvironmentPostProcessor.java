package com.indfinvestor.app.config;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;

public class VendorSqlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String vendor = environment.getProperty("app.vendor"); // or read from env

        if (vendor != null) {
            try {
                Resource resource = new ClassPathResource("db/" + vendor + "/sql/batch.properties");
                if (resource.exists()) {
                    PropertySource<?> propertySource = new ResourcePropertySource(resource);
                    environment.getPropertySources().addLast(propertySource);
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not load vendor properties file", e);
            }
        }
    }
}
