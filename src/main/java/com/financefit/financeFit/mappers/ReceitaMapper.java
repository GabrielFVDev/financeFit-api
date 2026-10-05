package com.financefit.financeFit.mappers;

import com.financefit.financeFit.dtos.request.ReceitaRequest;
import com.financefit.financeFit.dtos.response.ReceitaResponse;
import com.financefit.financeFit.entities.Receita;

public final class ReceitaMapper {

    private ReceitaMapper() {}

    public static Receita toEntity(ReceitaRequest dto) {
        Receita receita = new Receita();
        receita.setValor(dto.valor());
        receita.setData(dto.data());
        receita.setDescricao(dto.descricao());
        return receita;
    }

    public static ReceitaResponse toResponse(Receita r) {
        if (r == null) return null;
        Long userId = r.getUsuario() != null ? r.getUsuario().getUserId() : null;
        Long catId = r.getCategoria() != null ? r.getCategoria().getCategoriaId() : null;
        return new ReceitaResponse(
                r.getId(),
                r.getValor(),
                r.getData(),
                r.getDescricao(),
                userId,
                catId,
                CategoriaMapper.toResponse(r.getCategoria()),
                r.getTipo()
        );
    }
}
