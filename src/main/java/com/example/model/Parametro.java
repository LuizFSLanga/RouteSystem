package com.example.model;

import java.time.LocalTime;

public class Parametro {
    private float valorCombustivel;
    private float custoPorKm;
    private LocalTime jornadaPadraoHoras;
    private String regrasDeCalculo;

    public Parametro() {
    }

    public float getValorCombustivel() { return valorCombustivel; }
    public void setValorCombustivel(float valorCombustivel) { this.valorCombustivel = valorCombustivel; }

    public float getCustoPorKm() { return custoPorKm;}
    public void setCustoPorKm(float custoPorKm) { this.custoPorKm = custoPorKm;}

    public LocalTime getJornadaPadraoHoras() { return jornadaPadraoHoras; }
    public void setJornadaPadraoHoras(LocalTime jornadaPadraoHoras) { this.jornadaPadraoHoras = jornadaPadraoHoras; }

    public String getRegrasDeCalculo() { return regrasDeCalculo; }
    public void setRegrasDeCalculo(String regrasDeCalculo) { this.regrasDeCalculo = regrasDeCalculo; }
}