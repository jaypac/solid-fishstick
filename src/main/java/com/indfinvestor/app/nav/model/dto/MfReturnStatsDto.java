package com.indfinvestor.app.nav.model.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MfReturnStatsDto {

    private Long year;
    private BigDecimal standardDeviation;
    private BigDecimal mean;
    private BigDecimal percentile90;
    private BigDecimal percentile95;
    private BigDecimal negative;
    private BigDecimal count5;
    private BigDecimal count10;
    private BigDecimal count15;
    private BigDecimal count20;
    private BigDecimal count25Plus;
    private BigDecimal totalCount;
    private Long startingYear;
    private Long schemeDetailsId;
}
