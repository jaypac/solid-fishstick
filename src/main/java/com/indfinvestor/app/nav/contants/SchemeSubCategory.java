package com.indfinvestor.app.nav.contants;

public enum SchemeSubCategory {
    MULTI_CAP("Multi Cap Fund"),
    GROWTH_OTHER("Growth"),
    GILT_OTHER("Gilt"),
    LIQUID_OTHER("Liquid"),
    BALANCED_OTHER("Balanced"),
    DOMESTIC_OTHER("Domestic"),
    FLEXI_CAP("Flexi Cap Fund"),
    LARGE_CAP("Large Cap Fund"),
    LARGE_AND_MID_CAP("Large & Mid Cap Fund"),
    MID_CAP("Mid Cap Fund"),
    SMALL_CAP("Small Cap Fund"),
    DIVIDEND_YIELD("Dividend Yield Fund"),
    VALUE("Value Fund"),
    FOCUSED("Focused Fund"),
    SECTORAL_THEMATIC("Sectoral/Thematic"),
    ELSS("ELSS"),
    OVERNIGHT("Overnight Fund"),
    LIQUID("Liquid Fund"),
    FLOATING_RATE("Floating Rate"),
    ULTRA_SHORT_DURATION("Ultra Short Duration Fund"),
    LOW_DURATION("Low Duration Fund"),
    MONEY_MARKET("Money Market Fund"),
    SHORT_DURATION("Short Duration Fund"),
    MEDIUM_DURATION("Medium Duration Fund"),
    MEDIUM_TO_LONG_DURATION("Medium to Long Duration Fund"),
    LONG_DURATION("Long Duration Fund"),
    DYNAMIC_BOND("Dynamic Bond"),
    CORPORATE_BOND("Corporate Bond Fund"),
    CREDIT_RISK("Credit Risk Fund"),
    BANKING_AND_PSU("Banking and PSU Fund"),
    GILT("Gilt Fund"),
    FLOATER("Floater Fund"),
    CONSERVATIVE_HYBRID("Conservative Hybrid Fund"),
    AGGRESSIVE_HYBRID("Aggressive Hybrid Fund"),
    BALANCED_HYBRID("Balanced Hybrid Fund"),
    DYNAMIC_ASSET_ALLOCATION_BALANCED_ADVANTAGE("Dynamic Asset Allocation or Balanced Advantage"),
    MULTI_ASSET("Multi Asset Allocation"),
    ARBITRAGE("Arbitrage Fund"),
    EQUITY_SAVINGS("Equity Savings"),
    RETIREMENT("Retirement Fund"),
    OVERSEAS("Overseas"),
    CONTRA("Contra Fund"),
    INDEX("Index Funds"),
    GOLD_ETF("Gold ETF"),
    OTHER_ETS("Other ETFs"),
    FOF_OVERSEAS("FoF Overseas"),
    FOF_DOMESTIC("FoF Domestic"),
    INCOME("Income"),
    CHILDRENS("Childrens Fund"),
    GILT_FUND_10_YEAR("Gilt Fund with 10 year constant duration"),
    UNCLASSIFIED("Unclassified");

    private final String name;

    SchemeSubCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SchemeSubCategory fromName(String name) {
        var categoryName =
                name.replace("/ ", "/").replace("  ", " ").replace("’s", "s").trim();
        for (SchemeSubCategory category : values()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No enum constant for name: " + name);
    }
}
