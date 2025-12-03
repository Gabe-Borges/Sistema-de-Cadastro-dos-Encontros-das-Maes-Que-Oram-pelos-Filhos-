// Pacote: br.com.fatec.dao
package br.com.fatec.dao;

import br.com.fatec.factory.ConnectionFactory;
import br.com.fatec.modelo.Mae;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaeDAO {

    public void inserir(Mae mae) throws SQLException {
        String sql = "INSERT INTO MAE (nome, telefone, endereco, data_aniversario) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, mae.getNome());
            stmt.setString(2, mae.getTelefone());
            stmt.setString(3, mae.getEndereco());
            stmt.setDate(4, Date.valueOf(mae.getDataAniversario()));
            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }

    public List<Mae> listarTodos() throws SQLException {
        String sql = "SELECT id_mae, nome, telefone, endereco, data_aniversario FROM MAE ORDER BY nome";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Mae> maes = new ArrayList<>();
        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Mae mae = new Mae(
                        rs.getInt("id_mae"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("endereco"),
                        rs.getDate("data_aniversario").toLocalDate()
                );
                maes.add(mae);
            }
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return maes;
    }

    public List<Mae> listarTodas() throws SQLException {
        return listarTodos();
    }

    // ⭐ MÉTODO NECESSÁRIO: buscarPorId(int id) ⭐
    public Mae buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_mae, nome, telefone, endereco, data_aniversario FROM MAE WHERE id_mae = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Mae mae = null;
        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                mae = new Mae(
                        rs.getInt("id_mae"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("endereco"),
                        rs.getDate("data_aniversario").toLocalDate()
                );
            }
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return mae;
    }

    // Dentro de br.com.fatec.dao.MaeDAO.java
    public void salvar(Mae mae) throws SQLException {
        String sql = "INSERT INTO MAE (nome, telefone, endereco, data_aniversario) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, mae.getNome());
            stmt.setString(2, mae.getTelefone());
            stmt.setString(3, mae.getEndereco());

            stmt.setDate(4, Date.valueOf(mae.getDataAniversario()));

            stmt.executeUpdate();

        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }


    public List<Mae> listarAniversariantesDoMes(int mes) throws SQLException {
        String sql = "SELECT id_mae, nome, data_aniversario FROM MAE WHERE MONTH(data_aniversario) = ? ORDER BY DAY(data_aniversario), nome";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Mae> aniversariantes = new ArrayList<>();
        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mes);
            rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate dataAniversario = rs.getDate("data_aniversario").toLocalDate();
                Mae mae = new Mae(
                        rs.getInt("id_mae"),
                        rs.getString("nome"),
                        null, // Telefone e Endereço não são carregados neste relatório
                        null,
                        dataAniversario
                );
                aniversariantes.add(mae);
            }
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return aniversariantes;
    }

}
