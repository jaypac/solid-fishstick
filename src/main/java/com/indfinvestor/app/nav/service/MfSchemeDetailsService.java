package com.indfinvestor.app.nav.service;

import com.indfinvestor.app.nav.model.entity.MfSchemeDetails;
import com.indfinvestor.app.nav.repository.MfSchemeDetailsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MfSchemeDetailsService {

    private final MfSchemeDetailsRepository schemeDetailsRepository;

    public MfSchemeDetails fetchSchemeDetailsByCode(String schemeCode, String schemeName) {
        Optional<MfSchemeDetails> response =
                schemeDetailsRepository.findBySchemeCodeAndSchemeName(schemeCode, schemeName);
        return response.orElse(null);
    }
}
