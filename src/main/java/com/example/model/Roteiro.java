package com.example.model;

import java.sql.Date;

public class Roteiro {
    private int id;
    private Date data;
    private int idMotorista;
    private Double distanciaTotal = 0.0;
    private Integer tempoTotalParado = 0;
    private Double custoEstimado = 0.0;

    public Roteiro() {
    }

    public Roteiro(int id, Date data, int idMotorista, Double distanciaTotal, Integer tempoTotalParado, Double custoEstimado) {
        this.id = id;
        this.data = data;
        this.idMotorista = idMotorista;
        this.distanciaTotal = distanciaTotal;
        this.tempoTotalParado = tempoTotalParado;
        this.custoEstimado = custoEstimado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public int getIdMotorista() { return idMotorista; }
    public void setIdMotorista(int idMotorista) { this.idMotorista = idMotorista; }

    public Double getDistanciaTotal() { return distanciaTotal; }
    public void setDistanciaTotal(Double distanciaTotal) { this.distanciaTotal = distanciaTotal; }

    public Integer getTempoTotalParado() { return tempoTotalParado; }
    public void setTempoTotalParado(Integer tempoTotalParado) { this.tempoTotalParado = tempoTotalParado; }

    public Double getCustoEstimado() { return custoEstimado; }
    public void setCustoEstimado(Double custoEstimado) { this.custoEstimado = custoEstimado; }
}
