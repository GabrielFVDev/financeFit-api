package com.financefit.financeFit.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AtualizarMetaRequest(
        @NotNull(message = "Meta mensal é obrigatória")
        @PositiveOrZero(message = "Meta mensal deve ser zero ou positiva")
        Double metaMensal
) {}
