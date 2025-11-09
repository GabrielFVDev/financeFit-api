package com.financefit.financeFit.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private int categoriaId;

    @Column(name = "nome", nullable = false)
    private String nome;

    public Categoria() {
    }

    public Categoria(int categoriaId, String nome) {
        this.categoriaId = categoriaId;
        this.nome = nome;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
