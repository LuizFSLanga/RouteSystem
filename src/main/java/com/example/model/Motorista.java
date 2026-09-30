package com.example.model;

public class Motorista extends Pessoa {
    private String documento;
    private String veiculo;
    private double rendimentoKmLitro;

    public Motorista() {
    }

    public Motorista(int id, String nome, String telefone, String documento, String veiculo, double rendimentoKmLitro) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.documento = documento;
        this.veiculo = veiculo;
        this.rendimentoKmLitro = rendimentoKmLitro;
    }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }

    public double getRendimentoKmLitro() { return rendimentoKmLitro; }
    public void setRendimentoKmLitro(double rendimentoKmLitro) { this.rendimentoKmLitro = rendimentoKmLitro; }
}
