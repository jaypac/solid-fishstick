package com.indfinvestor.app.nav.repository;

import com.indfinvestor.app.nav.model.entity.MfFundHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MfFundHouseRepository extends JpaRepository<MfFundHouse, Long> {
    MfFundHouse findByName(String name);
}
