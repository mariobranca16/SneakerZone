package model.DAO;

import model.Bean.Prodotto;
import model.Bean.Wishlist;
import model.ConPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * DAO per la tabella Wishlist.
 * Gestisce l'aggiunta, la rimozione e il recupero dei prodotti salvati dall'utente
 */
public class WishlistDAO {

    /*
     * Aggiunge un prodotto alla wishlist.
     * Se il prodotto è già presente non viene inserito di nuovo
     */
    public void addToWishlist(Wishlist wishlist) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT IGNORE INTO Wishlist (utente_id, prodotto_id) VALUES (?, ?)"
             )) {
            ps.setLong(1, wishlist.getIdUtente());
            ps.setLong(2, wishlist.getIdProdotto());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'aggiunta alla wishlist del prodotto con ID: " + wishlist.getIdProdotto(), e);
        }
    }

    // Rimuove un prodotto dalla wishlist
    public void removeFromWishlist(Wishlist wishlist) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM Wishlist WHERE utente_id = ? AND prodotto_id = ?"
             )) {
            ps.setLong(1, wishlist.getIdUtente());
            ps.setLong(2, wishlist.getIdProdotto());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella rimozione dalla wishlist del prodotto con ID: " + wishlist.getIdProdotto(), e);
        }
    }

    // Conta quanti prodotti sono presenti nella wishlist dell'utente
    public int countByUtente(long idUtente) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT COUNT(*) FROM Wishlist WHERE utente_id = ?"
             )) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel conteggio della wishlist", e);
        }
        return 0;
    }

    // Recupera tutti i prodotti presenti nella wishlist dell'utente.
    public List<Prodotto> doRetrieveProdottiByUtente(long idUtente) {
        List<Prodotto> prodotti = new ArrayList<>();
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT p.* FROM Wishlist w " +
                             "JOIN Prodotto p ON w.prodotto_id = p.id " +
                             "WHERE w.utente_id = ?"
             )) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    prodotti.add(prodottoDAO.buildProdotto(rs)); // riusa l'helper protected in prodottoDAO
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei prodotti dalla wishlist per l'utente con ID: " + idUtente, e);
        }
        return prodotti;
    }
}
