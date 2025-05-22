package com.indfinvestor.app.nav.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MfSchemeNavDto implements Serializable {

    private BigDecimal netAssetValue;
    private LocalDate navDate;
}
