package model.dao;

import model.Bean.*;
import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ProdottoDAO {
    public void doSave(Prodotto prodotto) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Prodotto (nome, descrizione, brand, costo, colore) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, prodotto.getNome());
                ps.setString(2, prodotto.getDescrizione());
                ps.setString(3, prodotto.getBrand());
                ps.setDouble(4, prodotto.getCosto());
                ps.setString(5, prodotto.getColore());

                if (ps.executeUpdate() != 1)
                    throw new RuntimeException("Errore nell'inserimento del prodotto");

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next())
                        prodotto.setId(rs.getLong(1));
                }
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del prodotto", e);
        }
    }

    public Prodotto doRetrieveByKey(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Prodotto WHERE id = ?"
             )) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return buildProdotto(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del prodotto con ID: " + id, e);
        }
        return null;
    }

    public List<Prodotto> doRetrieveByFiltri(String categoria, String q, Double prezzoMin, Double prezzoMax) {
        List<Prodotto> prodotti = new ArrayList<>();

        boolean hasCategoria = categoria != null && !categoria.isBlank();
        boolean hasQ         = q != null && !q.isBlank();

        StringBuilder sql = new StringBuilder("SELECT DISTINCT p.* FROM Prodotto p ");
        if (hasCategoria)
            sql.append("JOIN Prodotto_Categoria pc ON p.id = pc.prodotto_id ")
               .append("JOIN Categoria c ON pc.categoria_id = c.id ");

        StringJoiner where = new StringJoiner(" AND ");
        if (hasCategoria)  where.add("c.nome = ?");
        if (hasQ)          where.add("(p.nome LIKE ? OR p.brand LIKE ?)");
        if (prezzoMin != null) where.add("p.costo >= ?");
        if (prezzoMax != null) where.add("p.costo <= ?");

        if (where.length() > 0)
            sql.append("WHERE ").append(where);
        sql.append(" ORDER BY p.nome");

        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int idx = 1;
            if (hasCategoria) ps.setString(idx++, categoria.trim());
            if (hasQ) {
                String pattern = "%" + q.trim() + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }
            if (prezzoMin != null) ps.setDouble(idx++, prezzoMin);
            if (prezzoMax != null) ps.setDouble(idx++, prezzoMax);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    prodotti.add(buildProdotto(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei prodotti con filtri", e);
        }

        return prodotti;
    }

    public List<Prodotto> doRetrieveProdottiInEvidenza(int numero) {
        if (numero <= 0)
            throw new RuntimeException("Il numero fornito deve essere positivo");

        List<Prodotto> prodottiInEvidenza = new ArrayList<>(numero);

        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Prodotto ORDER BY RAND() LIMIT ?"
             )) {

            ps.setInt(1, numero);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    prodottiInEvidenza.add(buildProdotto(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei prodotti in evidenza", e);
        }

        return prodottiInEvidenza;
    }

    public List<Prodotto> doRetrieveAll() {
        List<Prodotto> prodotti = new ArrayList<>();

        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Prodotto ORDER BY nome"
             )) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    prodotti.add(buildProdotto(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero di tutti i prodotti", e);
        }

        return prodotti;
    }

    public void doUpdate(Prodotto prodotto) {
        new ImmagineProdottoDAO().deleteByProdotto(prodotto.getId());
        if (prodotto.getImmagini() != null) {
            ImmagineProdottoDAO ipDAO = new ImmagineProdottoDAO();
            for (ImmagineProdotto ip : prodotto.getImmagini()) {
                ip.setIdProdotto(prodotto.getId());
                ipDAO.doSave(ip);
            }
        }

        new ProdottoTagliaDAO().doDeleteByProdotto(prodotto.getId());
        if (prodotto.getTaglie() != null) {
            ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();
            for (ProdottoTaglia pt : prodotto.getTaglie()) {
                pt.setIdProdotto(prodotto.getId());
                ptDAO.doSaveOrUpdate(pt);
            }
        }

        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE Prodotto SET nome = ?, descrizione = ?, brand = ?, costo = ?, colore = ? WHERE id = ?")) {

            ps.setString(1, prodotto.getNome());
            ps.setString(2, prodotto.getDescrizione());
            ps.setString(3, prodotto.getBrand());
            ps.setDouble(4, prodotto.getCosto());
            ps.setString(5, prodotto.getColore());
            ps.setLong(6, prodotto.getId());

            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Errore nella modifica del prodotto con ID: " + prodotto.getId());

        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'aggiornamento del prodotto con ID: " + prodotto.getId(), e);
        }
    }

    public void doDelete(long id) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);

            // Elimina prima le tabelle collegate
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Dettaglio_Ordine WHERE prodotto_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Wishlist WHERE prodotto_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Recensione WHERE prodotto_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Prodotto_Categoria WHERE prodotto_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Prodotto_Taglia WHERE prodotto_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Immagine_Prodotto WHERE prodotto_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            int rows;
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Prodotto WHERE id = ?")) {
                ps.setLong(1, id);
                rows = ps.executeUpdate();
            }

            if (rows != 1) {
                connection.rollback();
                throw new RuntimeException("Nessun prodotto trovato da eliminare");
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'eliminazione del prodotto con ID: " + id, e);
        }
    }

    protected Prodotto buildProdotto(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto();
        p.setId(rs.getLong("id"));
        p.setNome(rs.getString("nome"));
        p.setDescrizione(rs.getString("descrizione"));
        p.setBrand(rs.getString("brand"));
        p.setCosto(rs.getDouble("costo"));
        p.setColore(rs.getString("colore"));

        long id = p.getId();

        ImmagineProdottoDAO ipDAO = new ImmagineProdottoDAO();
        List<ImmagineProdotto> immagini = ipDAO.doRetrieveByProdotto(id);
        p.setImmagini(immagini);

        if (!immagini.isEmpty()) {
            String imgPath = immagini.get(0).getImgPath();
            if (imgPath != null) p.setImgPath(imgPath);
        }

        p.setTaglie(new ProdottoTagliaDAO().doRetrieveDisponibilitaByProdotto(id));
        p.setCategorie(new CategoriaDAO().doRetrieveByProdotto(id));

        return p;
    }
}
