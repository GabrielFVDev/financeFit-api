package com.financefit.financeFit.dtos.response;

import java.time.LocalDate;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        LocalDate dataCriacao,
        Double metaMensal
) {}
