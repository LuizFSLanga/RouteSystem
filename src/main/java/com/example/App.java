package com.example;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import com.example.dao.DAO;
import com.example.model.Gerente;
import com.example.model.Motorista;
import com.example.model.Parametro;
import com.example.model.Ponto;
import com.example.model.Roteiro;

import com.example.service.GerenteService;
import com.example.service.MotoristaService;
import com.example.service.ParametroService;
import com.example.service.PontoService;
import com.example.service.RoteiroService;

import static spark.Spark.*;

public class App {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    public static void main(String[] args) {
        
        port(4567); 
        for (String pasta : new String[] {"src/main/webapp", "main/webapp", "webapp"}) {
            if (new java.io.File(pasta).isDirectory()) {
                staticFiles.externalLocation(pasta);
                System.out.println("Servindo front-end de: " + new java.io.File(pasta).getAbsolutePath());
                break;
            }
        }

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.type("application/json");
            res.body(erroJson(e.getMessage()));
        });
        exception(JsonParseException.class, (e, req, res) -> {
            res.status(400);
            res.type("application/json");
            res.body(erroJson("JSON inválido na requisição."));
        });
        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(500);
            res.type("application/json");
            res.body(erroJson("Erro interno do servidor."));
        });

        DAO dao = new DAO();
        dao.conectar();
        boolean dbOk = dao.testarConexao(); 

        MotoristaService motoristaService = new MotoristaService();
        RoteiroService roteiroService = new RoteiroService();
        GerenteService gerenteService = new GerenteService();
        ParametroService parametroService = new ParametroService();
        PontoService pontoService = new PontoService();

        get("/", (request, response) -> {
            response.redirect("index.html");
            return null;
        });

        get("/api/status", (request, response) -> {
            response.type("application/json");
            Map<String, Object> status = new HashMap<>();
            status.put("servidor", "ATIVO");
            status.put("conexao_db", dbOk ? "OK" : "FALHA");
            return gson.toJson(status);
        });

        get("/api/motoristas", (request, response) -> {
            response.type("application/json");
            return gson.toJson(motoristaService.listarTodos());
        });

        get("/api/motoristas/:id", (request, response) -> {
            response.type("application/json");
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(motoristaService.buscarPorId(id));
        });

        post("/api/motoristas", (request, response) -> {
            response.type("application/json");
            Motorista motorista = gson.fromJson(request.body(), Motorista.class);
            boolean sucesso = motoristaService.salvarMotorista(motorista);
            return responderSucessoOuErro(response, sucesso, "Motorista cadastrado com sucesso", "Erro ao cadastrar motorista");
        });

        put("/api/motoristas/:id", (request, response) -> {
            response.type("application/json");
            Motorista motorista = gson.fromJson(request.body(), Motorista.class);
            motorista.setId(Integer.parseInt(request.params(":id"))); // Garante que atualiza o id certo
            boolean sucesso = motoristaService.salvarMotorista(motorista);
            return responderSucessoOuErro(response, sucesso, "Motorista atualizado com sucesso", "Erro ao atualizar motorista");
        });

        delete("/api/motoristas/:id", (request, response) -> {
            response.type("application/json");
            int id = Integer.parseInt(request.params(":id"));
            boolean sucesso = motoristaService.excluirMotorista(id);
            return responderSucessoOuErro(response, sucesso, "Motorista excluído com sucesso", "Erro ao excluir motorista");
        });

        get("/api/gerentes", (request, response) -> {
            response.type("application/json");
            return gson.toJson(gerenteService.listarTodos());
        });

        post("/api/gerentes", (request, response) -> {
            response.type("application/json");
            Gerente gerente = gson.fromJson(request.body(), Gerente.class);
            boolean sucesso = gerenteService.salvarGerente(gerente);
            return responderSucessoOuErro(response, sucesso, "Gerente cadastrado com sucesso", "Erro ao cadastrar gerente");
        });

        delete("/api/gerentes/:id", (request, response) -> {
            response.type("application/json");
            int id = Integer.parseInt(request.params(":id"));
            boolean sucesso = gerenteService.excluirGerente(id);
            return responderSucessoOuErro(response, sucesso, "Gerente excluído", "Erro ao excluir");
        });

        get("/api/parametros", (request, response) -> {
            response.type("application/json");
            return gson.toJson(parametroService.getParametrosGlobais());
        });

        post("/api/parametros", (request, response) -> {
            response.type("application/json");
            Parametro parametro = gson.fromJson(request.body(), Parametro.class);
            boolean sucesso = parametroService.salvarParametro(parametro);
            return responderSucessoOuErro(response, sucesso, "Parâmetros atualizados", "Erro ao atualizar parâmetros");
        });

        get("/api/roteiros", (request, response) -> {
            response.type("application/json");
            return gson.toJson(roteiroService.listarTodos());
        });

        get("/api/roteiros/:id", (request, response) -> {
            response.type("application/json");
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(roteiroService.buscarPorId(id));
        });

        post("/api/roteiros", (request, response) -> {
            response.type("application/json");
            Roteiro roteiro = gson.fromJson(request.body(), Roteiro.class);
            boolean sucesso = roteiroService.salvarRoteiro(roteiro);
            return responderSucessoOuErro(response, sucesso, "Roteiro criado", "Erro ao criar roteiro");
        });

        delete("/api/roteiros/:id", (request, response) -> {
            response.type("application/json");
            int id = Integer.parseInt(request.params(":id"));
            boolean sucesso = roteiroService.excluirRoteiro(id);
            return responderSucessoOuErro(response, sucesso, "Roteiro excluído", "Erro ao excluir roteiro");
        });

        get("/api/roteiros/:id/pontos", (request, response) -> {
            response.type("application/json");
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(pontoService.listarPorRoteiro(id));
        });

        get("/api/pontos", (request, response) -> {
            response.type("application/json");
            return gson.toJson(pontoService.listarTodos());
        });

        get("/api/pontos/:id", (request, response) -> {
            response.type("application/json");
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(pontoService.buscarPorId(id));
        });

        post("/api/pontos", (request, response) -> {
            response.type("application/json");
            Ponto ponto = gson.fromJson(request.body(), Ponto.class);
            boolean sucesso = pontoService.salvarPonto(ponto);
            return responderSucessoOuErro(response, sucesso, "Ponto registrado com sucesso", "Erro ao registrar ponto");
        });

        put("/api/pontos/:id", (request, response) -> {
            response.type("application/json");
            Ponto ponto = gson.fromJson(request.body(), Ponto.class);
            ponto.setId(Integer.parseInt(request.params(":id")));
            boolean sucesso = pontoService.salvarPonto(ponto);
            return responderSucessoOuErro(response, sucesso, "Ponto atualizado com sucesso", "Erro ao atualizar ponto");
        });

        delete("/api/pontos/:id", (request, response) -> {
            response.type("application/json");
            int id = Integer.parseInt(request.params(":id"));
            boolean sucesso = pontoService.excluirPonto(id);
            return responderSucessoOuErro(response, sucesso, "Ponto excluído", "Erro ao excluir ponto");
        });

        post("/api/roteiros/:id/calcular", (request, response) -> {
            response.type("application/json");
            int idRoteiro = Integer.parseInt(request.params(":id"));
            
            boolean sucesso = roteiroService.fecharRoteiro(idRoteiro);
            return responderSucessoOuErro(response, sucesso, "Roteiro calculado e atualizado no banco com sucesso!", "Falha ao calcular o roteiro. Verifique os logs.");
        });

        awaitInitialization();
        System.out.println("Servidor Spark (Hermes Routes) iniciado na porta 4567. Acesse http://localhost:4567");
    }

    private static String erroJson(String mensagem) {
        Map<String, Object> r = new HashMap<>();
        r.put("status", "erro");
        r.put("mensagem", mensagem);
        return gson.toJson(r);
    }

    private static String responderSucessoOuErro(spark.Response response, boolean sucesso, String msgSucesso, String msgErro) {
        Map<String, Object> resultado = new HashMap<>();
        if (sucesso) {
            resultado.put("status", "sucesso");
            resultado.put("mensagem", msgSucesso);
        } else {
            response.status(400);
            resultado.put("status", "erro");
            resultado.put("mensagem", msgErro);
        }
        return gson.toJson(resultado);
    }
}