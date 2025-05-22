package com.indfinvestor.app.nav.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MfNavRecord {

    private String schemeCode;
    private String schemeName;
    private String nav;
    private LocalDate date;
    private String category;
    private String subCategory;
}
