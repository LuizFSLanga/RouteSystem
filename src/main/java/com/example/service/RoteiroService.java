package com.example.service;

import java.util.List;

import com.example.dao.MotoristaDAO;
import com.example.dao.ParametroDAO;
import com.example.dao.PontoDAO;
import com.example.dao.RoteiroDAO;
import com.example.model.Motorista;
import com.example.model.Parametro;
import com.example.model.Ponto;
import com.example.model.Roteiro;

public class RoteiroService {

    private final RoteiroDAO roteiroDAO;
    private final PontoDAO pontoDAO;
    private final ParametroDAO parametroDAO;
    private final MotoristaDAO motoristaDAO;

    public RoteiroService() {
        this.roteiroDAO = new RoteiroDAO();
        this.pontoDAO = new PontoDAO();
        this.parametroDAO = new ParametroDAO();
        this.motoristaDAO = new MotoristaDAO();
    }

    public boolean salvarRoteiro(Roteiro roteiro) {
        if (roteiro == null || roteiro.getIdMotorista() <= 0) {
            throw new IllegalArgumentException("O roteiro precisa estar vinculado a um motorista válido.");
        }
        if (roteiro.getData() == null) {
            throw new IllegalArgumentException("O roteiro precisa de uma data (RN05).");
        }
        if (motoristaDAO.get(roteiro.getIdMotorista()) == null) {
            throw new IllegalArgumentException("O motorista informado não existe.");
        }
        if (roteiro.getId() > 0) {
            return roteiroDAO.update(roteiro);
        } else {
            return roteiroDAO.insert(roteiro);
        }
    }

    public Roteiro buscarPorId(int id) {
        return roteiroDAO.get(id);
    }

    public List<Roteiro> listarTodos() {
        return roteiroDAO.getAll();
    }

    public List<Roteiro> listarPorMotorista(int idMotorista) {
        return roteiroDAO.getRoteirosPorMotorista(idMotorista);
    }

    public boolean excluirRoteiro(int id) {
        return roteiroDAO.remove(id);
    }

    /** Botão "Calcular": recalcula e devolve erro claro se faltar algo (sem pontos, sem parâmetros). */
    public boolean fecharRoteiro(int idRoteiro) {
        Roteiro roteiro = roteiroDAO.get(idRoteiro);
        if (roteiro == null) {
            throw new IllegalArgumentException("Roteiro não encontrado.");
        }
        List<Ponto> pontos = pontoDAO.getPontosPorRoteiro(idRoteiro);
        if (pontos == null || pontos.isEmpty()) {
            throw new IllegalArgumentException("Este roteiro ainda não tem pontos. Cadastre ao menos um ponto antes de calcular.");
        }
        return calcular(roteiro, pontos, true);
    }

    public void recalcular(int idRoteiro) {
        Roteiro roteiro = roteiroDAO.get(idRoteiro);
        if (roteiro == null) return;
        List<Ponto> pontos = pontoDAO.getPontosPorRoteiro(idRoteiro);
        if (pontos == null || pontos.isEmpty()) {
            roteiro.setDistanciaTotal(0.0);
            roteiro.setTempoTotalParado(0);
            roteiro.setCustoEstimado(0.0);
            roteiroDAO.update(roteiro);
            return;
        }
        calcular(roteiro, pontos, false);
    }

    private boolean calcular(Roteiro roteiro, List<Ponto> pontos, boolean estrito) {
        double distanciaTotalKm = 0.0;
        int tempoTotalMin = 0;

        for (int i = 0; i < pontos.size(); i++) {
            Ponto atual = pontos.get(i);

            int minutos = PontoService.calcularMinutos(atual);
            if (minutos != atual.getTempoParadoCalculado()) {
                atual.setTempoParadoCalculado(minutos);
                pontoDAO.update(atual);
            }
            tempoTotalMin += minutos; // RN03

            if (i < pontos.size() - 1) {
                Ponto proximo = pontos.get(i + 1);
                distanciaTotalKm += calcularDistanciaHaversine(
                        atual.getLatitude(), atual.getLongitude(),
                        proximo.getLatitude(), proximo.getLongitude());
            }
        }

        roteiro.setDistanciaTotal(arredondar(distanciaTotalKm));
        roteiro.setTempoTotalParado(tempoTotalMin);

        // RN07: rendimento do motorista tem prioridade; se não houver, usa o km/L padrão dos parâmetros
        Parametro param = parametroDAO.get(1);
        Motorista motorista = motoristaDAO.get(roteiro.getIdMotorista());
        double kmPorLitro = (motorista != null && motorista.getRendimentoKmLitro() > 0)
                ? motorista.getRendimentoKmLitro()
                : (param != null && param.getKmLitro() != null ? param.getKmLitro() : 0);

        if (param == null || kmPorLitro <= 0) {
            if (estrito) {
                throw new IllegalArgumentException("Não foi possível calcular o custo: configure os parâmetros (combustível e km/L) ou o rendimento do motorista.");
            }
            roteiro.setCustoEstimado(0.0);
        } else {
            double litros = distanciaTotalKm / kmPorLitro;
            double custoCombustivel = litros * (param.getValorCombustivel() == null ? 0 : param.getValorCombustivel());
            double custoPorKm = param.getCustoPorKm() == null ? 0 : param.getCustoPorKm();
            roteiro.setCustoEstimado(arredondar(custoCombustivel + distanciaTotalKm * custoPorKm));
        }

        return roteiroDAO.update(roteiro);
    }

    private double arredondar(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int RAIO_TERRA_KM = 6371;
        double distLat = Math.toRadians(lat2 - lat1);
        double distLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(distLat / 2) * Math.sin(distLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(distLon / 2) * Math.sin(distLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA_KM * c;
    }
}
