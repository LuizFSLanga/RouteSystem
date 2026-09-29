package com.example.model;

public class Parametro {
    private int id;
    private Double valorCombustivel;
    private Double kmLitro;
    private Double custoPorKm;
    private String jornadaPadraoHoras;
    private String regrasDeCalculo;

    public Parametro() {
    }

    public Parametro(int id,Double valorCombustivel, Double kmLitro, Double custoPorKm, String jornadaPadraoHoras, String regrasDeCalculo) {

        this.id = id;
        this.valorCombustivel = valorCombustivel;
        this.kmLitro = kmLitro;
        this.custoPorKm = custoPorKm;
        this.jornadaPadraoHoras = jornadaPadraoHoras;
        this.regrasDeCalculo = regrasDeCalculo;

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Double getValorCombustivel() { return valorCombustivel; }
    public void setValorCombustivel(Double valorCombustivel) { this.valorCombustivel = valorCombustivel; }

    public Double getKmLitro() { return kmLitro; }
    public void setKmLitro(Double kmLitro) { this.kmLitro = kmLitro; }

    public Double getCustoPorKm() { return custoPorKm;}
    public void setCustoPorKm(Double custoPorKm) { this.custoPorKm = custoPorKm;}

    public String getJornadaPadraoHoras() { return jornadaPadraoHoras; }
    public void setJornadaPadraoHoras(String jornadaPadraoHoras) { this.jornadaPadraoHoras = jornadaPadraoHoras; }

    public String getRegrasDeCalculo() { return regrasDeCalculo; }
    public void setRegrasDeCalculo(String regrasDeCalculo) { this.regrasDeCalculo = regrasDeCalculo; }
}