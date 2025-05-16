package com.indfinvestor.app.nav.model.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MfNavDetails {
    @NotBlank private String fundHouse;

    private Map<MfSchemeDetailsRecord, List<MfNavRecord>> historicalNavData;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        MfNavDetails that = (MfNavDetails) o;
        return fundHouse.equals(that.fundHouse);
    }

    @Override
    public int hashCode() {
        return fundHouse.hashCode();
    }
}
