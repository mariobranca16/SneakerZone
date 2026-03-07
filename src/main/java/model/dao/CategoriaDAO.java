package model.dao;

import model.bean.Categoria;
import model.ConPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    public List<Categoria> doRetrieveAll(){
        List<Categoria> categorie = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                 "SELECT * FROM Categoria ORDER BY nome"
             );
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()){
                Categoria categoria = new Categoria();
                categoria.setId(rs.getLong("id"));
                categoria.setNome(rs.getString("nome"));
                categorie.add(categoria);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero di tutte le categorie", e);
        }
        return categorie;
    }

    public List<Categoria> doRetrieveByProdotto(long idProdotto){
        List<Categoria> categorie = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
             "SELECT c.* FROM Categoria c JOIN Prodotto_Categoria pc ON c.id = pc.categoria_id WHERE pc.prodotto_id = ?"
             )) {
            ps.setLong(1, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getLong("id"));
                    categoria.setNome(rs.getString("nome"));
                    categorie.add(categoria);
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Errore nel recupero delle categorie del prodotto con ID: " + idProdotto, e);
        }
        return categorie;
    }

}
