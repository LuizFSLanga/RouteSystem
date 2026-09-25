package model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Ponto {
    private int id;
    private String endereco;
    private double latitude;
    private double longitude;
    private Roteiro ordemNoRoteiro;
    private LocalDateTime horarioChegada;
    private LocalDateTime horarioSaida;
    private LocalTime tempoParadoCalculado;

    public Ponto() {
    }

    public String getEndereco() { return endereco; }    
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Roteiro getOrdemNoRoteiro() { return ordemNoRoteiro; }
    public void setOrdemNoRoteiro(Roteiro ordemNoRoteiro) { this.ordemNoRoteiro = ordemNoRoteiro; }

    public LocalDateTime getHorarioChegada() { return horarioChegada; }
    public void setHorarioChegada(LocalDateTime horarioChegada) { this.horarioChegada = horarioChegada; }

    public LocalDateTime getHorarioSaida() { return horarioSaida; }
    public void setHorarioSaida(LocalDateTime horarioSaida) { this.horarioSaida = horarioSaida; }

    public LocalTime getTempoParadoCalculado() { return tempoParadoCalculado; }
    public void setTempoParadoCalculado(LocalTime tempoParadoCalculado) { this.tempoParadoCalculado = tempoParadoCalculado; }

    @Override
    public String toString() {
        return "Ponto [id=" + id + ", endereco=" + endereco + ", latitude=" + latitude + ", longitude=" + longitude + ", ordemNoRoteiro=" + ordemNoRoteiro + ", horarioChegada=" + horarioChegada + ", horarioSaida=" + horarioSaida + ", tempoParadoCalculado=" + tempoParadoCalculado + "]";
    }
}