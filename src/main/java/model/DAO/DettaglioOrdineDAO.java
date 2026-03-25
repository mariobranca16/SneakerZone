package model.DAO;

import model.Bean.DettaglioOrdine;
import model.Bean.Prodotto;
import model.ConPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * DAO della tabella Dettaglio_Ordine.
 * Ogni record rappresenta un prodotto acquistato in uno specifico ordine.
 */
public class DettaglioOrdineDAO {

    /*
     * Salva un dettaglio ordine usando la connessione già aperta.
     * In questo modo il metodo resta dentro la stessa transazione dell'ordine.
     */
    public void doSave(Connection connection, DettaglioOrdine dettaglioOrdine) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Dettaglio_Ordine (ordine_id, prodotto_id, taglia, quantita, costo) VALUES (?, ?, ?, ?, ?)"
        )) {
            ps.setLong(1, dettaglioOrdine.getIdOrdine());
            ps.setLong(2, dettaglioOrdine.getIdProdotto());
            ps.setInt(3, dettaglioOrdine.getTaglia());
            ps.setInt(4, dettaglioOrdine.getQuantita());
            ps.setDouble(5, dettaglioOrdine.getCosto());
            // deve essere inserito in una sola riga, altrimenti qualcosa non va
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nell'inserimento dei dettagli ordine");
        }
    }

    /*
     * Recupera tutti i prodotti presenti in un ordine.
     * Per ogni riga costruisco anche il relativo DettagliOrdine.
     */
    public List<DettaglioOrdine> doRetrieveByOrdine(long idOrdine) {
        List<DettaglioOrdine> dettagli = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT ordine_id, prodotto_id, taglia, quantita, costo " +
                             "FROM Dettaglio_Ordine WHERE ordine_id = ?"
             )) {
            ps.setLong(1, idOrdine);
            // trasforma ogni riga in un dettaglio ordine completo
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    dettagli.add(buildDettaglioOrdine(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei dettagli per l'ordine con ID: " + idOrdine, e);
        }
        return dettagli;
    }


    // Helper privato per costruire il dettaglio ordine a partire da una riga del ResultSet
    private DettaglioOrdine buildDettaglioOrdine(ResultSet rs) throws SQLException {
        DettaglioOrdine d = new DettaglioOrdine();
        d.setIdOrdine(rs.getLong("ordine_id"));
        d.setIdProdotto(rs.getLong("prodotto_id"));
        d.setTaglia(rs.getInt("taglia"));
        d.setQuantita(rs.getInt("quantita"));
        d.setCosto(rs.getDouble("costo"));
        // prova a recuperare il prodotto dal catalogo
        Prodotto p = new ProdottoDAO().doRetrieveByKey(d.getIdProdotto());
        if (p == null) {
            // usa un placeholder così rimane leggibile
            p = new Prodotto();
            p.setId(d.getIdProdotto());
            p.setNome("Prodotto non più disponibile");
        }
        d.setProdotto(p);
        return d;
    }
}
