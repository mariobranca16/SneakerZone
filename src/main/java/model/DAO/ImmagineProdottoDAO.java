package model.DAO;

import model.ConPool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * DAO della la tabella Immagine_Prodotto.
 * Ogni prodotto gestisce una sola immagine principale.
 */
public class ImmagineProdottoDAO {

    // Recupera il path dell'immagine associata al prodotto.
    public String doRetrievePrimaImmagine(long idProdotto) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT imgPath FROM Immagine_Prodotto WHERE prodotto_id = ?"
             )) {
            ps.setLong(1, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("imgPath");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dell'immagine per il prodotto con ID: " + idProdotto, e);
        }
        return null;
    }

    /*
     * Salva prima il file sul disco, poi aggiorna il database.
     * Se il salvataggio nel db fallisce, prova a rimuovere il file scritto, per non lasciare residui inutilizzati.
     */
    public void doSaveWithFile(long idProdotto, byte[] fileContent, String uploadDir, String nomeFile, String imgPath) throws IOException {
        // crea la cartella di upload se non esiste ancora
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs() && !dir.exists()) {
            throw new IOException("Impossibile creare la cartella di upload delle immagini");
        }
        File dest = new File(dir, nomeFile);
        // salva il file nella cartella scelta
        try (OutputStream out = new FileOutputStream(dest)) {
            out.write(fileContent);
        }
        try (Connection connection = ConPool.getConnection()) {
            // elimina eventuali righe vecchie
            doDeleteByProdottoRows(connection, idProdotto);
            // salva nel db il path della nuova immagine
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Immagine_Prodotto (prodotto_id, imgPath) VALUES (?, ?)"
            )) {
                ps.setLong(1, idProdotto);
                ps.setString(2, imgPath);
                if (ps.executeUpdate() != 1)
                    throw new RuntimeException("Errore nell'inserimento dell'immagine");
            }
        } catch (SQLException | RuntimeException e) {
            // se c'è un errore nel db, prova a cancellare il file
            if (dest.exists() && !dest.delete()) dest.deleteOnExit();
            throw new RuntimeException("Errore nel salvataggio dell'immagine per il prodotto", e);
        }
    }

    /*
     * Elimina l'immagine di un prodotto sia dal disco che dal database.
     * Serve a non lasciare file inutilizzati dopo la rimozione del prodotto.
     */
    public void doDeleteByProdotto(long idProdotto, String appRealPath) {
        String imgPath = doRetrievePrimaImmagine(idProdotto);
        if (imgPath != null && appRealPath != null) {
            File f = new File(appRealPath + imgPath);
            if (f.exists()) f.delete();
        }
        try (Connection connection = ConPool.getConnection()) {
            doDeleteByProdottoRows(connection, idProdotto);
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella cancellazione delle immagini per il prodotto con ID: " + idProdotto, e);
        }
    }

    // Cancella dal database tutte le righe immagine collegate al prodotto.
    private void doDeleteByProdottoRows(Connection connection, long idProdotto) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM Immagine_Prodotto WHERE prodotto_id = ?"
        )) {
            ps.setLong(1, idProdotto);
            ps.executeUpdate();
        }
    }
}
