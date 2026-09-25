package app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import dao.DAO;
import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;



public class Aplicacao {

    private static Gson gson = new Gson();

    public static void main(String[] args) {
        
        
        port(4567); 
       
        staticFiles.location("/html"); 
        
        DAO dao = new DAO();
        
        boolean dbOk = dao.testarConexao(); 

        
        
        get("/", (request, response) -> {
            
            response.redirect("/homepage/index.html");
            response.redirect("/admin/admin.html");
            return null;
        });
        
        
        get("/api/status", (request, response) -> {
            response.type("application/json");
            Map<String, Object> status = new HashMap<>();
            status.put("servidor", "ATIVO");
            status.put("conexao_db", dbOk ? "OK" : "FALHA");
            return gson.toJson(status);
        });

        
        get("/api/produtos/listar", (request, response) -> {
            response.type("application/json");
            
           
            if (!dbOk) {
                 response.status(503); 
                 return gson.toJson(new StatusResponse("Banco de dados indisponível na inicialização.", false));
            }
            
            try {
              
                List<Produto> produtos = produtoService.listarProdutos(); 
                response.status(200);
                return gson.toJson(produtos);
            } catch (Exception e) {
               
                response.status(500);
                System.err.println("Erro ao executar listarProdutos (Provavelmente erro de SQL no DAO): " + e.getMessage());
                return gson.toJson(new StatusResponse("Erro interno ao buscar produtos: " + e.getMessage(), false));
            }
        });
        
       

        awaitInitialization();
        System.out.println("Servidor Spark (LOOP E-COMMERCE) iniciado na porta 4567. Acesse http://localhost:4567");
    }

    
    private static class StatusResponse {
        String message;
        boolean success;

        public StatusResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
    }
}
