package controller.admin;

import controller.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.bean.*;
import model.dao.*;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "gestioneProdottoAdmin", urlPatterns = "/admin/prodotto")
public class GestioneProdottoAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        Prodotto prodotto = null;
        String titoloPagina = "Nuovo prodotto";
        Set<Long> idCategorieSelezionate = new HashSet<>();

        if (idParam != null && !idParam.isBlank()) {
            long id;
            try {
                id = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/prodotti");
                return;
            }

            prodotto = new ProdottoDAO().doRetrieveByKey(id);
            if (prodotto == null) {
                response.sendRedirect(request.getContextPath() + "/admin/prodotti");
                return;
            }

            titoloPagina = "Modifica prodotto";
            CategoriaDAO categoriaDAO = new CategoriaDAO();
            for (Categoria c : categoriaDAO.doRetrieveByProdotto(prodotto.getId())) {
                idCategorieSelezionate.add(c.getId());
            }
        }

        prepareFormView(request, prodotto, titoloPagina, idCategorieSelezionate, null);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_prodotto.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String idParam = ValidatoreInput.normalizzaTesto(request.getParameter("id"));
        String nome = ValidatoreInput.normalizzaTesto(request.getParameter("nome"));
        String descrizione = ValidatoreInput.normalizzaTesto(request.getParameter("descrizione"));
        String brand = ValidatoreInput.normalizzaTesto(request.getParameter("brand"));
        String colore = ValidatoreInput.normalizzaTesto(request.getParameter("colore"));
        String genere = ValidatoreInput.normalizzaTesto(request.getParameter("genere"));
        String costoParam = ValidatoreInput.normalizzaTesto(request.getParameter("costo"));

        ProdottoDAO prodottoDAO = new ProdottoDAO();
        Prodotto prodotto;
        String titoloPagina = "Nuovo prodotto";

        if (idParam != null && !idParam.isBlank()) {
            long id;
            try {
                id = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/prodotti");
                return;
            }

            prodotto = prodottoDAO.doRetrieveByKey(id);
            if (prodotto == null) {
                response.sendRedirect(request.getContextPath() + "/admin/prodotti");
                return;
            }
            titoloPagina = "Modifica prodotto";
        } else {
            prodotto = new Prodotto();
        }

        if (!ValidatoreInput.contieneTesto(genere)) {
            genere = "Unisex";
        }

        boolean hasError = false;

        if (!ValidatoreInput.isNomeProdottoValido(nome)) {
            request.setAttribute("erroreNome", !ValidatoreInput.contieneTesto(nome)
                    ? "Il nome e obbligatorio."
                    : "Il nome non puo superare 150 caratteri.");
            hasError = true;
        }

        if (!ValidatoreInput.isBrandProdottoValido(brand)) {
            request.setAttribute("erroreBrand", !ValidatoreInput.contieneTesto(brand)
                    ? "Il brand e obbligatorio."
                    : "Il brand non puo superare 100 caratteri.");
            hasError = true;
        }

        if (!ValidatoreInput.isDescrizioneProdottoValida(descrizione)) {
            request.setAttribute("erroreDescrizione", "La descrizione non puo superare 2000 caratteri.");
            hasError = true;
        }

        if (!ValidatoreInput.isColoreProdottoValido(colore)) {
            request.setAttribute("erroreColore", "Il colore non puo superare 50 caratteri.");
            hasError = true;
        }

        if (!ValidatoreInput.isGenereProdottoValido(genere)) {
            request.setAttribute("erroreGenere", "Seleziona un genere valido.");
            hasError = true;
        }

        boolean costoValido = false;
        double costo = 0;
        if (!ValidatoreInput.contieneTesto(costoParam)) {
            request.setAttribute("erroreCosto", "Il costo e obbligatorio.");
            hasError = true;
        } else {
            try {
                costo = Double.parseDouble(costoParam);
                if (costo < 0) {
                    request.setAttribute("erroreCosto", "Il costo non puo essere negativo.");
                    hasError = true;
                } else if (costo > 9999.99) {
                    request.setAttribute("erroreCosto", "Il costo non puo superare 9.999,99 EUR.");
                    hasError = true;
                } else {
                    costoValido = true;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("erroreCosto", "Costo non valido.");
                hasError = true;
            }
        }

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        CategoriaParseResult categoriaResult = parseIdCategorie(request);
        if (categoriaResult.ids.isEmpty()) {
            request.setAttribute("erroreCategorie", "Seleziona almeno una categoria.");
            hasError = true;
        } else if (categoriaResult.hasInvalidValues || !categoriaDAO.doExistsAllByIds(categoriaResult.ids)) {
            request.setAttribute("erroreCategorie", "Le categorie selezionate non sono valide.");
            hasError = true;
        }

        TaglieParseResult taglieResult = parseTaglie(request, prodotto.getId());
        if (taglieResult.hasInvalidValues) {
            request.setAttribute("erroreTaglie", "Le quantita devono essere numeri interi maggiori o uguali a zero.");
            hasError = true;
        } else if (!taglieResult.hasPositiveQuantity) {
            request.setAttribute("erroreTaglie", "Inserisci almeno una taglia con quantita disponibile maggiore di zero.");
            hasError = true;
        }

        prodotto.setNome(nome);
        prodotto.setDescrizione(descrizione);
        prodotto.setBrand(brand);
        prodotto.setColore(colore);
        prodotto.setGenere(genere);
        prodotto.setTaglie(taglieResult.taglie);

        if (costoValido) {
            prodotto.setCosto(costo);
        }

        if (hasError) {
            request.setAttribute("formCosto", costoParam);
            prepareFormView(request, prodotto, titoloPagina, categoriaResult.ids, taglieResult.valoriForm);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_prodotto.jsp").forward(request, response);
            return;
        }

        prodotto.setCosto(costo);

        if (prodotto.getId() > 0) {
            prodottoDAO.doUpdate(prodotto);
            new ProdottoCategoriaDAO().doReplace(prodotto.getId(), categoriaResult.ids);
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        prodottoDAO.doSave(prodotto);
        new ProdottoCategoriaDAO().doReplace(prodotto.getId(), categoriaResult.ids);
        response.sendRedirect(request.getContextPath() + "/admin/prodotto?id=" + prodotto.getId());
    }

    private Map<Integer, String> buildQuantitaPerTaglia(Prodotto prodotto) {
        Map<Integer, String> mappa = new LinkedHashMap<>();
        for (int t : ProdottoTagliaDAO.TAGLIE_DISPONIBILI) {
            mappa.put(t, "0");
        }

        if (prodotto != null && prodotto.getTaglie() != null) {
            for (ProdottoTaglia pt : prodotto.getTaglie()) {
                if (pt != null) {
                    mappa.put(pt.getTaglia(), String.valueOf(pt.getQuantita()));
                }
            }
        }

        return mappa;
    }

    private CategoriaParseResult parseIdCategorie(HttpServletRequest request) {
        Set<Long> ids = new HashSet<>();
        boolean hasInvalidValues = false;
        String[] values = request.getParameterValues("categoria_id");
        if (values != null) {
            for (String v : values) {
                try {
                    ids.add(Long.parseLong(v.trim()));
                } catch (Exception e) {
                    hasInvalidValues = true;
                }
            }
        }
        return new CategoriaParseResult(ids, hasInvalidValues);
    }

    private TaglieParseResult parseTaglie(HttpServletRequest request, long idProdotto) {
        List<ProdottoTaglia> taglie = new ArrayList<>();
        Map<Integer, String> valoriForm = new LinkedHashMap<>();
        boolean hasInvalidValues = false;
        boolean hasPositiveQuantity = false;

        for (int taglia : ProdottoTagliaDAO.TAGLIE_DISPONIBILI) {
            String raw = request.getParameter("q_" + taglia);
            String valore = raw == null ? "" : raw.trim();
            if (valore.isEmpty()) {
                valore = "0";
            }
            valoriForm.put(taglia, valore);

            int quantita;
            try {
                quantita = Integer.parseInt(valore);
            } catch (NumberFormatException e) {
                hasInvalidValues = true;
                continue;
            }

            if (quantita < 0) {
                hasInvalidValues = true;
                continue;
            }

            if (quantita > 0) {
                hasPositiveQuantity = true;
            }

            ProdottoTaglia pt = new ProdottoTaglia();
            pt.setIdProdotto(idProdotto);
            pt.setTaglia(taglia);
            pt.setQuantita(quantita);
            taglie.add(pt);
        }

        return new TaglieParseResult(taglie, valoriForm, hasInvalidValues, hasPositiveQuantity);
    }

    private void prepareFormView(HttpServletRequest request, Prodotto prodotto, String titoloPagina,
                                 Set<Long> idCategorieSelezionate, Map<Integer, String> quantitaPerTaglia) {
        request.setAttribute("prodotto", prodotto);
        request.setAttribute("titoloPagina", titoloPagina);
        request.setAttribute("taglieDisponibili", ProdottoTagliaDAO.TAGLIE_DISPONIBILI);
        request.setAttribute("quantitaPerTaglia",
                quantitaPerTaglia != null ? quantitaPerTaglia : buildQuantitaPerTaglia(prodotto));
        request.setAttribute("tutteCategorie", new CategoriaDAO().doRetrieveAll());
        request.setAttribute("idCategorieSelezionate",
                idCategorieSelezionate != null ? idCategorieSelezionate : Collections.emptySet());

        if (prodotto != null && prodotto.getId() > 0) {
            request.setAttribute("immagini", new ImmagineProdottoDAO().doRetrieveByProdotto(prodotto.getId()));
            List<Recensione> recensioni = new RecensioneDAO().doRetrieveByProdotto(prodotto.getId());
            request.setAttribute("recensioni", recensioni);
            request.setAttribute("emailUtenti", buildEmailUtenti(recensioni));
        }
    }

    private Map<Long, String> buildEmailUtenti(List<Recensione> recensioni) {
        UtenteDAO utenteDAO = new UtenteDAO();
        Map<Long, String> emailUtenti = new HashMap<>();
        for (Recensione rec : recensioni) {
            if (!emailUtenti.containsKey(rec.getIdUtente())) {
                Utente u = utenteDAO.doRetrieveByKey(rec.getIdUtente());
                emailUtenti.put(rec.getIdUtente(), u != null ? u.getEmail() : "-");
            }
        }
        return emailUtenti;
    }

    private static final class CategoriaParseResult {
        private final Set<Long> ids;
        private final boolean hasInvalidValues;

        private CategoriaParseResult(Set<Long> ids, boolean hasInvalidValues) {
            this.ids = ids;
            this.hasInvalidValues = hasInvalidValues;
        }
    }

    private static final class TaglieParseResult {
        private final List<ProdottoTaglia> taglie;
        private final Map<Integer, String> valoriForm;
        private final boolean hasInvalidValues;
        private final boolean hasPositiveQuantity;

        private TaglieParseResult(List<ProdottoTaglia> taglie, Map<Integer, String> valoriForm,
                                  boolean hasInvalidValues, boolean hasPositiveQuantity) {
            this.taglie = taglie;
            this.valoriForm = valoriForm;
            this.hasInvalidValues = hasInvalidValues;
            this.hasPositiveQuantity = hasPositiveQuantity;
        }
    }
}
