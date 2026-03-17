package model.DAO;

import model.Bean.ImmagineProdotto;
import model.Bean.Prodotto;
import model.Bean.ProdottoTaglia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAO {
    public void doSave(Prodotto prodotto) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO Prodotto (nome, descrizione, brand, costo, colore, genere) VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, prodotto.getNome());
                    ps.setString(2, prodotto.getDescrizione());
                    ps.setString(3, prodotto.getBrand());
                    ps.setDouble(4, prodotto.getCosto());
                    ps.setString(5, prodotto.getColore());
                    ps.setString(6, prodotto.getGenere() != null ? prodotto.getGenere() : "Unisex");

                    if (ps.executeUpdate() != 1)
                        throw new RuntimeException("Errore nell'inserimento del prodotto");

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next())
                            prodotto.setId(rs.getLong(1));
                    }
                }

                if (prodotto.getTaglie() != null && !prodotto.getTaglie().isEmpty()) {
                    ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();
                    for (ProdottoTaglia pt : prodotto.getTaglie()) {
                        pt.setIdProdotto(prodotto.getId());
                        ptDAO.doSaveOrUpdate(connection, pt);
                    }
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
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

    public List<Prodotto> doRetrieveByFiltri(String categoria, String q, Double prezzoMin, Double prezzoMax, String genere) {
        return doRetrieveByFiltri(categoria, q, prezzoMin, prezzoMax, genere, "p.nome");
    }

    public List<Prodotto> doRetrieveByFiltriRandom() {
        return doRetrieveByFiltri(null, null, null, null, null, "RAND()");
    }

    public List<Prodotto> doRetrieveByFiltriRandom(String categoria, String q, Double prezzoMin, Double prezzoMax, String genere) {
        return doRetrieveByFiltri(categoria, q, prezzoMin, prezzoMax, genere, "RAND()");
    }

    private List<Prodotto> doRetrieveByFiltri(String categoria, String q, Double prezzoMin, Double prezzoMax,
                                              String genere, String orderBy) {
        List<Prodotto> prodotti = new ArrayList<>();

        boolean hasCategoria = categoria != null && !categoria.isBlank();
        boolean hasQ = q != null && !q.isBlank();
        boolean hasGenere = genere != null && !genere.isBlank();

        StringBuilder sql = new StringBuilder("SELECT DISTINCT p.* FROM Prodotto p ");
        if (hasCategoria)
            sql.append("JOIN Prodotto_Categoria pc ON p.id = pc.prodotto_id ")
                    .append("JOIN Categoria c ON pc.categoria_id = c.id ");

        StringBuilder where = new StringBuilder();
        if (hasCategoria) where.append(where.length() == 0 ? "WHERE " : " AND ").append("c.nome = ?");
        if (hasQ) where.append(where.length() == 0 ? "WHERE " : " AND ").append("(p.nome LIKE ? OR p.brand LIKE ?)");
        if (prezzoMin != null) where.append(where.length() == 0 ? "WHERE " : " AND ").append("p.costo >= ?");
        if (prezzoMax != null) where.append(where.length() == 0 ? "WHERE " : " AND ").append("p.costo <= ?");
        if (hasGenere) where.append(where.length() == 0 ? "WHERE " : " AND ").append("p.genere = ?");

        sql.append(where);
        sql.append(" ORDER BY ").append(orderBy);

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
            if (hasGenere) ps.setString(idx++, genere.trim());

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
        return doRetrieveAll("nome");
    }

    public List<Prodotto> doRetrieveAllRandom() {
        return doRetrieveAll("RAND()");
    }

    private List<Prodotto> doRetrieveAll(String orderBy) {
        List<Prodotto> prodotti = new ArrayList<>();

        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM Prodotto ORDER BY " + orderBy
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
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                ImmagineProdottoDAO ipDAO = new ImmagineProdottoDAO();
                ipDAO.deleteByProdotto(connection, prodotto.getId());
                if (prodotto.getImmagini() != null) {
                    for (ImmagineProdotto ip : prodotto.getImmagini()) {
                        ip.setIdProdotto(prodotto.getId());
                        ipDAO.doSave(connection, ip);
                    }
                }

                ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();
                ptDAO.doDeleteByProdotto(connection, prodotto.getId());
                if (prodotto.getTaglie() != null) {
                    for (ProdottoTaglia pt : prodotto.getTaglie()) {
                        pt.setIdProdotto(prodotto.getId());
                        ptDAO.doSaveOrUpdate(connection, pt);
                    }
                }

                try (PreparedStatement ps = connection.prepareStatement(
                        "UPDATE Prodotto SET nome = ?, descrizione = ?, brand = ?, costo = ?, colore = ?, genere = ? WHERE id = ?")) {

                    ps.setString(1, prodotto.getNome());
                    ps.setString(2, prodotto.getDescrizione());
                    ps.setString(3, prodotto.getBrand());
                    ps.setDouble(4, prodotto.getCosto());
                    ps.setString(5, prodotto.getColore());
                    ps.setString(6, prodotto.getGenere() != null ? prodotto.getGenere() : "Unisex");
                    ps.setLong(7, prodotto.getId());

                    if (ps.executeUpdate() != 1)
                        throw new RuntimeException("Errore nella modifica del prodotto con ID: " + prodotto.getId());
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'aggiornamento del prodotto con ID: " + prodotto.getId(), e);
        }
    }

    public void doDelete(long id) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);

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
        p.setGenere(rs.getString("genere"));

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
