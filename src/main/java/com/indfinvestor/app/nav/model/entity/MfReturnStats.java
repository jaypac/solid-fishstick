package com.indfinvestor.app.nav.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MF_RETURN_STATS")
public class MfReturnStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mf_return_stats_seq")
    @SequenceGenerator(name = "mf_return_stats_seq", sequenceName = "MF_RETURN_STATS_SEQ", allocationSize = 100)
    private Long id;

    @Column(name = "YEAR_NOS", nullable = false)
    private Long year;

    @Column(name = "STD_DEV", precision = 10, scale = 2)
    private BigDecimal standardDeviation;

    @Column(name = "MEAN", precision = 10, scale = 2)
    private BigDecimal mean;

    @Column(name = "PERCENTILE_90", precision = 10, scale = 2)
    private BigDecimal percentile90;

    @Column(name = "PERCENTILE_95", precision = 10, scale = 2)
    private BigDecimal percentile95;

    @Column(name = "NEGATIVE_COUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal negative;

    @Column(name = "COUNT_5", nullable = false, precision = 10, scale = 2)
    private BigDecimal count5;

    @Column(name = "COUNT_10", nullable = false, precision = 10, scale = 2)
    private BigDecimal count10;

    @Column(name = "COUNT_15", nullable = false, precision = 10, scale = 2)
    private BigDecimal count15;

    @Column(name = "COUNT_20", nullable = false, precision = 10, scale = 2)
    private BigDecimal count20;

    @Column(name = "COUNT_25", nullable = false, precision = 10, scale = 2)
    private BigDecimal count25Plus;

    @Column(name = "TOTAL_COUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCount;

    @Column(name = "STARTING_YEAR", nullable = false)
    private Long startingYear;

    @ManyToOne
    @JoinColumn(name = "SCHEME_ID", nullable = false)
    private MfSchemeDetails schemeDetails;
}
