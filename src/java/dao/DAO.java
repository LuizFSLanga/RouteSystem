package dao;

import java.sql.*;

public class DAO {
	protected Connection conexao;

	public DAO() {
		conexao = null;
	}
	
	public boolean conectar() {
		String driverName = "org.postgresql.Driver";                    
		String serverName = "loop.postgres.database.azure.com";
		String mydatabase = "";
		int porta = 5432;
		String url = "jdbc:postgresql://" + serverName + ":" + porta +"/" + mydatabase + "?sslmode=require";
		String username = "";
		String password = "";
		boolean status = false;

		try {
			Class.forName(driverName);
			conexao = DriverManager.getConnection(url, username, password);
			status = (conexao != null);
			System.out.println("Conexão efetuada com o postgres!");
		} catch (ClassNotFoundException e) { 
			System.err.println("Conexão NÃO efetuada com o postgres -- Driver não encontrado -- " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Conexão NÃO efetuada com o postgres -- " + e.getMessage());
		}

		return status;
	}

	public boolean testarConexao() {
		try {
            if (conexao != null && !conexao.isClosed() && conexao.isValid(5)) {
                System.out.println("STATUS: Conexão com o PostgreSQL no Azure O.K.");
                return true;
            } else {
                System.err.println("STATUS: Conexão inativa ou não estabelecida.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("ERRO FATAL: Falha ao verificar a conexão com o PostgreSQL.");
            System.err.println("Detalhes do erro: " + e.getMessage());
            return false;
        }
    }
	
	public boolean close() {
		boolean status = false;
		
		try {
			conexao.close();
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}
}