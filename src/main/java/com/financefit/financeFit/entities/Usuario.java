package com.financefit.financeFit.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private int userId;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "dataCriacao", nullable = false)
    private LocalDate dataCriacao;

    @Column(name = "metaMensal")
    private double metaMensal;

    public Usuario() {
    }

    public Usuario(int userId, String nome, String email, String senha, LocalDate dataCriacao, double metaMensal) {
        this.userId = userId;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCriacao = dataCriacao;
        this.metaMensal = metaMensal;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public double getMetaMensal() {
        return metaMensal;
    }

    public void setMetaMensal(double metaMensal) {
        this.metaMensal = metaMensal;
    }
}
