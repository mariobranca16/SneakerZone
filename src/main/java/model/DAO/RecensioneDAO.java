package model.DAO;

import model.Bean.Recensione;
import model.Bean.StatoOrdine;
import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * DAO per la tabella Recensione.
 * Gestisce il salvataggio e il recupero delle recensioni prodotto.
 */
public class RecensioneDAO {

    // Salva una nuova recensione
    public void doSave(Recensione recensione) {
        // controlla prima che la sua valutazione sia nel range corretto
        if (recensione.getValutazione() < 1 || recensione.getValutazione() > 5) {
            throw new IllegalArgumentException("La valutazione deve essere compresa tra 1 e 5");
        }
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO Recensione (utente_id, prodotto_id, titolo, valutazione, commento, data_recensione) " +
                             "VALUES (?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS
             )) {
            ps.setLong(1, recensione.getIdUtente());
            ps.setLong(2, recensione.getIdProdotto());
            ps.setString(3, recensione.getTitolo());
            ps.setInt(4, recensione.getValutazione());
            ps.setString(5, recensione.getCommento());
            ps.setDate(6, Date.valueOf(recensione.getDataRecensione()));
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nell'inserimento della recensione");
            // salva l'id generato direttamente nel bean
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    recensione.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della recensione", e);
        }
    }

    // Recupera una recensione tramite il suo id
    public Recensione doRetrieveByKey(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Recensione WHERE id = ? "
             )) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return buildRecensione(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero della recensione con ID: " + id, e);
        }
        return null;
    }

    // Recupera tutte le recensioni di un prodotto, ordinandole dalla più recente
    public List<Recensione> doRetrieveByProdotto(long idProdotto) {
        List<Recensione> recensioni = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Recensione WHERE prodotto_id = ? ORDER BY data_recensione DESC"
             )) {
            ps.setLong(1, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(buildRecensione(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle recensioni il prodotto con ID: " + idProdotto, e);
        }
        return recensioni;
    }

    // Recupera tutte le recensioni scritte da un utente, ordinandole dalla più recente
    public List<Recensione> doRetrieveByUtente(long idUtente) {
        List<Recensione> recensioni = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Recensione WHERE utente_id = ? ORDER BY data_recensione DESC"
             )) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(buildRecensione(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle recensioni dell'utente con ID: " + idUtente, e);
        }
        return recensioni;
    }

    // Controlla se l'utente ha acquistato il prodotto almeno una volta per poter recensire
    // Gli ordini annullati non sono considerati validi
    public boolean haAcquistato(long idUtente, long idProdotto) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT 1 FROM Ordine o JOIN Dettaglio_Ordine d ON o.id = d.ordine_id " +
                             "WHERE o.utente_id = ? AND d.prodotto_id = ? AND o.stato_ordine <> ? LIMIT 1"
             )) {
            ps.setLong(1, idUtente);
            ps.setLong(2, idProdotto);
            ps.setString(3, StatoOrdine.ANNULLATO.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel controllo dell'acquisto", e);
        }
    }

    // Controlla se un utente ha già recensito un prodotto
    public boolean haGiaRecensito(long idUtente, long idProdotto) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT 1 FROM Recensione WHERE utente_id = ? AND prodotto_id = ? LIMIT 1"
             )) {
            ps.setLong(1, idUtente);
            ps.setLong(2, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel controllo della recensione esistente", e);
        }
    }

    // Cancella una recensione per id
    public void doDelete(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM Recensione WHERE id = ?"
             )) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella cancellazione della recensione con ID: " + id, e);
        }
    }

    // Helper privato per costruire l'oggetto partendo dal ResultSet
    private Recensione buildRecensione(ResultSet rs) throws SQLException {
        Recensione r = new Recensione();
        r.setId(rs.getLong("id"));
        r.setIdUtente(rs.getLong("utente_id"));
        r.setIdProdotto(rs.getLong("prodotto_id"));
        r.setTitolo(rs.getString("titolo"));
        r.setValutazione(rs.getInt("valutazione"));
        r.setCommento(rs.getString("commento"));
        Date dataRec = rs.getDate("data_recensione");
        r.setDataRecensione(dataRec != null ? dataRec.toLocalDate() : null);
        return r;
    }
}
