package com.example.service;

import com.example.dao.GerenteDAO;
import com.example.model.Gerente;
import java.util.List;

public class GerenteService {

    private GerenteDAO gerenteDAO;

    public GerenteService() {
        this.gerenteDAO = new GerenteDAO();
    }

    public boolean salvarGerente(Gerente gerente) {
        if (gerente == null) {
            throw new IllegalArgumentException("Dados do gerente não informados.");
        }
        if (gerente.getNome() == null || gerente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do gerente é obrigatório.");
        }
        String email = gerente.getEmail() == null ? "" : gerente.getEmail().trim();
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        gerente.setEmail(email);

        if (gerente.getId() > 0) {
            return gerenteDAO.update(gerente);
        } else {
            return gerenteDAO.insert(gerente);
        }
    }

    public Gerente buscarPorId(int id) {
        return gerenteDAO.get(id);
    }

    public List<Gerente> listarTodos() {
        return gerenteDAO.getAll();
    }

    public boolean excluirGerente(int id) {
        return gerenteDAO.remove(id);
    }
}
