package com.financefit.financeFit.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    private double metaMensal;

    public RegisterDTO() {}

    public RegisterDTO(String nome, String email, String senha, double metaMensal) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.metaMensal = metaMensal;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public double getMetaMensal() { return metaMensal; }
    public void setMetaMensal(double metaMensal) { this.metaMensal = metaMensal; }
}

