package com.financefit.financeFit.dtos;

import com.financefit.financeFit.entities.TipoTransacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceitaDTO {
    @NotNull(message = "ID é obrigatório")
    @Positive(message = "ID deve ser positivo")
    private Long id;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    private String descricao;

    @NotNull(message = "ID do usuário é obrigatório")
    @Positive(message = "ID do usuário deve ser positivo")
    private Long idUsuario;

    private Long idCategoria; // Opcional para receitas

    private CategoriaDTO categoria;

    @NotNull(message = "Tipo é obrigatório")
    private TipoTransacao tipo;

    public ReceitaDTO() {
    }

    public ReceitaDTO(Long id, BigDecimal valor, LocalDate data, String descricao, Long idUsuario, Long idCategoria, TipoTransacao tipo) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.idUsuario = idUsuario;
        this.idCategoria = idCategoria;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }
}
