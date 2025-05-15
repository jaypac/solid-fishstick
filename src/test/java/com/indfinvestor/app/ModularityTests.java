package com.indfinvestor.app;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(AppApplication.class);

    @Test
    void verifyModularity() {

        System.out.println(modules.toString());
        modules.verify();
    }
}
