package com.example.model;

public class Gerente extends Pessoa {
    private String email;
    private String equipeSobResponsabilidade;

    public Gerente() {
    }

    public Gerente(int id, String nome, String telefone, String email, String equipeSobResponsabilidade) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.equipeSobResponsabilidade = equipeSobResponsabilidade;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getequipeSobResponsabilidade() { return equipeSobResponsabilidade; }
    public void setequipeSobResponsabilidade(String equipeSobResponsabilidade) { this.equipeSobResponsabilidade = equipeSobResponsabilidade; }
}