package com.financefit.financeFit.dtos;

public class UpdateUsuarioDTO {
    private String nome;
    private String email;
    private String senha;
    private Double metaMensal;

    public UpdateUsuarioDTO() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Double getMetaMensal() { return metaMensal; }
    public void setMetaMensal(Double metaMensal) { this.metaMensal = metaMensal; }
}

