package com.financefit.financeFit.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CategoriaDTO {
    @NotNull(message = "ID da categoria é obrigatório")
    @Positive(message = "ID da categoria deve ser positivo")
    private Long categoriaId;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    public CategoriaDTO() {
    }

    public CategoriaDTO(Long categoriaId, String nome) {
        this.categoriaId = categoriaId;
        this.nome = nome;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
