package model.DAO;

import model.Bean.MetodoPagamento;
import model.ConPool;

import java.sql.*;

public class MetodoPagamentoDAO {

    public MetodoPagamento doRetrieveByUtente(long idUtente) {
        try (Connection conn = ConPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM MetodoPagamento WHERE utente_id = ?")) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return build(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore recupero metodo pagamento utente " + idUtente, e);
        }
        return null;
    }

    public void doSave(MetodoPagamento mp) {
        try (Connection conn = ConPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO MetodoPagamento (utente_id, nome_carta, numero_carta, scadenza) VALUES (?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, mp.getIdUtente());
            ps.setString(2, mp.getNomeCarta());
            ps.setString(3, mp.getNumeroCarta());
            ps.setString(4, mp.getScadenza());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) mp.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore salvataggio metodo pagamento", e);
        }
    }

    public void doUpdate(MetodoPagamento mp) {
        try (Connection conn = ConPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE MetodoPagamento SET nome_carta=?, numero_carta=?, scadenza=? WHERE utente_id=?")) {
            ps.setString(1, mp.getNomeCarta());
            ps.setString(2, mp.getNumeroCarta());
            ps.setString(3, mp.getScadenza());
            ps.setLong(4, mp.getIdUtente());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore aggiornamento metodo pagamento", e);
        }
    }

    public void doSaveOrUpdate(MetodoPagamento mp) {
        if (doRetrieveByUtente(mp.getIdUtente()) == null) {
            doSave(mp);
        } else {
            doUpdate(mp);
        }
    }

    private MetodoPagamento build(ResultSet rs) throws SQLException {
        MetodoPagamento mp = new MetodoPagamento();
        mp.setId(rs.getLong("id"));
        mp.setIdUtente(rs.getLong("utente_id"));
        mp.setNomeCarta(rs.getString("nome_carta"));
        mp.setNumeroCarta(rs.getString("numero_carta"));
        mp.setScadenza(rs.getString("scadenza"));
        return mp;
    }
}
