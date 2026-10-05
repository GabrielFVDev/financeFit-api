package com.financefit.financeFit.dtos.response;

import java.math.BigDecimal;

public record ResumoFinanceiroResponse(
        BigDecimal totalGasto,
        BigDecimal totalReceita,
        BigDecimal metaMensal,
        BigDecimal saldo,
        BigDecimal percentualUsado,
        String statusMeta,
        int mes,
        int ano
) {}
