package com.financefit.financeFit.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "receita")
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receita_id")
    private Long id;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id") // Categoria is optional for income
    private Categoria categoria;

    @Enumerated(EnumType.STRING) // Adicionar esta anotação para mapear o enum para String no banco
    @Column(name = "tipo", nullable = false)
    private TipoTransacao tipo = TipoTransacao.RECEITA; // Definir RECEITA como padrão

    public Receita() {
    }

    public Receita(Long id, BigDecimal valor, LocalDate data, String descricao, Usuario usuario, Categoria categoria) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.usuario = usuario;
        this.categoria = categoria;
        this.tipo = TipoTransacao.RECEITA; // Garantir que o tipo seja RECEITA
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }
}
