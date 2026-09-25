package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Roteiro {
    private int id;
    private LocalDate data;
    private float distanciaTotal;
    private LocalTime tempoTotalParado;
    private float custoEstimado;

    public Roteiro() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public float getDistanciaTotal() { return distanciaTotal; }
    public void setDistanciaTotal(float distanciaTotal) { this.distanciaTotal = distanciaTotal; }

    public LocalTime getTempoTotalParado() { return tempoTotalParado; }
    public void setTempoTotalParado(LocalTime tempoTotalParado) { this.tempoTotalParado = tempoTotalParado; }

    public float getCustoEstimado() { return custoEstimado; }
    public void setCustoEstimado(float custoEstimado) { this.custoEstimado = custoEstimado; }

    @Override
    public String toString() {
        return "Roteiro [id=" + id + ", data=" + data + ", distanciaTotal=" + distanciaTotal + ", tempoTotalParado=" + tempoTotalParado + ", custoEstimado=" + custoEstimado + "]";
    }
}