package com.indfinvestor.app.nav.amfi.bulkload;

import com.indfinvestor.app.nav.model.dto.MfNavDetails;
import com.indfinvestor.app.nav.model.dto.MfNavRecord;
import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class AmfiFileItemProcessor implements ItemProcessor<MfNavDetails, MfNavDetails> {

    @Override
    public MfNavDetails process(MfNavDetails item) throws Exception {

        MfNavDetails newMfNavDetails = new MfNavDetails();
        newMfNavDetails.setFundHouse(item.getFundHouse());
        log.info("Processing records for fund {}", item.getFundHouse());
        var historicalData = item.getHistoricalNavData();
        historicalData.forEach((k, v) ->
                v.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate())));

        Map<MfSchemeDetailsRecord, List<MfNavRecord>> historicalNavData = new HashMap<>();
        item.getHistoricalNavData().forEach((key, value) -> {
            Map<LocalDate, MfNavRecord> navHistory =
                    value.stream().collect(Collectors.toMap(MfNavRecord::getDate, Function.identity()));

            List<MfNavRecord> navRecords = populateMissingDates(navHistory, value);
            historicalNavData.put(key, navRecords);
        });

        newMfNavDetails.setHistoricalNavData(historicalNavData);
        return newMfNavDetails;
    }

    private List<MfNavRecord> populateMissingDates(Map<LocalDate, MfNavRecord> navHistory, List<MfNavRecord> value) {
        List<MfNavRecord> records = new ArrayList<>();

        // Get the first date
        var firstDate = value.getFirst().getDate();
        var lastDate = value.getLast().getDate();

        // Loop through the date range
        for (LocalDate date = firstDate; !date.isAfter(lastDate); date = date.plusDays(1)) {
            // Check if the date is present in the navHistory map
            if (navHistory.containsKey(date)) {
                // If present, add the record to the list
                records.add(navHistory.get(date));
            } else {
                // If not present, search for the previous date
                var navRecord = new MfNavRecord();
                var count = 1;
                while (count <= 7) {
                    var oldDate = date.minusDays(count);
                    if (navHistory.containsKey(oldDate)) {
                        var oldRecord = navHistory.get(oldDate);
                        BeanUtils.copyProperties(oldRecord, navRecord);
                        navRecord.setDate(date);
                        records.add(navRecord);
                        break;
                    }

                    count++;
                }
            }
        }

        return records;
    }
}
