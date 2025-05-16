package com.indfinvestor.app.nav.amfi.bulkload;

import com.indfinvestor.app.nav.contants.SchemeCategory;
import com.indfinvestor.app.nav.contants.SchemeSubCategory;
import com.indfinvestor.app.nav.model.dto.MfNavDetails;
import com.indfinvestor.app.nav.model.dto.MfNavRecord;
import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsRecord;
import com.indfinvestor.app.nav.model.entity.MfFundHouse;
import com.indfinvestor.app.nav.model.entity.MfSchemeDetails;
import com.indfinvestor.app.nav.model.entity.MfSchemeNav;
import com.indfinvestor.app.nav.service.MfFundHouseService;
import com.indfinvestor.app.nav.service.MfSchemeDetailsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
@RequiredArgsConstructor
public class AmfiFileItemWriter implements ItemWriter<MfNavDetails> {

    private final MfFundHouseService fundHouseService;
    private final MfSchemeDetailsService mfSchemeDetailsService;

    @PersistenceContext
    private EntityManager entityManager;

    private void convertToEntity(MfNavDetails newMfNavDetails) {
        MfFundHouse mfFundHouse = fundHouseService.getFundHouseByName(newMfNavDetails.getFundHouse());
        if(mfFundHouse == null){
            log.error("Fund House not found for name {}", newMfNavDetails.getFundHouse());
            throw new RuntimeException("Fund House not found");
        }

        log.info("Started writing records for fund {} ...", mfFundHouse.getName());
        var startTime = System.currentTimeMillis();
        for (var entry : newMfNavDetails.getHistoricalNavData().entrySet()) {
            MfSchemeDetailsRecord key = entry.getKey();
            var schemeCode = key.schemeCode();
            var schemeName = key.schemeName();
            List<MfNavRecord> navRecords = entry.getValue();

            MfSchemeDetails schemeDetails = mfSchemeDetailsService.fetchSchemeDetailsByCode(schemeCode, schemeName);
            if (schemeDetails == null) {
                schemeDetails = new MfSchemeDetails();
                schemeDetails.setSchemeCode(schemeCode);
                schemeDetails.setSchemeName(schemeName);
                schemeDetails.setCategory(SchemeCategory.fromName(key.category()));
                schemeDetails.setSubCategory(SchemeSubCategory.fromName(key.subCategory()));
                schemeDetails.setFundHouse(mfFundHouse);
            }
            entityManager.persist(schemeDetails);

            for (MfNavRecord navRecord : navRecords) {

                MfSchemeNav mfSchemeNav = new MfSchemeNav();
                mfSchemeNav.setNavDate(navRecord.getDate());
                mfSchemeNav.setNetAssetValue(new BigDecimal(navRecord.getNav()));
                mfSchemeNav.setSchemeDetails(schemeDetails);
                entityManager.persist(mfSchemeNav);
            }
        }

        log.info("Finished writing records for fund {} in {} ms", mfFundHouse.getName(), System.currentTimeMillis() - startTime);
    }

    @Override
    public void write(Chunk<? extends MfNavDetails> chunk) throws Exception {
        chunk.getItems().forEach(item -> {
            convertToEntity(item);
        });
    }
}
