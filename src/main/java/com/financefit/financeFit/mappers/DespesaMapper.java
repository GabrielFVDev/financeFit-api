package com.financefit.financeFit.mappers;

import com.financefit.financeFit.dtos.request.DespesaRequest;
import com.financefit.financeFit.dtos.response.DespesaResponse;
import com.financefit.financeFit.entities.Despesa;

public final class DespesaMapper {

    private DespesaMapper() {}

    public static Despesa toEntity(DespesaRequest dto) {
        Despesa despesa = new Despesa();
        despesa.setValor(dto.valor());
        despesa.setData(dto.data());
        despesa.setDescricao(dto.descricao());
        return despesa;
    }

    public static DespesaResponse toResponse(Despesa d) {
        if (d == null) return null;
        Long userId = d.getUsuario() != null ? d.getUsuario().getUserId() : null;
        Long catId = d.getCategoria() != null ? d.getCategoria().getCategoriaId() : null;
        return new DespesaResponse(
                d.getId(),
                d.getValor(),
                d.getData(),
                d.getDescricao(),
                userId,
                catId,
                CategoriaMapper.toResponse(d.getCategoria()),
                d.getTipo()
        );
    }
}
