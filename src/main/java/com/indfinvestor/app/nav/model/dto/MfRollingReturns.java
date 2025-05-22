package com.indfinvestor.app.nav.model.dto;

import java.util.List;

public record MfRollingReturns(Long schemeId, List<MfReturnStatsDto> mfReturnStatsDtos) {}
