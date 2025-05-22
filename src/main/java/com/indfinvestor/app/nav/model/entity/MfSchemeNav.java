package com.indfinvestor.app.nav.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MF_SCHEME_NAV")
public class MfSchemeNav {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAV_DATE", nullable = false)
    private LocalDate navDate;

    @Column(name = "NET_ASSET_VALUE", nullable = false)
    private BigDecimal netAssetValue;

    @ManyToOne
    @JoinColumn(name = "SCHEME_ID", nullable = false)
    private MfSchemeDetails schemeDetails;
}
