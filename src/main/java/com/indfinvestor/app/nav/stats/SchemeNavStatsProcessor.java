package com.indfinvestor.app.nav.stats;

import com.google.common.math.Quantiles;
import com.google.common.math.StatsAccumulator;
import com.indfinvestor.app.nav.model.dto.MfReturnStatsDto;
import com.indfinvestor.app.nav.model.dto.MfRollingReturns;
import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsDto;
import com.indfinvestor.app.nav.model.dto.MfSchemeNavDto;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@RequiredArgsConstructor
public class SchemeNavStatsProcessor implements ItemProcessor<MfSchemeDetailsDto, MfRollingReturns> {

    private final String startingYear;

    @Override
    public MfRollingReturns process(MfSchemeDetailsDto schemeDetails) throws Exception {

        var schemeName = schemeDetails.getSchemeName();
        var navHistory = schemeDetails.getMfSchemeNavs();

        log.info("Calculating Rolling Returns for {}", schemeName);
        var navDateMap = schemeDetails.getMfSchemeNavs().stream()
                .collect(Collectors.toMap(MfSchemeNavDto::getNavDate, MfSchemeNavDto::getNetAssetValue));

        var yearMap = new HashMap<Integer, List<Double>>();
        int maxCountOfYears = 10;
        for (int i = 1; i <= maxCountOfYears; i++) {
            yearMap.put(i, new ArrayList<>());
        }

        navHistory.forEach(record -> {
            for (int yearCount = 1; yearCount <= maxCountOfYears; yearCount++) {

                BigDecimal price = record.getNetAssetValue();
                List<Double> pctValues = yearMap.get(yearCount);

                if (price.compareTo(BigDecimal.ZERO) == 0) {
                    pctValues.add(BigDecimal.ZERO.doubleValue());
                } else {
                    LocalDate navDate = record.getNavDate();
                    LocalDate previousNavDate = localDate(navDate, navDateMap, (long) yearCount);

                    if (navDateMap.containsKey(previousNavDate)) {
                        BigDecimal oldPrice = navDateMap.get(previousNavDate);
                        if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
                            pctValues.add(BigDecimal.ZERO.doubleValue());
                        } else {
                            // LOG.info("1 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice,
                            // price.doubleValue());
                            var pct = ((price.doubleValue() - oldPrice.doubleValue()) / price.doubleValue()) * 100;

                            BigDecimal cagr = new BigDecimal(pct);
                            if (yearCount > 1) {
                                cagr = calculateCagr(price, oldPrice, yearCount);
                            }
                            pctValues.add(cagr.doubleValue());
                        }
                    }
                }
            }
        });

        // mfRollingReturnsRepository.save(mfRollingReturns)
        log.info("Calculating Stats for {}", schemeName);

        List<MfReturnStatsDto> mfReturnStatsDtos = new ArrayList<>();
        for (Map.Entry<Integer, List<Double>> entry : yearMap.entrySet()) {

            var year = entry.getKey();
            var pctValues = entry.getValue();

            var accumYear = new StatsAccumulator();

            if (!pctValues.isEmpty()) {

                accumYear.addAll(pctValues);
                var mfReturnStats = new MfReturnStatsDto();
                mfReturnStats.setYear(Long.valueOf(year));
                mfReturnStats.setStandardDeviation(BigDecimal.valueOf(accumYear.populationStandardDeviation()));
                mfReturnStats.setMean(BigDecimal.valueOf(accumYear.mean()));
                mfReturnStats.setPercentile90(
                        BigDecimal.valueOf(Quantiles.percentiles().index(90).compute(pctValues)));
                mfReturnStats.setPercentile95(
                        BigDecimal.valueOf(Quantiles.percentiles().index(95).compute(pctValues)));
                setFrequencyDistribution(mfReturnStats, pctValues);
                mfReturnStatsDtos.add(mfReturnStats);
                mfReturnStats.setStartingYear(Long.valueOf(startingYear));
            }
        }

        return new MfRollingReturns(schemeDetails.getId(), mfReturnStatsDtos);
    }

    private LocalDate localDate(LocalDate navDate, Map<LocalDate, BigDecimal> navDateMap, Long minusYears) {
        var oldDate = navDate.minusYears(minusYears);

        var isNavPresentForDate = navDateMap.containsKey(oldDate);
        var count = 7;
        while (!isNavPresentForDate && count-- > 0) {
            oldDate = oldDate.minusDays(1L);
            isNavPresentForDate = navDateMap.containsKey(oldDate);
        }

        return oldDate;
    }

    private BigDecimal calculateCagr(BigDecimal futurePrice, BigDecimal oldPrice, int numberOfYears) {

        // CAGR = (FV / PV) ^ (1 / n) – 1
        MathContext mc = new MathContext(2, RoundingMode.HALF_UP);

        var division = futurePrice.divide(oldPrice, mc);
        var exponent = BigDecimal.ONE.divide(new BigDecimal(numberOfYears), mc);
        var pow = Math.pow(division.doubleValue(), exponent.doubleValue());
        var cagr = BigDecimal.valueOf(pow).subtract(BigDecimal.ONE);
        return cagr.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    private void setFrequencyDistribution(MfReturnStatsDto indexReturnStats, List<Double> pctValues) {

        int negative = 0;
        int count5 = 0;
        int count10 = 0;
        int count15 = 0;
        int count20 = 0;
        int count25 = 0;

        for (Double value : pctValues) {

            if (value < 0) {
                negative++;
            } else if (value <= 5) {
                count5++;
            } else if (value <= 10) {
                count10++;
            } else if (value <= 15) {
                count15++;
            } else if (value <= 20) {
                count20++;
            } else {
                count25++;
            }
        }

        int total = negative + count5 + count10 + count15 + count20 + count25;

        if (total > 0) {
            indexReturnStats.setNegative(getValue(negative, total));
            indexReturnStats.setCount5(getValue(count5, total));
            indexReturnStats.setCount10(getValue(count10, total));
            indexReturnStats.setCount15(getValue(count15, total));
            indexReturnStats.setCount20(getValue(count20, total));
            indexReturnStats.setCount25Plus(getValue(count25, total));
            indexReturnStats.setTotalCount(BigDecimal.valueOf(total));
        }
    }

    private BigDecimal getValue(int count, int total) {
        BigDecimal countOfEntries = BigDecimal.valueOf(count);
        BigDecimal totalCount = BigDecimal.valueOf(total);
        BigDecimal percentage =
                countOfEntries.divide(totalCount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

        return percentage.setScale(2, RoundingMode.HALF_UP);
    }
}
