package com.example.model;

//import java.time.LocalDateTime;
//import java.time.LocalTime;
import java.sql.Timestamp;

public class Ponto {
    private int id;
    private int idRoteiro;
    private String endereco;
    private double latitude;
    private double longitude;
    private Integer ordemNoRoteiro;
    private Timestamp horarioChegada;
    private Timestamp horarioSaida;
    private int tempoParadoCalculado;

    public Ponto() {
    }

    public Ponto( int id, int idRoteiro, String endereco, double latitude, double longitude, Integer ordemNoRoteiro, Timestamp horarioChegada, Timestamp horarioSaida, int tempoParadoCalculado) {

        this.id = id;
        this.idRoteiro = idRoteiro;
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ordemNoRoteiro = ordemNoRoteiro;
        this.horarioChegada = horarioChegada;
        this.horarioSaida = horarioSaida;
        this.tempoParadoCalculado = tempoParadoCalculado;

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdRoteiro() { return idRoteiro; }
    public void setIdRoteiro(int id) { this.id = idRoteiro; }

    public String getEndereco() { return endereco; }    
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Integer getOrdemNoRoteiro() { return ordemNoRoteiro; }
    public void setOrdemNoRoteiro(Integer ordemNoRoteiro) { this.ordemNoRoteiro = ordemNoRoteiro; }

    public Timestamp getHorarioChegada() { return horarioChegada; }
    public void setHorarioChegada(Timestamp horarioChegada) { this.horarioChegada = horarioChegada; }

    public Timestamp getHorarioSaida() { return horarioSaida; }
    public void setHorarioSaida(Timestamp horarioSaida) { this.horarioSaida = horarioSaida; }

    public int getTempoParadoCalculado() { return tempoParadoCalculado; }
    public void setTempoParadoCalculado(int tempoParadoCalculado) { this.tempoParadoCalculado = tempoParadoCalculado; }
}