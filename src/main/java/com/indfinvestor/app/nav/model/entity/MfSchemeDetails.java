package com.indfinvestor.app.nav.model.entity;

import com.indfinvestor.app.nav.contants.SchemeCategory;
import com.indfinvestor.app.nav.contants.SchemeSubCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@Entity
@Table(name = "MF_SCHEME_DETAILS")
public class MfSchemeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mf_scheme_details_seq")
    @SequenceGenerator(name = "mf_scheme_details_seq", sequenceName = "MF_SCHEME_DETAILS_SEQ", allocationSize = 10)
    private Long id;

    @NaturalId
    @Column(name = "SCHEME_CODE", nullable = false)
    private String schemeCode;

    @NaturalId
    @Column(name = "SCHEME_NAME", nullable = false)
    private String schemeName;

    @Column(name = "CATEGORY", nullable = false)
    @Enumerated(EnumType.STRING)
    private SchemeCategory category;

    @Column(name = "SUB_CATEGORY", nullable = false)
    @Enumerated(EnumType.STRING)
    private SchemeSubCategory subCategory;

    @ManyToOne
    @JoinColumn(name = "FUND_HOUSE_ID", nullable = false)
    private MfFundHouse fundHouse;
}
