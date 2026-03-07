package model.dao;

import model.ConPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProdottoCategoriaDAO {
    public void doSave(long idProdotto, long idCategoria) {
        try (Connection connection = ConPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "INSERT IGNORE INTO Prodotto_Categoria (prodotto_id, categoria_id) VALUES (?, ?)"
            )){
            ps.setLong(1, idProdotto);
            ps.setLong(2, idCategoria);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Errore nell'associazione prodotto-categoria", e);
        }
    }

    public void doDeleteByProdotto(long idProdotto) {
        try (Connection connection = ConPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM Prodotto_Categoria WHERE prodotto_id = ?"
            )){
            ps.setLong(1, idProdotto);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Errore nella rimozione delle categorie associate al prodotto con ID: " + idProdotto, e);
        }
    }
}
