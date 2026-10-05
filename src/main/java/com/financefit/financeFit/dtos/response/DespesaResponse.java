package com.financefit.financeFit.dtos.response;

import com.financefit.financeFit.entities.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaResponse(
        Long id,
        BigDecimal valor,
        LocalDate data,
        String descricao,
        Long idUsuario,
        Long idCategoria,
        CategoriaResponse categoria,
        TipoTransacao tipo
) {}
