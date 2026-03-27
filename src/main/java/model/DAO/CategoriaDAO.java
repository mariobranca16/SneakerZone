package model.DAO;

import model.Bean.Categoria;
import model.ConPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/*
 * DAO della tabella Categoria.
 * Serve per recuperare le categorie usate nel catalogo e nei prodotti.
 */
public class CategoriaDAO {
    // Recupera tutte le categorie, ordinate per nome
    public List<Categoria> doRetrieveAll() {
        List<Categoria> categorie = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Categoria ORDER BY nome"
             );
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
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

    // Recupera solo le categorie che compaiono in almeno un prodotto.
    public List<Categoria> doRetrieveAllUsed() {
        List<Categoria> categorie = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     // DISTINCT per evitare duplicati
                     "SELECT DISTINCT c.* FROM Categoria c JOIN Prodotto_Categoria pc ON c.id = pc.categoria_id ORDER BY c.nome"
             );
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getLong("id"));
                categoria.setNome(rs.getString("nome"));
                categorie.add(categoria);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle categorie utilizzate nel catalogo", e);
        }
        return categorie;
    }

    // Recupera le categorie associate a uno specifico prodotto
    public List<Categoria> doRetrieveByProdotto(long idProdotto) {
        List<Categoria> categorie = new ArrayList<>();
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT c.* FROM Categoria c JOIN Prodotto_Categoria pc ON c.id = pc.categoria_id WHERE pc.prodotto_id = ?"
             )) {
            ps.setLong(1, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getLong("id"));
                    categoria.setNome(rs.getString("nome"));
                    categorie.add(categoria);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle categorie del prodotto con ID: " + idProdotto, e);
        }
        return categorie;
    }

    /*
     * Controlla che tutti gli id ricevuti esistano veramente nel db.
     * Serve nel form admin per evitare categorie inventate o mancanti.
     */
    public boolean doExistsAllByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT COUNT(*) FROM Categoria WHERE id IN (" + placeholders + ")"
             )) {
            int index = 1;
            for (Long id : ids) {
                ps.setLong(index++, id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // se il conteggio coincide con la dimensione del set, allora tutte le categorie richieste esistono
                    return rs.getInt(1) == ids.size();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel controllo delle categorie selezionate", e);
        }
        return false;
    }
}
