package com.indfinvestor.app.nav.contants;

public enum SchemeCategory {
    EQUITY("Equity Scheme"),
    DEBT("Debt Scheme"),
    HYBRID("Hybrid Scheme"),
    SOLUTION_ORIENTED("Solution Oriented Scheme"),
    OTHER("Other Scheme"),
    OTHER_UNCLASSIFIED("Other"),
    FUND_OF_FUNDS("Fund of Funds"),
    UNCLASSIFIED("Unclassified");

    private final String name;

    SchemeCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SchemeCategory fromName(String name) {
        for (SchemeCategory category : values()) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No enum constant for name: " + name);
    }
}
