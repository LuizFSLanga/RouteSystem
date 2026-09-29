package com.example.model;

//import java.time.LocalDate;
import java.sql.Date;
//import java.time.LocalTime;
import java.sql.Timestamp;

public class Roteiro {
    private int id;
    private Date data;
    private int idMotorista;
    private Double distanciaTotal;
    private Timestamp tempoTotalParado;
    private Double custoEstimado;

    public Roteiro() {
    }

    public Roteiro(int id, Date data, int idMotorista, Double distanciaTotal, Timestamp tempoTotalParado, Double custoEstimado) {

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

    public int getMotorista() { return idMotorista; }
    public void setMotorista(int idMotorista) { this.idMotorista = idMotorista; }

    public Double getDistanciaTotal() { return distanciaTotal; }
    public void setDistanciaTotal(Double distanciaTotal) { this.distanciaTotal = distanciaTotal; }

    public Timestamp getTempoTotalParado() { return tempoTotalParado; }
    public void setTempoTotalParado(Timestamp tempoTotalParado) { this.tempoTotalParado = tempoTotalParado; }

    public Double getCustoEstimado() { return custoEstimado; }
    public void setCustoEstimado(Double custoEstimado) { this.custoEstimado = custoEstimado; }
}