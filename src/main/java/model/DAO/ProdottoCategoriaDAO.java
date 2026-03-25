package model.DAO;

import model.ConPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

/*
 * DAO per la tabella di associazione n-m tra Prodotto e Categoria.
 * Gestisce l'assegnazione delle categorie a un prodotto.
 */
public class ProdottoCategoriaDAO {

    // associa una categoria a un prodotto
    public void doSave(long idProdotto, long idCategoria) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT IGNORE INTO Prodotto_Categoria (prodotto_id, categoria_id) VALUES (?, ?)"
             )) {
            ps.setLong(1, idProdotto);
            ps.setLong(2, idCategoria);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'associazione prodotto-categoria", e);
        }
    }

    // sostituisce completamente le categorie del prodotto
    public void doReplace(long idProdotto, Set<Long> idCategorie) {
        // rimuove prima tutte quelle vecchie
        doDeleteByProdotto(idProdotto);
        // ricrea tutte le associazione in base alle categorie
        for (long idCat : idCategorie)
            doSave(idProdotto, idCat);
    }

    // elimina tutte le categorie associate a un prodotto
    public void doDeleteByProdotto(long idProdotto) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM Prodotto_Categoria WHERE prodotto_id = ?"
             )) {
            ps.setLong(1, idProdotto);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella rimozione delle categorie associate al prodotto con ID: " + idProdotto, e);
        }
    }
}
