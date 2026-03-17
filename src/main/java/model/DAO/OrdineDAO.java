package model.DAO;

import model.Bean.DettaglioOrdine;
import model.Bean.Ordine;
import model.Bean.StatoOrdine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAO {
    public void doSave(Ordine ordine) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);
            try {
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

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            ordine.setId(rs.getLong(1));
                        } else {
                            throw new SQLException("Errore recupero ID ordine");
                        }
                    }
                }

                if (ordine.getDettagliOrdine() != null) {
                    DettaglioOrdineDAO dettagliOrdineDAO = new DettaglioOrdineDAO();
                    ProdottoTagliaDAO prodottoTagliaDAO = new ProdottoTagliaDAO();

                    for (DettaglioOrdine d : ordine.getDettagliOrdine()) {
                        d.setIdOrdine(ordine.getId());
                        dettagliOrdineDAO.doSave(connection, d);
                        prodottoTagliaDAO.decrementaDisponibilita(connection, d.getIdProdotto(), d.getTaglia(), d.getQuantita());
                    }
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("Errore durante il salvataggio dell'ordine", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio dell'ordine", e);
        }
    }

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

    private Ordine buildOrdine(ResultSet rs) throws SQLException {
        Ordine o = new Ordine();
        o.setId(rs.getLong("id"));
        o.setIdUtente(rs.getLong("utente_id"));
        o.setIdIndirizzoSpedizione(rs.getLong("indirizzo_spedizione_id"));

        Date data = rs.getDate("data_ordine");
        o.setDataOrdine(data != null ? data.toLocalDate() : null);

        String statoStr = rs.getString("stato_ordine");
        o.setStato(statoStr != null ? StatoOrdine.fromString(statoStr) : null);

        DettaglioOrdineDAO doDAO = new DettaglioOrdineDAO();
        o.setDettagliOrdine(doDAO.doRetrieveByOrdine(o.getId()));
        return o;
    }
}
