package br.com.fatec.dao;

import br.com.fatec.factory.ConnectionFactory;
import br.com.fatec.modelo.Encontro;
import br.com.fatec.modelo.Mae;
import br.com.fatec.modelo.ServicoEncontro;
import br.com.fatec.modelo.ServicoFixo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncontroDAO {

    private MaeDAO maeDAO = new MaeDAO();
    private ServicoFixoDAO servicoFixoDAO = new ServicoFixoDAO();


    public void salvarComEscala(Encontro encontro) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            int idEncontro = salvarApenasEncontro(conn, encontro);
            encontro.setId(idEncontro); // Atualiza o objeto com o ID

            for (ServicoEncontro se : encontro.getServicos()) {
                se.setEncontro(encontro); // Garante a referência correta
                salvarServicoEncontro(conn, se);
            }

            conn.commit(); // Confirma
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                ConnectionFactory.closeConnection(conn);
            }
        }
    }

    private int salvarApenasEncontro(Connection conn, Encontro encontro) throws SQLException {
        String sql = "INSERT INTO ENCONTRO (data_encontro, excluido_logico) VALUES (?, ?)";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setDate(1, Date.valueOf(encontro.getDataEncontro()));
            stmt.setBoolean(2, encontro.isExcluidoLogico());
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Falha ao obter ID do encontro.");
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

    private void salvarServicoEncontro(Connection conn, ServicoEncontro se) throws SQLException {
        String sql = "INSERT INTO SERVICO_ENCONTRO (id_encontro, id_servico_fixo, id_mae_responsavel, descricao_atividade) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, se.getEncontro().getId());
            stmt.setInt(2, se.getServicoFixo().getId());

            if (se.getMaeResponsavel() != null && se.getMaeResponsavel().getId() > 0) {
                stmt.setInt(3, se.getMaeResponsavel().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, se.getDescricaoAtividade());
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
        }
    }

    public void atualizar(Encontro encontro) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtDelete = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Transação

            String sqlUpdate = "UPDATE ENCONTRO SET data_encontro = ?, excluido_logico = ? WHERE id_encontro = ?";
            stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setDate(1, Date.valueOf(encontro.getDataEncontro()));
            stmtUpdate.setBoolean(2, encontro.isExcluidoLogico());
            stmtUpdate.setInt(3, encontro.getId());
            stmtUpdate.executeUpdate();

            String sqlDelete = "DELETE FROM SERVICO_ENCONTRO WHERE id_encontro = ?";
            stmtDelete = conn.prepareStatement(sqlDelete);
            stmtDelete.setInt(1, encontro.getId());
            stmtDelete.executeUpdate();

            for (ServicoEncontro se : encontro.getServicos()) {
                se.setEncontro(encontro); // Garante ID
                salvarServicoEncontro(conn, se);
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (stmtUpdate != null) stmtUpdate.close();
            if (stmtDelete != null) stmtDelete.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                ConnectionFactory.closeConnection(conn);
            }
        }
    }

    public Encontro buscarComEscalaPorId(int idEncontro) throws SQLException {
        String sql = "SELECT id_encontro, data_encontro, excluido_logico FROM ENCONTRO WHERE id_encontro = ?";
        return buscarEncontroGenerico(sql, pstmt -> pstmt.setInt(1, idEncontro));
    }

    public Encontro buscarComEscalaPorData(LocalDate data) throws SQLException {
        String sql = "SELECT id_encontro, data_encontro, excluido_logico FROM ENCONTRO WHERE data_encontro = ?";
        return buscarEncontroGenerico(sql, pstmt -> pstmt.setDate(1, Date.valueOf(data)));
    }

    private Encontro buscarEncontroGenerico(String sql, SqlParametros params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Encontro encontro = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            params.setarParametros(stmt); // Aplica o parâmetro (ID ou Data)
            rs = stmt.executeQuery();

            if (rs.next()) {
                encontro = new Encontro(
                        rs.getInt("id_encontro"),
                        rs.getDate("data_encontro").toLocalDate(),
                        rs.getBoolean("excluido_logico")
                );
                // Carrega a escala
                encontro.setServicos(carregarServicosDoEncontro(conn, encontro));
            }
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return encontro;
    }

    public List<Encontro> listarTodos() throws SQLException {
        String sql = "SELECT id_encontro, data_encontro, excluido_logico FROM ENCONTRO ORDER BY data_encontro DESC";
        List<Encontro> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Encontro(
                        rs.getInt("id_encontro"),
                        rs.getDate("data_encontro").toLocalDate(),
                        rs.getBoolean("excluido_logico")
                ));
            }
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return lista;
    }

    private List<ServicoEncontro> carregarServicosDoEncontro(Connection conn, Encontro encontro) throws SQLException {
        String sql = "SELECT id_servico_encontro, id_servico_fixo, id_mae_responsavel, descricao_atividade FROM SERVICO_ENCONTRO WHERE id_encontro = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ServicoEncontro> lista = new ArrayList<>();

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, encontro.getId());
            rs = stmt.executeQuery();

            while (rs.next()) {
                int idServicoFixo = rs.getInt("id_servico_fixo");
                int idMae = rs.getInt("id_mae_responsavel");
                boolean maeExiste = !rs.wasNull();

                ServicoFixo servico = servicoFixoDAO.buscarPorId(idServicoFixo);
                Mae mae = maeExiste ? maeDAO.buscarPorId(idMae) : null;

                ServicoEncontro se = new ServicoEncontro(
                        rs.getInt("id_servico_encontro"),
                        encontro,
                        servico,
                        mae,
                        rs.getString("descricao_atividade")
                );
                lista.add(se);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        return lista;
    }

    public void excluirLogico(int id) throws SQLException {
        String sql = "UPDATE ENCONTRO SET excluido_logico = TRUE WHERE id_encontro = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }

    @FunctionalInterface
    private interface SqlParametros {
        void setarParametros(PreparedStatement pstmt) throws SQLException;
    }
}