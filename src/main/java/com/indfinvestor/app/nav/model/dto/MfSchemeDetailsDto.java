package com.indfinvestor.app.nav.model.dto;

import com.indfinvestor.app.nav.contants.SchemeCategory;
import com.indfinvestor.app.nav.contants.SchemeSubCategory;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class MfSchemeDetailsDto implements Serializable {

    private Long id;
    private String schemeCode;
    private String schemeName;
    private SchemeCategory category;
    private SchemeSubCategory subCategory;

    public MfSchemeDetailsDto(
            Long id, String schemeCode, String schemeName, SchemeCategory category, SchemeSubCategory subCategory) {
        this.id = id;
        this.schemeCode = schemeCode;
        this.schemeName = schemeName;
        this.category = category;
        this.subCategory = subCategory;
    }

    private List<MfSchemeNavDto> mfSchemeNavs;
}
