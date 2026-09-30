package com.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Ponto;

public class PontoDAO extends DAO {

    public PontoDAO() {
    }

    public boolean insert(Ponto ponto) {

        String sql = "INSERT INTO Ponto (roteiro_id, endereco, latitude, longitude, ordem_no_roteiro, data_hora_chegada, data_hora_saida, tempo_parado_calculado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, ponto.getIdRoteiro());
            pst.setString(2, ponto.getEndereco());
            pst.setDouble(3, ponto.getLatitude());
            pst.setDouble(4, ponto.getLongitude());
            pst.setInt(5, ponto.getOrdemNoRoteiro());
            pst.setTimestamp(6, ponto.getHorarioChegada());
            pst.setTimestamp(7, ponto.getHorarioSaida());
            pst.setInt(8, ponto.getTempoParadoCalculado());

            if (pst.executeUpdate() > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        ponto.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir ponto: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public Ponto get(int idPonto) {

        Ponto ponto = null;
        String sql = "SELECT * FROM Ponto WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {
            
            pst.setInt(1, idPonto);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    ponto = new Ponto(rs.getInt("id"),
                                      rs.getInt("roteiro_id"),
                                      rs.getString("endereco"),
                                      rs.getDouble("latitude"),
                                      rs.getDouble("longitude"),
                                      rs.getInt("ordem_no_roteiro"),
                                      rs.getTimestamp("data_hora_chegada"),
                                      rs.getTimestamp("data_hora_saida"),
                                      rs.getInt("tempo_parado_calculado")
                                     );
                }

            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar ponto: " + e.getMessage());
            e.printStackTrace();
        }

        return ponto;

    }

    public List<Ponto> getAll() {

        List<Ponto> listaPonto = new ArrayList<>();
        
        String sql = "SELECT * FROM Ponto ORDER BY id";

        try (PreparedStatement pst = conexao().prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Ponto ponto = new Ponto(rs.getInt("id"),
                                        rs.getInt("roteiro_id"),
                                        rs.getString("endereco"),
                                        rs.getDouble("latitude"),
                                        rs.getDouble("longitude"),
                                        rs.getInt("ordem_no_roteiro"),
                                        rs.getTimestamp("data_hora_chegada"),
                                        rs.getTimestamp("data_hora_saida"),
                                        rs.getInt("tempo_parado_calculado")
                                       );
                listaPonto.add(ponto);
            }

        } catch (SQLException e) {
            System.err.println("--- ERRO NA LISTAGEM DE PONTOS (SQL) ---");
            System.err.println("Detalhes do erro: " + e.getMessage());
            e.printStackTrace();
            listaPonto.clear();
        }

        return listaPonto;

    }

    public List<Ponto> getPontosPorRoteiro(int idRoteiro) {
        
        List<Ponto> listaPonto = new ArrayList<>();

        String sql = "SELECT * FROM Ponto WHERE roteiro_id = ? ORDER BY ordem_no_roteiro ASC";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {

            pst.setInt(1, idRoteiro);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    Ponto ponto = new Ponto(rs.getInt("id"),
                                            rs.getInt("roteiro_id"),
                                            rs.getString("endereco"),
                                            rs.getDouble("latitude"),
                                            rs.getDouble("longitude"),
                                            rs.getInt("ordem_no_roteiro"),
                                            rs.getTimestamp("data_hora_chegada"),
                                            rs.getTimestamp("data_hora_saida"),
                                            rs.getInt("tempo_parado_calculado")
                                        );
                    listaPonto.add(ponto);
                }

            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar pontos por roteiro: " + e.getMessage());
            e.printStackTrace();
        }

        return listaPonto;

    }

    public boolean update(Ponto ponto) {

        String sql = "UPDATE Ponto SET roteiro_id = ?, endereco = ?, latitude = ?, longitude = ?, ordem_no_roteiro = ?, data_hora_chegada = ?, data_hora_saida = ?, tempo_parado_calculado = ? WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {
            

            pst.setInt(1, ponto.getIdRoteiro());
            pst.setString(2, ponto.getEndereco());
            pst.setDouble(3, ponto.getLatitude());
            pst.setDouble(4, ponto.getLongitude());
            pst.setInt(5, ponto.getOrdemNoRoteiro());
            pst.setTimestamp(6, ponto.getHorarioChegada());
            pst.setTimestamp(7, ponto.getHorarioSaida());
            pst.setInt(8, ponto.getTempoParadoCalculado());
            pst.setInt(9, ponto.getId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERRO ao atualizar ponto: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public boolean remove(int id) {

        String sql = "DELETE FROM Ponto WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {

            pst.setInt(1, id);

            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("ERRO ao remover ponto: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

}
