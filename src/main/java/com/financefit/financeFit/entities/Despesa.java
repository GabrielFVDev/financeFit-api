package com.financefit.financeFit.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "despesa")
public class Despesa {
    private int id;
    private double valor;
    private LocalDate data;
    private String descricao;
    private Usuario usuario;
    private Categoria categoria;
}
