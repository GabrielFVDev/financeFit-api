package com.financefit.financeFit.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReceitaRequest(
        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser positivo")
        BigDecimal valor,

        @NotNull(message = "Data é obrigatória")
        LocalDate data,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
        String descricao,

        @NotNull(message = "ID da categoria é obrigatório")
        @Positive(message = "ID da categoria deve ser positivo")
        Long idCategoria
) {}
