package model.DAO;

import model.Bean.DettaglioOrdine;
import model.Bean.Ordine;
import model.Bean.StatoOrdine;
import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * DAO per la tabella Ordine.
 * Gestisce salvataggio, lettura e aggiornamento di un ordine.
 */
public class OrdineDAO {


    // Salva un nuovo ordine con una transazione JDBC manuale.
    // Questo perché l'ordine, i dettagli e l'aggiornamento degli stock devono andare a buon fine tutti insieme
    public void doSave(Ordine ordine) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // inserimento dell'ordine principale e recupero dell'id generato
                // in modo da collegare direttamente tutti i dettagli
                try (PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO Ordine (utente_id, indirizzo_spedizione_id, data_ordine, stato_ordine) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, ordine.getIdUtente());
                    ps.setLong(2, ordine.getIdIndirizzoSpedizione());
                    if (ordine.getDataOrdine() != null)
                        ps.setDate(3, Date.valueOf(ordine.getDataOrdine()));
                    else
                        ps.setNull(3, Types.DATE);
                    if (ordine.getStato() != null)
                        ps.setString(4, ordine.getStato().name());
                    else
                        ps.setNull(4, Types.VARCHAR);
                    if (ps.executeUpdate() != 1)
                        throw new SQLException("Errore inserimento ordine");
                    // recupero dell'id generato e salvataggio nel bean
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            ordine.setId(rs.getLong(1));
                        } else {
                            throw new SQLException("Errore recupero ID ordine");
                        }
                    }
                }
                // salvataggio di tutti i dettagli ordine e aggiornamento taglie sulla stessa connessione
                if (ordine.getDettagliOrdine() != null) {
                    DettaglioOrdineDAO dettagliOrdineDAO = new DettaglioOrdineDAO();
                    ProdottoTagliaDAO prodottoTagliaDAO = new ProdottoTagliaDAO();
                    for (DettaglioOrdine d : ordine.getDettagliOrdine()) {
                        d.setIdOrdine(ordine.getId());
                        dettagliOrdineDAO.doSave(connection, d);
                        // se lo stock è insufficiente, decrementaDisponibilita lancia RuntimeException
                        // l'eccezione viene catturata nel blocco catch, che annulla tutto
                        prodottoTagliaDAO.decrementaDisponibilita(connection, d.getIdProdotto(), d.getTaglia(), d.getQuantita());
                    }
                }
                // se tutto è andato bene, conferma l'ordine, i dettagli e lo stock
                connection.commit();
            } catch (Exception e) {
                // se un passaggio fallisce, annulla l'intera operazione
                connection.rollback();
                throw new RuntimeException("Errore durante il salvataggio dell'ordine", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio dell'ordine", e);
        }
    }

    // recupera un ordine tramite il suo id
    public Ordine doRetrieveByKey(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Ordine WHERE id = ?"
             )) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildOrdine(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dell'ordine con ID: " + id, e);
        }
        return null;
    }

    // recupera tutti gli ordini effettuati sulla piattaforma, ordinandoli dal più recente
    public List<Ordine> doRetrieveAll() {
        List<Ordine> ordini = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Ordine ORDER BY data_ordine DESC"
             )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordini.add(buildOrdine(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero di tutti gli ordini", e);
        }
        return ordini;
    }

    // recupera tutti gli ordini di uno specifico utente, ordinandoli dal più recente
    public List<Ordine> doRetrieveByUtente(long idUtente) {
        List<Ordine> ordini = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Ordine WHERE utente_id = ? ORDER BY data_ordine DESC"
             )) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordini.add(buildOrdine(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero degli ordini dell'utente con ID: " + idUtente, e);
        }
        return ordini;
    }

    /*
     * Annulla un ordine con una transazione manuale.
     * Aggiorna lo stato e rimette disponibili le taglie acquistate.
     */
    public void doAnnulla(Ordine ordine) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // aggiorna prima lo stato dell'ordine
                try (PreparedStatement ps = connection.prepareStatement(
                        "UPDATE Ordine SET stato_ordine = ? WHERE id = ?")) {
                    ps.setString(1, StatoOrdine.ANNULLATO.name());
                    ps.setLong(2, ordine.getId());
                    ps.executeUpdate();
                }
                // poi ripristina lo stock per ogni prodotto/taglia presente nell'ordine
                ProdottoTagliaDAO prodottoTagliaDAO = new ProdottoTagliaDAO();
                for (DettaglioOrdine d : ordine.getDettagliOrdine()) {
                    prodottoTagliaDAO.incrementaDisponibilita(connection, d.getIdProdotto(), d.getTaglia(), d.getQuantita());
                }
                // conferma tutto solo se gli aggiornamenti precedenti sono andati a buon fine
                connection.commit();
            } catch (Exception e) {
                // se qualcosa fallisce, annulla tutto
                connection.rollback();
                throw new RuntimeException("Errore durante l'annullamento dell'ordine", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'annullamento dell'ordine", e);
        }
    }

    // aggiorna lo stato dell'ordine
    public void doUpdateStato(long idOrdine, StatoOrdine nuovoStato) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE Ordine SET stato_ordine = ? WHERE id = ?")) {
            if (nuovoStato != null)
                ps.setString(1, nuovoStato.name());
            else
                ps.setNull(1, Types.VARCHAR);
            ps.setLong(2, idOrdine);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore aggiornamento stato ordine", e);
        }
    }

    // Helper privato per settare i campi dell'oggetto a partire dal ResultSet
    // carica anche i dettagli ordine, così l'oggetto è completo
    private Ordine buildOrdine(ResultSet rs) throws SQLException {
        Ordine o = new Ordine();
        o.setId(rs.getLong("id"));
        o.setIdUtente(rs.getLong("utente_id"));
        o.setIdIndirizzoSpedizione(rs.getLong("indirizzo_spedizione_id"));
        Date data = rs.getDate("data_ordine");
        o.setDataOrdine(data != null ? data.toLocalDate() : null);
        String statoStr = rs.getString("stato_ordine");
        o.setStato(statoStr != null ? StatoOrdine.fromString(statoStr) : null);
        // Caricamento eager dei dettagli ordine (ogni ordine porta già i suoi prodotti)
        DettaglioOrdineDAO doDAO = new DettaglioOrdineDAO();
        o.setDettagliOrdine(doDAO.doRetrieveByOrdine(o.getId()));
        return o;
    }
}
