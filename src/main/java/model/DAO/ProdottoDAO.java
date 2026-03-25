package model.DAO;

import model.Bean.Prodotto;
import model.Bean.ProdottoTaglia;
import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * DAO per la tabella Prodotto.
 * Gestisce inserimento, recupero, modifica e cancellazione dei prodotti.
 */
public class ProdottoDAO {
    /*
     * Salva un nuovo prodotto con una transazione manuale.
     * Prima inserisce il prodotto principale poi salva le taglie collegate.
     */
    public void doSave(Prodotto prodotto) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // inserisce prima la riga del prodotto e recupera l'id generato
                // in modo da poterlo usare per salvare le taglie
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
                    // salva l'id generato direttamente nel bean
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next())
                            prodotto.setId(rs.getLong(1));
                    }
                }
                // salva anche le taglie disponibili usando la stessa connessione
                // così resta tutto sulla stessa transazione
                if (prodotto.getTaglie() != null && !prodotto.getTaglie().isEmpty()) {
                    ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();
                    for (ProdottoTaglia pt : prodotto.getTaglie()) {
                        pt.setIdProdotto(prodotto.getId());
                        ptDAO.doSaveOrUpdate(connection, pt);
                    }
                }
                // se il prodotto e le taglie sono stati salvati correttamente, conferma
                connection.commit();
            } catch (Exception e) {
                // se un solo passaggio fallisce, tutto si annulla
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del prodotto", e);
        }
    }

    // recupera un prodotto tramite il suo id
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

    // recupera i prodotti filtrati ordinati per id
    public List<Prodotto> doRetrieveByFiltriRandom(String categoria, String testoRicerca, Double prezzoMin, Double prezzoMax, String genere) {
        // flag per capire quale filtro è impostato
        boolean hasCategoria = categoria != null && !categoria.isBlank();
        boolean hasTestoRicerca = testoRicerca != null && !testoRicerca.isBlank();
        boolean hasGenere = genere != null && !genere.isBlank();

        // raccoglie le condizioni WHERE attive e le unisce con AND
        List<String> condizioni = new ArrayList<>();
        if (hasCategoria) condizioni.add("c.nome = ?");
        if (hasTestoRicerca) condizioni.add("(p.nome LIKE ? OR p.brand LIKE ?)");
        if (prezzoMin != null) condizioni.add("p.costo >= ?");
        if (prezzoMax != null) condizioni.add("p.costo <= ?");
        if (hasGenere) condizioni.add("p.genere = ?");

        // il JOIN su Categoria si aggiunge solo se il filtro è anche per categoria, per evitare righe duplicate
        String joinCategoria = hasCategoria
                ? "JOIN Prodotto_Categoria pc ON p.id = pc.prodotto_id JOIN Categoria c ON pc.categoria_id = c.id " : "";
        // costruisce la clausola where composta dai filtri presenti
        String clausolaWhere = condizioni.isEmpty() ? "" : "WHERE " + String.join(" AND ", condizioni);
        String query = "SELECT DISTINCT p.* FROM Prodotto p " + joinCategoria + clausolaWhere + " ORDER BY p.id";

        List<Prodotto> prodotti = new ArrayList<>();
        // prepara i parametri della query in base ai filtri attivi, la esegue e costruisce la lista dei prodotti trovati
        try (Connection connection = ConPool.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            int idx = 1;

            if (hasCategoria) ps.setString(idx++, categoria.trim());

            if (hasTestoRicerca) {
                String pattern = "%" + testoRicerca.trim() + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }

            if (prezzoMin != null) ps.setDouble(idx++, prezzoMin);
            if (prezzoMax != null) ps.setDouble(idx++, prezzoMax);
            if (hasGenere) ps.setString(idx, genere.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    prodotti.add(buildProdotto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei prodotti con filtri", e);
        }
        return prodotti;
    }

    // Recupera un numero fisso di prodotti casuali da mostrare nella pagina home
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

    // wrapper per recuperare tutti i prodotti ordinati per nome (usato nell' area admin)
    public List<Prodotto> doRetrieveAll() {
        return doRetrieveAll("nome");
    }

    // wrapper per recuperare tutti i prodotti ordinati per id
    public List<Prodotto> doRetrieveAllRandom() {
        return doRetrieveAll("id");
    }

    // Recupera tutti i prodotti in base a un ordinamento scelto dal parametro
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

    // aggiorna un prodotto esistente con una transazione manuale
    public void doUpdate(Prodotto prodotto) {
        try (Connection connection = ConPool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // cancella tutte le taglie vecchie e poi salva quelle nuove nell'oggetto
                ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();
                ptDAO.doDeleteByProdotto(connection, prodotto.getId());
                if (prodotto.getTaglie() != null) {
                    for (ProdottoTaglia pt : prodotto.getTaglie()) {
                        pt.setIdProdotto(prodotto.getId());
                        ptDAO.doSaveOrUpdate(connection, pt);
                    }
                }
                // aggiorna il resto dei dati principali del prodotto
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
                // se qualcosa fallisce, si annulla sia l'update che la gestione delle taglie
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'aggiornamento del prodotto con ID: " + prodotto.getId(), e);
        }
    }

    // Cancella un prodotto dal db.
    public void doDelete(long id) {
        try (Connection connection = ConPool.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM Prodotto WHERE id = ?")) {
            ps.setLong(1, id);
            if (ps.executeUpdate() != 1)
                throw new RuntimeException("Nessun prodotto trovato da eliminare");
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'eliminazione del prodotto con ID: " + id, e);
        }
    }

    // Helper per costruire un prodotto completo a partire dal ResultSet
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
        // carica anche l'immagine
        String imgPath = new ImmagineProdottoDAO().doRetrievePrimaImmagine(id);
        if (imgPath != null) p.setImgPath(imgPath);
        // le taglie
        p.setTaglie(new ProdottoTagliaDAO().doRetrieveDisponibilitaByProdotto(id));
        // e le categorie
        p.setCategorie(new CategoriaDAO().doRetrieveByProdotto(id));
        return p;
    }
}
