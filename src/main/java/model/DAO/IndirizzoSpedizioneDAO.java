package model.DAO;

import model.Bean.IndirizzoSpedizione;
import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * DAO per la tabella IndirizzoSpedizione.
 * Gestisce il salvataggio e il recupero degli indirizzi usati dagli utenti.
 */
public class IndirizzoSpedizioneDAO {
    // Inserisce un nuovo indirizzo nel db
    public void doSave(IndirizzoSpedizione is) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO IndirizzoSpedizione (utente_id, destinatario, via, citta, provincia, cap, paese) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS
             )) {
            ps.setLong(1, is.getIdUtente());
            ps.setString(2, is.getDestinatario());
            ps.setString(3, is.getVia());
            ps.setString(4, is.getCitta());
            ps.setString(5, is.getProvincia());
            ps.setString(6, is.getCap());
            ps.setString(7, is.getPaese());
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nell'inserimento dell'indirizzo");
            // salva l'id generato direttamente nel bean
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    is.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'inserimento dell'indirizzo " + is.getVia(), e);
        }
    }

    // Recupera un indirizzo di spedizione tramite l'id; se non esiste restituisce null
    public IndirizzoSpedizione doRetrieveByKey(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM IndirizzoSpedizione WHERE id = ?"
             )) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildIndirizzoSpedizione(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dell'indirizzo con ID: " + id, e);
        }
        return null;
    }

    // Recupera tutti gli indirizzi salvati da un utente
    public List<IndirizzoSpedizione> doRetrieveByUtente(long idUtente) {
        List<IndirizzoSpedizione> indirizzi = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM IndirizzoSpedizione WHERE utente_id = ?"
             )) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    indirizzi.add(buildIndirizzoSpedizione(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero degli indirizzi dell'utente con ID: " + idUtente, e);
        }
        return indirizzi;
    }

    // Aggiorna i campi di un indirizzo già esistente
    public void doUpdate(IndirizzoSpedizione is) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE IndirizzoSpedizione SET destinatario = ?, via = ?, citta = ?, provincia = ?, " +
                             "cap = ?, paese = ? WHERE id = ?"
             )) {
            ps.setString(1, is.getDestinatario());
            ps.setString(2, is.getVia());
            ps.setString(3, is.getCitta());
            ps.setString(4, is.getProvincia());
            ps.setString(5, is.getCap());
            ps.setString(6, is.getPaese());
            ps.setLong(7, is.getId());
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nell'aggiornamento dell'indirizzo");
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'aggiornamento dell'indirizzo " + is.getVia(), e);
        }
    }

    // Elimina un indirizzo tramite il suo id
    public void doDelete(long idIndirizzoSpedizione) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM IndirizzoSpedizione WHERE id=?"
             )) {
            ps.setLong(1, idIndirizzoSpedizione);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella cancellazione dell'indirizzo con ID: " + idIndirizzoSpedizione, e);
        }
    }

    // Helper privato per settare i campi dell'oggetto a partire dal ResultSet
    private IndirizzoSpedizione buildIndirizzoSpedizione(ResultSet rs) throws SQLException {
        IndirizzoSpedizione is = new IndirizzoSpedizione();
        is.setId(rs.getLong("id"));
        is.setIdUtente(rs.getLong("utente_id"));
        is.setDestinatario(rs.getString("destinatario"));
        is.setVia(rs.getString("via"));
        is.setCitta(rs.getString("citta"));
        is.setProvincia(rs.getString("provincia"));
        is.setCap(rs.getString("cap"));
        is.setPaese(rs.getString("paese"));
        return is;
    }
}
