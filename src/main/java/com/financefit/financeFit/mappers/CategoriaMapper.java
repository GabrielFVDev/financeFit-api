package com.financefit.financeFit.mappers;

import com.financefit.financeFit.dtos.request.CategoriaRequest;
import com.financefit.financeFit.dtos.response.CategoriaResponse;
import com.financefit.financeFit.entities.Categoria;

public final class CategoriaMapper {

    private CategoriaMapper() {}

    public static Categoria toEntity(CategoriaRequest dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        return categoria;
    }

    public static CategoriaResponse toResponse(Categoria c) {
        if (c == null) return null;
        return new CategoriaResponse(c.getCategoriaId(), c.getNome());
    }
}
