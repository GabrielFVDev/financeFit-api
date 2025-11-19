package com.financefit.financeFit.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Long categoriaId; // Alterado para Long

    @Column(name = "nome", nullable = false)
    private String nome;

    public Categoria() {
    }

    public Categoria(Long categoriaId, String nome) { // Alterado para Long
        this.categoriaId = categoriaId;
        this.nome = nome;
    }

    public Long getCategoriaId() { // Alterado para Long
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) { // Alterado para Long
        this.categoriaId = categoriaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
