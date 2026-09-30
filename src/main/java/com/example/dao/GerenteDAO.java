package com.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Gerente;

public class GerenteDAO extends DAO {
    
	public GerenteDAO() {
		super();
	}

	public boolean insert(Gerente gerente) {

        String sql = "INSERT INTO Gerente (nome, telefone, email, equipe_sob_responsabilidade) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = conexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, gerente.getNome());
            pst.setString(2, gerente.getTelefone());
            pst.setString(3, gerente.getEmail());
            pst.setString(4, gerente.getequipeSobResponsabilidade());

            if (pst.executeUpdate() > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        gerente.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir gerente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Gerente get(int idGerente) {

        Gerente gerente = null;
        String sql = "SELECT * FROM Gerente WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {
            
            pst.setInt(1, idGerente);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    gerente = new Gerente(rs.getInt("id"),
                                          rs.getString("nome"), 
                                          rs.getString("telefone"), 
                                          rs.getString("email"),
                                          rs.getString("equipe_sob_responsabilidade")
                                         );
                }

            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar gerente: " + e.getMessage());
            e.printStackTrace();
        }
        return gerente;

    }

    public List<Gerente> getAll() {

        List<Gerente> listaGerente = new ArrayList<>();
        
        String sql = "SELECT * FROM Gerente ORDER BY id";

        try (PreparedStatement pst = conexao().prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                    Gerente gerente = new Gerente(rs.getInt("id"),
                                                  rs.getString("nome"), 
                                                  rs.getString("telefone"), 
                                                  rs.getString("email"),
                                                  rs.getString("equipe_sob_responsabilidade")
                                                 );
                    listaGerente.add(gerente);
            }

        } catch (SQLException e) {
            System.err.println("--- ERRO NA LISTAGEM DE GERENTES (SQL) ---");
            System.err.println("Detalhes do erro: " + e.getMessage());
            e.printStackTrace();
            listaGerente.clear();
        }
        return listaGerente;

    }

    public boolean update(Gerente gerente) {

        String sql = "UPDATE Gerente SET nome = ?, telefone = ?, email = ?, equipe_sob_responsabilidade = ? WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {

            pst.setString(1, gerente.getNome());
            pst.setString(2, gerente.getTelefone());
            pst.setString(3, gerente.getEmail());
            pst.setString(4, gerente.getequipeSobResponsabilidade());
            pst.setInt(5, gerente.getId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERRO ao atualizar gerente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;

    }

    public boolean remove(int id) {

        String sql = "DELETE FROM Gerente WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {

            pst.setInt(1, id);

            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("ERRO ao remover gerente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;

    }

}
