package com.example;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import com.example.dao.DAO;

import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class App {

    private static Gson gson = new Gson();

    public static void main(String[] args) {
        
        port(4567); 
       
        staticFiles.externalLocation("src/main/webapp/"); 
        
        DAO dao = new DAO();
        dao.conectar(); 
        
        boolean dbOk = dao.testarConexao(); 
        
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

        awaitInitialization();
        System.out.println("Servidor Spark (Hemes Route) iniciado na porta 4567. Acesse http://localhost:4567");

    }
    /* 
    private static class StatusResponse {
        String message;
        boolean success;

        public StatusResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
    }*/
}
