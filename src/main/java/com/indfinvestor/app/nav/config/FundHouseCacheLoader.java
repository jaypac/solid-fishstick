package com.indfinvestor.app.nav.config;

import com.indfinvestor.app.nav.service.MfFundHouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FundHouseCacheLoader {
    private final MfFundHouseService fundHouseService;

    @EventListener(ApplicationReadyEvent.class)
    public void loadCacheOnStartup() {
        log.info("Application started, loading MfFundHouse cache");
        fundHouseService.loadCache();
        log.info(fundHouseService.getFundHouseById(1L).toString());
    }
}
