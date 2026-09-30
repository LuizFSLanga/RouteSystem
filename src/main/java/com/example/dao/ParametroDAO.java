package com.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Parametro;

public class ParametroDAO extends DAO {

    public boolean insert(Parametro parametro) {

        String sql = "INSERT INTO Parametro (valor_combustivel, km_litro_veiculo, custo_por_km, jornada_padrao, regras_calculo_tempo_parado) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setDouble(1, parametro.getValorCombustivel());
            pst.setDouble(2, parametro.getKmLitro());
            pst.setDouble(3, parametro.getCustoPorKm());
            pst.setString(4, parametro.getJornadaPadraoHoras());
            pst.setString(5, parametro.getRegrasDeCalculo());

            if (pst.executeUpdate() > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        parametro.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir parametro: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public Parametro get(int idParametro) {

        Parametro parametro = null;
        String sql = "SELECT * FROM Parametro WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {
            
            pst.setInt(1, idParametro);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    parametro = new Parametro(rs.getInt("id"),
                                              rs.getDouble("valor_combustivel"),
                                              rs.getDouble("km_litro_veiculo"),
                                              rs.getDouble("custo_por_km"),
                                              rs.getString("jornada_padrao"),
                                              rs.getString("regras_calculo_tempo_parado")
                                             );
                }

            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar parametro: " + e.getMessage());
            e.printStackTrace();
        }

        return parametro;

    }

    public List<Parametro> getAll() {

        List<Parametro> listaParametro = new ArrayList<>();
        
        String sql = "SELECT * FROM Parametro ORDER BY id";

        try (PreparedStatement pst = conexao().prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Parametro parametro = new Parametro(rs.getInt("id"),
                                                    rs.getDouble("valor_combustivel"),
                                                    rs.getDouble("km_litro_veiculo"),
                                                    rs.getDouble("custo_por_km"),
                                                    rs.getString("jornada_padrao"),
                                                    rs.getString("regras_calculo_tempo_parado")
                                                   );
                listaParametro.add(parametro);
            }

        } catch (SQLException e) {
            System.err.println("--- ERRO NA LISTAGEM DE PARAMETROS (SQL) ---");
            System.err.println("Detalhes do erro: " + e.getMessage());
            e.printStackTrace();
            listaParametro.clear();
        }

        return listaParametro;

    }

    public boolean update(Parametro parametro) {

        String sql = "UPDATE Parametro SET valor_combustivel = ?, km_litro_veiculo = ?, custo_por_km = ?, jornada_padrao = ?, regras_calculo_tempo_parado = ? WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {

            pst.setDouble(1, parametro.getValorCombustivel());
            pst.setDouble(2, parametro.getKmLitro());
            pst.setDouble(3, parametro.getCustoPorKm());
            pst.setString(4, parametro.getJornadaPadraoHoras());
            pst.setString(5, parametro.getRegrasDeCalculo());
            pst.setInt(6, parametro.getId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERRO ao atualizar parametro: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public boolean remove(int id) {

        String sql = "DELETE FROM Parametro WHERE id = ?";

        try (PreparedStatement pst = conexao().prepareStatement(sql)) {

            pst.setInt(1, id);

            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("ERRO ao remover parametro: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

}
