package com.example.service;

import java.util.List;

import com.example.dao.PontoDAO;
import com.example.dao.RoteiroDAO;
import com.example.model.Ponto;

public class PontoService {

    private final PontoDAO pontoDAO;
    private final RoteiroDAO roteiroDAO;
    private final RoteiroService roteiroService;

    public PontoService() {
        this.pontoDAO = new PontoDAO();
        this.roteiroDAO = new RoteiroDAO();
        this.roteiroService = new RoteiroService();
    }

    private void recalcularRoteiro(int idRoteiro) {
        try {
            roteiroService.recalcular(idRoteiro);
        } catch (Exception e) {
            System.err.println("Aviso: não foi possível recalcular o roteiro " + idRoteiro + ": " + e.getMessage());
        }
    }

    public static int calcularMinutos(Ponto p) {
        if (p.getOrdemNoRoteiro() == null || p.getOrdemNoRoteiro() <= 1) return 0;
        if (p.getHorarioChegada() == null || p.getHorarioSaida() == null) return 0;
        long diff = p.getHorarioSaida().getTime() - p.getHorarioChegada().getTime();
        return (int) Math.max(0, diff / 60000);
    }

    private void validar(Ponto p) {
        if (p == null) {
            throw new IllegalArgumentException("Dados do ponto não informados.");
        }
        if (p.getEndereco() == null || p.getEndereco().trim().isEmpty()) {
            throw new IllegalArgumentException("O endereço do ponto é obrigatório.");
        }
        if (roteiroDAO.get(p.getIdRoteiro()) == null) {
            throw new IllegalArgumentException("O roteiro informado não existe.");
        }
        if (p.getLatitude() < -90 || p.getLatitude() > 90 || p.getLongitude() < -180 || p.getLongitude() > 180) {
            throw new IllegalArgumentException("Coordenadas inválidas.");
        }
        Integer ordem = p.getOrdemNoRoteiro();
        if (ordem == null || ordem < 1) {
            throw new IllegalArgumentException("A ordem no roteiro deve ser 1 ou maior.");
        }
        // RN06: ordem sequencial única dentro do roteiro
        for (Ponto outro : pontoDAO.getPontosPorRoteiro(p.getIdRoteiro())) {
            if (outro.getId() != p.getId() && ordem.equals(outro.getOrdemNoRoteiro())) {
                throw new IllegalArgumentException("Já existe o ponto " + ordem + " neste roteiro.");
            }
        }
        if (p.getHorarioChegada() == null) {
            throw new IllegalArgumentException("O horário de chegada é obrigatório.");
        }
        if (p.getHorarioSaida() != null && p.getHorarioSaida().before(p.getHorarioChegada())) {
            throw new IllegalArgumentException("A saída não pode ser anterior à chegada.");
        }
    }

    public boolean salvarPonto(Ponto ponto) {
        validar(ponto);
        ponto.setTempoParadoCalculado(calcularMinutos(ponto));
        Ponto antigo = ponto.getId() > 0 ? pontoDAO.get(ponto.getId()) : null;
        boolean ok = ponto.getId() > 0 ? pontoDAO.update(ponto) : pontoDAO.insert(ponto);
        if (ok) {
            recalcularRoteiro(ponto.getIdRoteiro());
            if (antigo != null && antigo.getIdRoteiro() != ponto.getIdRoteiro()) {
                recalcularRoteiro(antigo.getIdRoteiro()); // ponto mudou de roteiro
            }
        }
        return ok;
    }

    public Ponto buscarPorId(int id) {
        return pontoDAO.get(id);
    }

    public List<Ponto> listarTodos() {
        return pontoDAO.getAll();
    }

    public List<Ponto> listarPorRoteiro(int idRoteiro) {
        return pontoDAO.getPontosPorRoteiro(idRoteiro);
    }

    public boolean excluirPonto(int id) {
        Ponto existente = pontoDAO.get(id);
        boolean ok = pontoDAO.remove(id);
        if (ok && existente != null) {
            recalcularRoteiro(existente.getIdRoteiro());
        }
        return ok;
    }
}
