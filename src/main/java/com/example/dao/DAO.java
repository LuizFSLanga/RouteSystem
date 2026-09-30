package com.example.dao;

import java.sql.*;

public class DAO {

	protected Connection conexao;

	public DAO() {
		conexao = null;
		conectar();
	}

	private static String env(String chave, String padrao) {
		String v = System.getenv(chave);
		return (v == null || v.isEmpty()) ? padrao : v;
	}

	public boolean conectar() {
		try {
			if (conexao != null && !conexao.isClosed()) {
				return true;
			}
		} catch (SQLException e) {
		}

		String driverName = "org.postgresql.Driver";
		String url = "jdbc:postgresql://" + env("DB_HOST", "localhost") + ":" + env("DB_PORT", "5432")
				+ "/" + env("DB_NAME", "RouteSystem");
		String username = env("DB_USER", "RouteSystem");
		String password = env("DB_PASSWORD", "Rs@1234");
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

	protected synchronized Connection conexao() throws SQLException {
		try {
			if (conexao == null || conexao.isClosed() || !conexao.isValid(2)) {
				conexao = null;
				conectar();
			}
		} catch (SQLException e) {
			conexao = null;
			conectar();
		}
		if (conexao == null) {
			throw new SQLException("Sem conexão com o banco de dados.");
		}
		return conexao;
	}

	public boolean testarConexao() {
		try {
			if (conexao != null && !conexao.isClosed() && conexao.isValid(5)) {
				System.out.println("STATUS: Conexão com o PostgreSQL O.K.");
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
			if (conexao != null) conexao.close();
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}

}
