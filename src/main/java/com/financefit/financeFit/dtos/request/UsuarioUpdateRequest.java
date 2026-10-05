package com.financefit.financeFit.dtos.request;

import com.financefit.financeFit.entities.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @Email(message = "Email inválido")
        @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
        String email,

        @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
        String senha,

        Double metaMensal
) {}
