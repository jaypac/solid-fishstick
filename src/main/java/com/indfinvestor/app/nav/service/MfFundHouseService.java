package com.indfinvestor.app.nav.service;

import com.indfinvestor.app.nav.model.entity.MfFundHouse;
import com.indfinvestor.app.nav.repository.MfFundHouseRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MfFundHouseService {
    private final MfFundHouseRepository fundHouseRepository;

    // Cache structures
    private Map<Long, MfFundHouse> fundHousesById = new ConcurrentHashMap<>();
    private Map<String, MfFundHouse> fundHousesByName = new ConcurrentHashMap<>();

    /**
     * Load all fund houses from the database into cache
     */
    public void loadCache() {
        log.info("Loading MfFundHouse cache from database");
        List<MfFundHouse> fundHouses = fundHouseRepository.findAll();

        // Build the cache maps
        fundHousesById.putAll(
                fundHouses.stream().collect(Collectors.toConcurrentMap(MfFundHouse::getId, Function.identity())));

        fundHousesByName.putAll(
                fundHouses.stream().collect(Collectors.toConcurrentMap(MfFundHouse::getName, Function.identity())));

        log.info("Loaded {} fund houses into cache", fundHouses.size());
    }

    /**
     * Get a fund house by ID from cache
     */
    public MfFundHouse getFundHouseById(Long id) {
        return fundHousesById.get(id);
    }

    /**
     * Get a fund house by name from cache
     */
    public MfFundHouse getFundHouseByName(String name) {
        return fundHousesByName.get(name);
    }

    /**
     * Get all fund houses from cache
     */
    public List<MfFundHouse> getAllFundHouses() {
        return List.copyOf(fundHousesById.values());
    }

    /**
     * Refresh the cache with the latest data from the database
     */
    public void refreshCache() {
        loadCache();
    }
}
