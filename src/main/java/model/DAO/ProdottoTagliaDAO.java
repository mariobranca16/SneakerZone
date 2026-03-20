package model.DAO;
import model.Bean.ProdottoTaglia;
import model.ConPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class ProdottoTagliaDAO {
    public static final int[] TAGLIE_DISPONIBILI = {35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46};
    public static List<ProdottoTaglia> buildFromParams(Map<String, String[]> params, long idProdotto) {
        List<ProdottoTaglia> taglie = new ArrayList<>();
        for (int taglia : TAGLIE_DISPONIBILI) {
            String[] values = params.get("q_" + taglia);
            if (values == null || values.length == 0 || values[0].isBlank()) continue;
            int quantita;
            try {
                quantita = Integer.parseInt(values[0]);
            } catch (NumberFormatException e) {
                continue;
            }
            if (quantita < 0) continue;
            ProdottoTaglia pt = new ProdottoTaglia();
            pt.setIdProdotto(idProdotto);
            pt.setTaglia(taglia);
            pt.setQuantita(quantita);
            taglie.add(pt);
        }
        return taglie;
    }
    public void doDeleteByProdotto(Connection connection, long idProdotto) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM Prodotto_Taglia WHERE prodotto_id = ?"
        )) {
            ps.setLong(1, idProdotto);
            ps.executeUpdate();
        }
    }
    public void doSaveOrUpdate(Connection connection, ProdottoTaglia pt) throws SQLException {
        if (pt.getQuantita() < 0)
            throw new IllegalArgumentException("La quantità non può essere negativa");
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Prodotto_Taglia (prodotto_id, taglia, quantita) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE quantita = ?"
        )) {
            ps.setLong(1, pt.getIdProdotto());
            ps.setInt(2, pt.getTaglia());
            ps.setInt(3, pt.getQuantita());
            ps.setInt(4, pt.getQuantita());
            ps.executeUpdate();
        }
    }
    public int doRetrieveDisponibilita(long idProdotto, int taglia) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT quantita FROM Prodotto_Taglia WHERE prodotto_id = ? AND taglia = ?"
             )) {
            ps.setLong(1, idProdotto);
            ps.setInt(2, taglia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt("quantita");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero della disponibilità per prodotto/taglia", e);
        }
        return 0;
    }
    public List<ProdottoTaglia> doRetrieveDisponibilitaByProdotto(long idProdotto) {
        List<ProdottoTaglia> taglie = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT prodotto_id, taglia, quantita FROM Prodotto_Taglia WHERE prodotto_id = ? ORDER BY taglia"
             )) {
            ps.setLong(1, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProdottoTaglia pt = new ProdottoTaglia();
                    pt.setIdProdotto(rs.getLong("prodotto_id"));
                    pt.setTaglia(rs.getInt("taglia"));
                    pt.setQuantita(rs.getInt("quantita"));
                    taglie.add(pt);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle taglie per prodotto", e);
        }
        return taglie;
    }
    public void incrementaDisponibilita(Connection connection, long idProdotto, int taglia, int quantita) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE Prodotto_Taglia SET quantita = quantita + ? WHERE prodotto_id = ? AND taglia = ?"
        )) {
            ps.setInt(1, quantita);
            ps.setLong(2, idProdotto);
            ps.setInt(3, taglia);
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Incremento non riuscito, prodotto o taglia non trovati");
        }
    }
    public void decrementaDisponibilita(Connection connection, long idProdotto, int taglia, int quantita) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE Prodotto_Taglia SET quantita = quantita - ? WHERE prodotto_id = ? AND taglia = ? AND quantita >= ?"
        )) {
            ps.setInt(1, quantita);
            ps.setLong(2, idProdotto);
            ps.setInt(3, taglia);
            ps.setInt(4, quantita);
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Stock insufficiente per il prodotto/taglia selezionato");
        }
    }
}
