package com.financefit.financeFit.dtos;

import com.financefit.financeFit.entities.TipoTransacao;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateDespesaDTO {
    @NotNull
    private BigDecimal valor;
    @NotNull
    private LocalDate data;
    private String descricao;
    @NotNull
    private int idUsuario;
    @NotNull
    private Long idCategoria; // Alterado para Long
    private TipoTransacao tipo;

    public CreateDespesaDTO() {
    }

    public CreateDespesaDTO(BigDecimal valor, LocalDate data, String descricao, Integer idUsuario, Long idCategoria, TipoTransacao tipo) {
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }
}
