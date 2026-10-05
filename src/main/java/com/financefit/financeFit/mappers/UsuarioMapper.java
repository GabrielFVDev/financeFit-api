package com.financefit.financeFit.mappers;

import com.financefit.financeFit.dtos.request.UsuarioRequest;
import com.financefit.financeFit.dtos.request.UsuarioUpdateRequest;
import com.financefit.financeFit.dtos.response.ResumoFinanceiroResponse;
import com.financefit.financeFit.dtos.response.UsuarioResponse;
import com.financefit.financeFit.entities.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class UsuarioMapper {

    private UsuarioMapper() {}

    public static Usuario toEntity(UsuarioRequest dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setDataCriacao(LocalDate.now());
        usuario.setMetaMensal(dto.metaMensal() != null ? dto.metaMensal() : 0.0);
        return usuario;
    }

    public static Usuario toEntity(UsuarioUpdateRequest dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setMetaMensal(dto.metaMensal());
        return usuario;
    }

    public static UsuarioResponse toResponse(Usuario u) {
        if (u == null) return null;
        return new UsuarioResponse(
                u.getUserId(),
                u.getNome(),
                u.getEmail(),
                u.getDataCriacao(),
                u.getMetaMensal()
        );
    }

    public static ResumoFinanceiroResponse toResumoResponse(
            BigDecimal totalGasto, BigDecimal totalReceita, BigDecimal metaMensal,
            BigDecimal saldo, BigDecimal percentual, String status, int mes, int ano) {
        return new ResumoFinanceiroResponse(
                totalGasto, totalReceita, metaMensal, saldo, percentual, status, mes, ano);
    }
}
