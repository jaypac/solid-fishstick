package com.indfinvestor.app.nav.service;

import com.indfinvestor.app.nav.model.entity.MfFundHouse;
import com.indfinvestor.app.nav.repository.MfFundHouseRepository;
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

    public MfFundHouse getFundHouseById(Long id) {
        return fundHouseRepository.getReferenceById(id);
    }

    public MfFundHouse getFundHouseByName(String name) {
        return fundHouseRepository.findByName(name);
    }
}
