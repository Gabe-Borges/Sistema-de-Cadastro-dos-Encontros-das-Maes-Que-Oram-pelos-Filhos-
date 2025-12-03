// Pacote: br.com.fatec.dao
package br.com.fatec.dao;

import br.com.fatec.factory.ConnectionFactory;
import br.com.fatec.modelo.ServicoFixo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicoFixoDAO {

    public ServicoFixo buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_servico_fixo, nome FROM SERVICO_FIXO WHERE id_servico_fixo = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int idServicoFixo = rs.getInt("id_servico_fixo");
                String nome = rs.getString("nome");
                return new ServicoFixo(idServicoFixo, nome);
            }
            return null;
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }

    public List<ServicoFixo> listarTodos() throws SQLException {
        String sql = "SELECT id_servico_fixo, nome FROM SERVICO_FIXO ORDER BY nome";
        List<ServicoFixo> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ServicoFixo servico = new ServicoFixo(
                        rs.getInt("id_servico_fixo"),
                        rs.getString("nome")
                );
                lista.add(servico);
            }

        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return lista;
    }
}