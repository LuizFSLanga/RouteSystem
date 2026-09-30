package com.example.service;

import com.example.dao.MotoristaDAO;
import com.example.model.Motorista;
import java.util.List;

public class MotoristaService {

    private MotoristaDAO motoristaDAO;

    public MotoristaService() {
        this.motoristaDAO = new MotoristaDAO();
    }

    public boolean salvarMotorista(Motorista motorista) {
        if (motorista == null) {
            throw new IllegalArgumentException("Dados do motorista não informados.");
        }
        if (motorista.getNome() == null || motorista.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do motorista é obrigatório.");
        }
        if (motorista.getDocumento() == null || motorista.getDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("O documento (CNH/CPF) é obrigatório.");
        }
        if (motorista.getRendimentoKmLitro() <= 0) {
            throw new IllegalArgumentException("O rendimento do veículo deve ser maior que zero (km/L).");
        }
        String doc = motorista.getDocumento().trim();
        for (Motorista outro : motoristaDAO.getAll()) {
            if (outro.getId() != motorista.getId() && doc.equals(outro.getDocumento())) {
                throw new IllegalArgumentException("Já existe um motorista com este documento.");
            }
        }
        motorista.setDocumento(doc);

        if (motorista.getId() > 0) {
            return motoristaDAO.update(motorista);
        } else {
            return motoristaDAO.insert(motorista);
        }
    }

    public Motorista buscarPorId(int id) {
        return motoristaDAO.get(id);
    }

    public List<Motorista> listarTodos() {
        return motoristaDAO.getAll();
    }

    public boolean excluirMotorista(int id) {
        return motoristaDAO.remove(id);
    }
}
