package com.indfinvestor.app.nav.contants;

public enum SchemeCategory {
    EQUITY("Equity"),
    DEBT("Debt"),
    HYBRID("Hybrid"),
    SOLUTION_ORIENTED("Solution Oriented"),
    OTHER("Other"),
    UNCLASSIFIED("Unclassified");

    private final String name;

    SchemeCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
