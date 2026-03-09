package model.dao;

import model.bean.ImmagineProdotto;
import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImmagineProdottoDAO {
    public void doSave(ImmagineProdotto img) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "INSERT INTO Immagine_Prodotto (prodotto_id, imgPath, descrizione, posizione) VALUES (?, ?, ?, ?)",
                 Statement.RETURN_GENERATED_KEYS
             )) {
            ps.setLong(1, img.getIdProdotto());
            ps.setString(2, img.getImgPath());
            ps.setString(3, img.getDescrizione());
            ps.setInt(4, img.getPosizione());

            if(ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nell'inserimento dell'immagine");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next())
                    img.setId(rs.getLong(1));
            }
        } catch (SQLException e){
            throw new RuntimeException("Errore nel salvataggio dell'immagine per il prodotto", e);
        }
    }

    public ImmagineProdotto doRetrieveByKey(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "SELECT * FROM Immagine_Prodotto WHERE id=?"
             )) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    ImmagineProdotto img = new ImmagineProdotto();
                    img.setId(rs.getLong("id"));
                    img.setIdProdotto(rs.getLong("prodotto_id"));
                    img.setImgPath(rs.getString("imgPath"));
                    img.setDescrizione(rs.getString("descrizione"));
                    img.setPosizione(rs.getInt("posizione"));
                    return img;
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Errore nel recupero dell'immagine con ID: " + id, e);
        }
        return null;
    }

    public List<ImmagineProdotto> doRetrieveByProdotto(long idProdotto) {
        List<ImmagineProdotto> immagini = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "SELECT * FROM Immagine_Prodotto WHERE prodotto_id = ? ORDER BY posizione ASC"
             )) {
            ps.setLong(1, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    ImmagineProdotto img = new ImmagineProdotto();
                    img.setId(rs.getLong("id"));
                    img.setIdProdotto(rs.getLong("prodotto_id"));
                    img.setImgPath(rs.getString("imgPath"));
                    img.setDescrizione(rs.getString("descrizione"));
                    img.setPosizione(rs.getInt("posizione"));
                    immagini.add(img);
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Errore nel recupero delle immagini per il prodotto con ID: " + idProdotto, e);
        }
        return immagini;
    }

    public void doDeleteByKey(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "DELETE FROM Immagine_Prodotto WHERE id = ?"
             )) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Errore nella cancellazione dell'immagine con ID: " + id, e);
        }
    }

    public void deleteByProdotto(long idProdotto) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "DELETE FROM Immagine_Prodotto WHERE prodotto_id = ?"
             )) {
            ps.setLong(1, idProdotto);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Errore nella cancellazione delle immagini del prodotto con ID: " + idProdotto, e);
        }
    }
}
