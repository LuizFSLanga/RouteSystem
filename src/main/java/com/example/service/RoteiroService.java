package com.example.service;

import java.util.List;

import com.example.dao.RoteiroDAO;
import com.example.model.Parametro;
import com.example.model.Ponto;
import com.example.model.Roteiro;
import com.example.dao.PontoDAO;
import com.example.dao.ParametroDAO;

public class RoteiroService {

    private RoteiroDAO roteiroDAO;
    private PontoDAO pontoDAO;
    private ParametroDAO parametroDAO;

    public RoteiroService() {

        this.roteiroDAO = new RoteiroDAO();
        this.pontoDAO = new PontoDAO();
        this.parametroDAO = new ParametroDAO();

    }

    public boolean fecharRoteiro(int idRoteiro) {

        Roteiro roteiro = roteiroDAO.get(idRoteiro);

        if(roteiro == null) {
            System.err.println("Roteiro não encontrado.");
            return false;
        }

        List<Ponto> pontos = pontoDAO.getPontosPorRoteiro(idRoteiro);

        if (pontos == null || pontos.isEmpty()) {
            System.err.println("Nenhum ponto registrado para este roteiro.");
            return false;
        }

        Parametro parametros = parametroDAO.get(1); 
        
        double distanciaTotalKm = 0.0;
        long tempoTotalParadoMillis = 0;

        for (int i = 0; i < pontos.size(); i++) {

            Ponto pontoAtual = pontos.get(i);

            if (pontoAtual.getHorarioChegada() != null && pontoAtual.getHorarioSaida() != null) {

                long chegadaMillis = pontoAtual.getHorarioChegada().getTime();
                long saidaMillis = pontoAtual.getHorarioSaida().getTime();
                long diferencaMillis = saidaMillis - chegadaMillis;
                
                int minutosParado = (int) (diferencaMillis / (1000 * 60));

                pontoAtual.setTempoParadoCalculado(minutosParado);
                pontoDAO.update(pontoAtual); // Atualiza no banco
                
                tempoTotalParadoMillis += diferencaMillis;
            }

            if (i < pontos.size() - 1) {

                Ponto proximoPonto = pontos.get(i + 1);

                distanciaTotalKm += calcularDistanciaHaversine(
                    pontoAtual.getLatitude(), pontoAtual.getLongitude(),
                    proximoPonto.getLatitude(), proximoPonto.getLongitude()
                );

            }

        }

        roteiro.setDistanciaTotal(distanciaTotalKm);
   
        roteiro.setTempoTotalParado(new java.sql.Timestamp(tempoTotalParadoMillis));

        if (parametros != null) {

            double litrosConsumidos = distanciaTotalKm / parametros.getKmLitro();
            double custoCombustivel = litrosConsumidos * parametros.getValorCombustivel();
            double custoDesgaste = distanciaTotalKm * parametros.getCustoPorKm();
            
            roteiro.setCustoEstimado(custoCombustivel + custoDesgaste);

        }

        return roteiroDAO.update(roteiro);

    }

    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {

        int RAIO_TERRA_KM = 6371;
        double distLat = Math.toRadians(lat2 - lat1);
        double distLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(distLat / 2) * Math.sin(distLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(distLon / 2) * Math.sin(distLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_KM * c;

    }

}
