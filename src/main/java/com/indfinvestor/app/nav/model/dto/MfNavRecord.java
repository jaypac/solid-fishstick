package com.indfinvestor.app.nav.model.dto;

import java.time.LocalDate;

public class MfNavRecord {

    private String schemeCode;
    private String schemeName;
    private String nav;
    private LocalDate date;
    private String category;
    private String subCategory;

    public MfNavRecord() {}

    public MfNavRecord(
            String schemeCode, String schemeName, String nav, LocalDate date, String category, String subCategory) {
        this.schemeCode = schemeCode;
        this.schemeName = schemeName;
        this.nav = nav;
        this.date = date;
        this.category = category;
        this.subCategory = subCategory;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getNav() {
        return nav;
    }

    public void setNav(String nav) {
        this.nav = nav;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
}
