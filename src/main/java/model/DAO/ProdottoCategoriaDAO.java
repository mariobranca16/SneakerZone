package model.DAO;
import model.ConPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
public class ProdottoCategoriaDAO {
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
    public void doReplace(long idProdotto, Set<Long> idCategorie) {
        doDeleteByProdotto(idProdotto);
        for (long idCat : idCategorie)
            doSave(idProdotto, idCat);
    }
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
