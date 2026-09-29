package com.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Roteiro;

public class RoteiroDAO extends DAO {

    public boolean insert(Roteiro roteiro) {

        String sql = "INSERT INTO Roteiro (data, motorista_id, distancia_total, tempo_total_parado, custo_estimado) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setDate(1, roteiro.getData());
            pst.setInt(2, roteiro.getMotorista());
            pst.setDouble(3, roteiro.getDistanciaTotal());
            pst.setTimestamp(4, roteiro.getTempoTotalParado());
            pst.setDouble(5, roteiro.getCustoEstimado());

            if (pst.executeUpdate() > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        roteiro.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir roteiro: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public Roteiro get(int idRoteiro) {

        Roteiro roteiro = null;
        String sql = "SELECT * FROM Roteiro WHERE id = ?";

        try (PreparedStatement pst = conexao.prepareStatement(sql)) {
            
            pst.setInt(1, idRoteiro);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    roteiro = new Roteiro(rs.getInt("id"),
                                          rs.getDate("data"),
                                          rs.getInt("motorista_id"),
                                          rs.getDouble("distancia_total"),
                                          rs.getTimestamp("tempo_total_parado"),
                                          rs.getDouble("custo_estimado")
                                         );
                }

            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar roteiro: " + e.getMessage());
            e.printStackTrace();
        }

        return roteiro;

    }

    public List<Roteiro> getAll() {

        List<Roteiro> listaRoteiro = new ArrayList<>();
        
        String sql = "SELECT * FROM Roteiro ORDER BY id";

        try (PreparedStatement pst = conexao.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                    Roteiro roteiro = new Roteiro(rs.getInt("id"),
                                                  rs.getDate("data"),
                                                  rs.getInt("motorista_id"),
                                                  rs.getDouble("distancia_total"),
                                                  rs.getTimestamp("tempo_total_parado"),
                                                  rs.getDouble("custo_estimado")
                                                 );
                    listaRoteiro.add(roteiro);
            }

        } catch (SQLException e) {
            System.err.println("--- ERRO NA LISTAGEM DE ROTEIROS (SQL) ---");
            System.err.println("Detalhes do erro: " + e.getMessage());
            e.printStackTrace();
            listaRoteiro.clear();
        }

        return listaRoteiro;

    }

    public List<Roteiro> getRoteirosPorMotorista(int idMotorista){

        List<Roteiro> listaRoteiros = new ArrayList<>();

        String sql = "SELECT * FROM Roteiro WHERE motorista_id = ? ORDER BY id DESC";

        try (PreparedStatement pst = conexao.prepareStatement(sql)) {

            pst.setInt(1, idMotorista);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    Roteiro roteiro = new Roteiro(rs.getInt("id"),
                                                  rs.getDate("data"),
                                                  rs.getInt("motorista_id"),
                                                  rs.getDouble("distancia_total"),
                                                  rs.getTimestamp("tempo_total_parado"),
                                                  rs.getDouble("custo_estimado")
                                                 );
                    listaRoteiros.add(roteiro);
                }

            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos por vendedor: " + e.getMessage());
            e.printStackTrace();
        }

        return listaRoteiros;

    }

    public boolean update(Roteiro roteiro) {

        String sql = "UPDATE Roteiro SET data = ?, motorista_id = ?, distancia_total = ?, tempo_total_parado = ?, custo_estimado = ? WHERE id = ?";

        try (PreparedStatement pst = conexao.prepareStatement(sql)) {

            pst.setDate(1, roteiro.getData());
            pst.setInt(2, roteiro.getMotorista());
            pst.setDouble(3, roteiro.getDistanciaTotal());
            pst.setTimestamp(4, roteiro.getTempoTotalParado());
            pst.setDouble(5, roteiro.getCustoEstimado());
            pst.setInt(6, roteiro.getId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERRO ao atualizar poteiro: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public boolean remove(int id) {

        String sql = "DELETE FROM Roteiro WHERE id = ?";

        try (PreparedStatement pst = conexao.prepareStatement(sql)) {

            pst.setInt(1, id);

            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("ERRO ao remover roteiro: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

}
