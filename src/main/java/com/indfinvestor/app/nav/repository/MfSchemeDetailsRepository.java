package com.indfinvestor.app.nav.repository;

import com.indfinvestor.app.nav.model.entity.MfSchemeDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MfSchemeDetailsRepository extends JpaRepository<MfSchemeDetails, Long> {

    Optional<MfSchemeDetails> findBySchemeCodeAndSchemeName(String schemeCode, String schemeName);
}
