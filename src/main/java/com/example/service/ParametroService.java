package com.example.service;

import com.example.dao.ParametroDAO;
import com.example.model.Parametro;
import java.util.List;

public class ParametroService {
    
    private ParametroDAO parametroDAO;

    public ParametroService() {
        this.parametroDAO = new ParametroDAO();
    }

    public boolean salvarParametro(Parametro parametro) {
        if (parametro.getValorCombustivel() == null || parametro.getValorCombustivel() <= 0) {
            throw new IllegalArgumentException("O valor do combustível deve ser maior que zero.");
        }
        if (parametro.getKmLitro() == null || parametro.getKmLitro() <= 0) {
            throw new IllegalArgumentException("O km/litro padrão deve ser maior que zero.");
        }
        if (parametro.getCustoPorKm() == null || parametro.getCustoPorKm() < 0) {
            throw new IllegalArgumentException("O custo por km não pode ser negativo.");
        }
        if (parametro.getJornadaPadraoHoras() != null && !parametro.getJornadaPadraoHoras().trim().isEmpty()) {
            try {
                double h = Double.parseDouble(parametro.getJornadaPadraoHoras().trim().replace(',', '.'));
                if (h <= 0 || h > 24) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("A jornada padrão deve ser um número de horas entre 0 e 24.");
            }
        }

        if (parametro.getId() > 0) {
            return parametroDAO.update(parametro);
        } else {
            return parametroDAO.insert(parametro);
        }
    }

    public Parametro getParametrosGlobais() {
        Parametro param = parametroDAO.get(1);
        if (param == null) {
            System.err.println("Aviso: Parâmetros globais não encontrados no ID 1.");
        }
        return param;
    }

    public List<Parametro> listarTodos() {
        return parametroDAO.getAll();
    }
}