package com.example.model;

public class Pessoa {
    protected int id;
    protected String nome;
    protected String telefone;

    public Pessoa() {
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}