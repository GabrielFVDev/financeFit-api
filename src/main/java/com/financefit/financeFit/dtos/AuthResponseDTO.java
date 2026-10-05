package com.financefit.financeFit.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthResponseDTO(
        @NotBlank String token,
        String tipo,
        @NotBlank @Email String email,
        @NotBlank String nome
) {
    public AuthResponseDTO(String token, String email, String nome) {
        this(token, "Bearer", email, nome);
    }
}
