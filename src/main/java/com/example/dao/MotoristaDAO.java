package com.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Motorista;

public class MotoristaDAO extends DAO {

    public boolean insert(Motorista motorista) {

        String sql = "INSERT INTO motorista (nome, telefone, documento, veiculo, rendimento_km_litro) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, motorista.getNome());
            pst.setString(2, motorista.getTelefone());
            pst.setString(3, motorista.getDocumento());
            pst.setString(4, motorista.getVeiculo());
            pst.setDouble(5, motorista.getRendimentoKmLitro());

            if (pst.executeUpdate() > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        motorista.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir motorista: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public Motorista get(int idMotorista) {

        Motorista motorista = null;
        String sql = "SELECT * FROM motorista WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {
            
            pst.setInt(1, idMotorista);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    motorista = new Motorista(rs.getInt("id"),
                                              rs.getString("nome"),
                                              rs.getString("telefone"),
                                              rs.getString("documento"),
                                              rs.getString("veiculo"),
                                              rs.getDouble("rendimento_km_litro")
                                             );
                }

            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar motorista: " + e.getMessage());
            e.printStackTrace();
        }

        return motorista;

    }

    public List<Motorista> getAll() {

        List<Motorista> listaMotorista = new ArrayList<>();
        
        String sql = "SELECT * FROM motorista ORDER BY id";

        try (PreparedStatement pst = conexao().prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                    Motorista motorista = new Motorista(rs.getInt("id"),
                                                        rs.getString("nome"),
                                                        rs.getString("telefone"),
                                                        rs.getString("documento"),
                                                        rs.getString("veiculo"),
                                                        rs.getDouble("rendimento_km_litro")
                                                       );
                    listaMotorista.add(motorista);
            }

        } catch (SQLException e) {
            System.err.println("--- ERRO NA LISTAGEM DE MOTORISTAS (SQL) ---");
            System.err.println("Detalhes do erro: " + e.getMessage());
            e.printStackTrace();
            listaMotorista.clear();
        }

        return listaMotorista;

    }

    public boolean update(Motorista motorista) {

        String sql = "UPDATE motorista SET nome = ?, telefone = ?, documento = ?, veiculo = ?, rendimento_km_litro = ? WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {

            pst.setString(1, motorista.getNome());
            pst.setString(2, motorista.getTelefone());
            pst.setString(3, motorista.getDocumento());
            pst.setString(4, motorista.getVeiculo());
            pst.setDouble(5, motorista.getRendimentoKmLitro());
            pst.setInt(6, motorista.getId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERRO ao atualizar motorista: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public boolean remove(int id) {

        String sql = "DELETE FROM motorista WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {

            pst.setInt(1, id);

            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("ERRO ao remover motorista: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

}
