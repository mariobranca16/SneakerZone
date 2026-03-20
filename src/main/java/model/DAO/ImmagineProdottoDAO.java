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
public class ImmagineProdottoDAO {
    public String doRetrievePrimaImmagine(long idProdotto) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT imgPath FROM Immagine_Prodotto WHERE prodotto_id = ? ORDER BY posizione ASC LIMIT 1"
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
    public void doSaveWithFile(long idProdotto, byte[] fileContent, String uploadDir, String nomeFile, String imgPath) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs() && !dir.exists()) {
            throw new IOException("Impossibile creare la cartella di upload delle immagini");
        }
        File dest = new File(dir, nomeFile);
        try (OutputStream out = new FileOutputStream(dest)) {
            out.write(fileContent);
        }
        try (Connection connection = ConPool.getConnection()) {
            doDeleteByProdottoRows(connection, idProdotto);
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Immagine_Prodotto (prodotto_id, imgPath, descrizione, posizione) VALUES (?, ?, '', 1)"
            )) {
                ps.setLong(1, idProdotto);
                ps.setString(2, imgPath);
                if (ps.executeUpdate() != 1)
                    throw new RuntimeException("Errore nell'inserimento dell'immagine");
            }
        } catch (SQLException | RuntimeException e) {
            if (dest.exists() && !dest.delete()) dest.deleteOnExit();
            throw new RuntimeException("Errore nel salvataggio dell'immagine per il prodotto", e);
        }
    }
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
    private void doDeleteByProdottoRows(Connection connection, long idProdotto) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM Immagine_Prodotto WHERE prodotto_id = ?"
        )) {
            ps.setLong(1, idProdotto);
            ps.executeUpdate();
        }
    }
}
