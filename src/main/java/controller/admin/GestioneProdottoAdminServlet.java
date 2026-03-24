package controller.admin;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Bean.*;
import model.DAO.*;

import java.io.IOException;
import java.util.*;

/*
 * Gestisce la creazione e la modifica di un prodotto nel pannello admin.
 * Si occupa anche di gestire le categorie, le taglie e l'upload dell'immagine.
 */
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
@WebServlet(name = "gestioneProdottoAdmin", urlPatterns = "/admin/prodotto")
public class GestioneProdottoAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // legge l'id del prodotto dalla richiesta per capire se è una modifica
        String idParam = request.getParameter("id");
        Prodotto prodotto = null;
        String titoloPagina = "Nuovo prodotto";
        Set<Long> idCategorieSelezionate = new HashSet<>();

        // se è presente l'id, carica dal db il prodotto associato
        if (idParam != null && !idParam.isBlank()) {
            long id;
            try {
                id = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                // se l'id non è valido, ritorna alla pagina di gestione dei prodotti
                response.sendRedirect(request.getContextPath() + "/admin/prodotti");
                return;
            }
            // recupero del prodotto dal db
            prodotto = new ProdottoDAO().doRetrieveByKey(id);
            if (prodotto == null) {
                // se il prodotto non esiste, ritorna alla pagina di gestione dei prodotti
                response.sendRedirect(request.getContextPath() + "/admin/prodotti");
                return;
            }
            titoloPagina = "Modifica prodotto";
            // recupera le categorie già associate al prodotto
            CategoriaDAO categoriaDAO = new CategoriaDAO();
            for (Categoria c : categoriaDAO.doRetrieveByProdotto(prodotto.getId())) {
                idCategorieSelezionate.add(c.getId());
            }
        }
        // helper privato per preparare tutti i dati necessari al form
        caricaDatiForm(request, prodotto, titoloPagina, idCategorieSelezionate, null);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_prodotto.jsp").forward(request, response);
    }

    // Elabora il form: valida i campi, salva o aggiorna il prodotto, gestisce categorie e immagine.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // normalizza i parametri testuali inviati dal form
        String idParam = ValidatoreInput.normalizzaTesto(request.getParameter("id"));
        String nome = ValidatoreInput.normalizzaTesto(request.getParameter("nome"));
        String descrizione = ValidatoreInput.normalizzaTesto(request.getParameter("descrizione"));
        String brand = ValidatoreInput.normalizzaTesto(request.getParameter("brand"));
        String colore = ValidatoreInput.normalizzaTesto(request.getParameter("colore"));
        String genere = ValidatoreInput.normalizzaTesto(request.getParameter("genere"));
        String costoParam = ValidatoreInput.normalizzaTesto(request.getParameter("costo"));

        // distingue tra aggiunta prodotto e modifica in base alla presenza dell'id
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        Prodotto prodotto;
        String titoloPagina = "Nuovo prodotto";
        if (idParam != null && !idParam.isBlank()) {
            long id;
            try {
                id = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                // se l'id non è valido, ritorna alla pagina di gestione prodotti
                response.sendRedirect(request.getContextPath() + "/admin/prodotti");
                return;
            }
            // recupero del prodotto da modificare
            prodotto = prodottoDAO.doRetrieveByKey(id);
            if (prodotto == null) {
                // se il prodotto non esiste, ritorna alla pagina di gestione prodotti
                response.sendRedirect(request.getContextPath() + "/admin/prodotti");
                return;
            }
            titoloPagina = "Modifica prodotto";
        } else {
            // se non c'è un id, allora crea un nuovo oggetto prodotto
            prodotto = new Prodotto();
        }

        // se il genere non è stato selezionato, usa come valore di default "unisex"
        if (!ValidatoreInput.contieneTesto(genere)) {
            genere = "Unisex";
        }

        boolean hasError = false;

        // validazione del nome
        if (!ValidatoreInput.isNomeProdottoValido(nome)) {
            request.setAttribute("erroreNome", !ValidatoreInput.contieneTesto(nome)
                    ? "Il nome e obbligatorio."
                    : "Il nome non puo superare 150 caratteri.");
            hasError = true;
        }
        // validazione del brand
        if (!ValidatoreInput.isBrandProdottoValido(brand)) {
            request.setAttribute("erroreBrand", !ValidatoreInput.contieneTesto(brand)
                    ? "Il brand e obbligatorio."
                    : "Il brand non puo superare 100 caratteri.");
            hasError = true;
        }
        // validazione della descrizione
        if (!ValidatoreInput.isDescrizioneProdottoValida(descrizione)) {
            request.setAttribute("erroreDescrizione", "La descrizione non puo superare 2000 caratteri.");
            hasError = true;
        }
        // validazione del colore
        if (!ValidatoreInput.isColoreProdottoValido(colore)) {
            request.setAttribute("erroreColore", "Il colore non puo superare 50 caratteri.");
            hasError = true;
        }
        // validazione del genere
        if (!ValidatoreInput.isGenereProdottoValido(genere)) {
            request.setAttribute("erroreGenere", "Seleziona un genere valido.");
            hasError = true;
        }

        // parsing del costo e controllo del range accettato
        boolean costoValido = false;
        double costo = 0;
        if (!ValidatoreInput.contieneTesto(costoParam)) {
            request.setAttribute("erroreCosto", "Il costo e obbligatorio.");
            hasError = true;
        } else {
            try {
                costo = Double.parseDouble(costoParam);
                if (costo < 0) { // il costo non può essere negativo
                    request.setAttribute("erroreCosto", "Il costo non puo essere negativo.");
                    hasError = true;
                } else if (costo > 9999.99) { // controlla che il costo non superi il limite massimo ragionevole previsto
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

        // controlla che sia stata selezionata almeno una categoria valida
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        Set<Long> idCategorie = new HashSet<>();
        boolean hasInvalidCategorie = false;
        String[] categorieValues = request.getParameterValues("categoria_id");
        // legge tutte le categorie inviate dal form
        if (categorieValues != null) {
            for (String v : categorieValues) {
                try {
                    idCategorie.add(Long.parseLong(v.trim()));
                } catch (Exception e) {
                    // segnala se almeno una categoria non è convertibile
                    hasInvalidCategorie = true;
                }
            }
        }
        // controlla che sia stata selezionata almeno una categoria
        if (idCategorie.isEmpty()) {
            request.setAttribute("erroreCategorie", "Seleziona almeno una categoria.");
            hasError = true;
        } else if (hasInvalidCategorie || !categoriaDAO.doExistsAllByIds(idCategorie)) {
            // controlla che tutte le categorie selezionate esistano davvero nel db
            request.setAttribute("erroreCategorie", "Le categorie selezionate non sono valide.");
            hasError = true;
        }

        List<ProdottoTaglia> taglie = new ArrayList<>();
        Map<Integer, String> valoriFormTaglie = new LinkedHashMap<>();
        boolean hasInvalidTaglie = false;
        boolean hasPositiveQuantity = false;
        // scorre tutte le taglie disponibili previste dal sistema
        for (int taglia : ProdottoTagliaDAO.TAGLIE_DISPONIBILI) {
            String raw = request.getParameter("q_" + taglia);
            String valore = raw == null ? "" : raw.trim();
            // se il campo è vuoto, considera la quantità pari a 0
            if (valore.isEmpty()) {
                valore = "0";
            }
            // salva il valore inserito per reinserirlo nel form in caso di errore
            valoriFormTaglie.put(taglia, valore);
            int quantita;
            // parsing della quantità
            try {
                quantita = Integer.parseInt(valore);
            } catch (NumberFormatException e) {
                hasInvalidTaglie = true;
                continue;
            }
            // la quantità non può essere negativa
            if (quantita < 0) {
                hasInvalidTaglie = true;
                continue;
            }
            // controlla se esiste almeno una taglia con disponibilità positiva
            if (quantita > 0) {
                hasPositiveQuantity = true;
            }
            // costruisce l'associazione taglia-quantità del prodotto
            ProdottoTaglia pt = new ProdottoTaglia();
            pt.setIdProdotto(prodotto.getId());
            pt.setTaglia(taglia);
            pt.setQuantita(quantita);
            taglie.add(pt);
        }
        if (hasInvalidTaglie) { // segnala errore se ci sono quantità non valide
            request.setAttribute("erroreTaglie", "Le quantita devono essere numeri interi maggiori o uguali a zero.");
            hasError = true;
        } else if (!hasPositiveQuantity) { // segnala errore se tutte le quantità sono 0
            request.setAttribute("erroreTaglie", "Inserisci almeno una taglia con quantita disponibile maggiore di zero.");
            hasError = true;
        }

        // precarica i dati nell'oggetto prodotto (utili anche in caso di errore per rimostrare il form)
        prodotto.setNome(nome);
        prodotto.setDescrizione(descrizione);
        prodotto.setBrand(brand);
        prodotto.setColore(colore);
        prodotto.setGenere(genere);
        prodotto.setTaglie(taglie);
        // assegna il costo solo se è stato validato correttamente
        if (costoValido) {
            prodotto.setCosto(costo);
        }

        // se ci sono errori, rimostra il form con i dati già inseriti
        if (hasError) {
            request.setAttribute("formCosto", costoParam);
            caricaDatiForm(request, prodotto, titoloPagina, idCategorie, valoriFormTaglie);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_prodotto.jsp").forward(request, response);
            return;
        }

        // il costo è stato validato correttamente, quindi lo assegna al prodotto
        prodotto.setCosto(costo);

        // se il prodotto esiste già, aggiorna i dati nel db
        if (prodotto.getId() > 0) {
            prodottoDAO.doUpdate(prodotto);
            // aggiorna le categorie associate al prodotto
            new ProdottoCategoriaDAO().doReplace(prodotto.getId(), idCategorie);
            // helper privato che salva l'immagine, se è stata caricata
            salvaImmagine(request, prodotto.getId());
            // messaggio di conferma della modifica salvato in sessione
            request.getSession().setAttribute("flashSuccesso", "Prodotto aggiornato con successo");
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        // se invece il prodotto è nuovo, lo aggiunge al db
        prodottoDAO.doSave(prodotto);
        // associa le categorie selezionate al nuovo prodotto
        new ProdottoCategoriaDAO().doReplace(prodotto.getId(), idCategorie);
        salvaImmagine(request, prodotto.getId());
        // messaggio di conferma dell'aggiunta salvato in sessione
        request.getSession().setAttribute("flashSuccesso", "Prodotto creato con successo");
        response.sendRedirect(request.getContextPath() + "/admin/prodotto?id=" + prodotto.getId());
    }

    // Helper che salva l'immagine del prodotto se presente e se è un tipo immagine valido.
    private void salvaImmagine(HttpServletRequest request, long idProdotto) throws ServletException, IOException {
        // recupera il file inviato dal form
        Part part = request.getPart("fileImmagine");
        if (part == null || part.getSize() == 0) return;
        // legge il content type dal file
        String contentType = part.getContentType();
        // controllo per accettare solo file immagine
        if (contentType == null || !contentType.startsWith("image/")) return;

        // legge tutti i byte del file caricato
        byte[] bytes = part.getInputStream().readAllBytes();
        // sceglie l'estensione in base al tipo dell'immagine
        String ext = contentType.contains("png") ? ".png" : contentType.contains("webp") ? ".webp" : ".jpg";
        // costruzione standard del nome del file da salvare
        String nomeFile = "prodotto_" + idProdotto + ext;
        // gestisce la cartella fisica di upload
        String uploadDir = getServletContext().getRealPath("/uploads/prodotti");
        // imposta il path per salvare l'immagine
        String imgPath = "/uploads/prodotti/" + nomeFile;
        // salva il file e il riferimento dell'immagine nel db
        new ImmagineProdottoDAO().doSaveWithFile(idProdotto, bytes, uploadDir, nomeFile, imgPath);
    }

    /*
     * Helper per caricare nella request tutti i dati necessari per mostrare il form.
     * In modalità modifica, mostra anche le recensioni e le email degli utenti.
     */
    private void caricaDatiForm(HttpServletRequest request, Prodotto prodotto, String titoloPagina, Set<Long> idCategorieSelezionate, Map<Integer, String> quantitaPerTaglia) {
        request.setAttribute("prodotto", prodotto);
        request.setAttribute("titoloPagina", titoloPagina);
        request.setAttribute("taglieDisponibili", ProdottoTagliaDAO.TAGLIE_DISPONIBILI);

        // se non è stata passata una mappa delle quantità, la costruisce da zero
        if (quantitaPerTaglia == null) {
            Map<Integer, String> mappa = new LinkedHashMap<>();
            // inizializza tutte le taglie con quantità 0
            for (int t : ProdottoTagliaDAO.TAGLIE_DISPONIBILI) {
                mappa.put(t, "0");
            }
            // se il prodotto ha già delle taglie, sovrascrive i valori presenti
            if (prodotto != null && prodotto.getTaglie() != null) {
                for (ProdottoTaglia pt : prodotto.getTaglie()) {
                    if (pt != null) {
                        mappa.put(pt.getTaglia(), String.valueOf(pt.getQuantita()));
                    }
                }
            }
            quantitaPerTaglia = mappa;
        }
        request.setAttribute("quantitaPerTaglia", quantitaPerTaglia);
        request.setAttribute("tutteCategorie", new CategoriaDAO().doRetrieveAll());
        request.setAttribute("idCategorieSelezionate",
                idCategorieSelezionate != null ? idCategorieSelezionate : Collections.emptySet());

        // se è in modalità modifica, carica le recensioni del prodotto e le email degli autori
        if (prodotto != null && prodotto.getId() > 0) {
            List<Recensione> recensioni = new RecensioneDAO().doRetrieveByProdotto(prodotto.getId());
            request.setAttribute("recensioni", recensioni);
            UtenteDAO utenteDAO = new UtenteDAO();
            // serve per associare ogni recensione alla mail del suo autore
            Map<Long, String> emailUtenti = new HashMap<>();
            // per evitare query duplicate per gli utenti che hanno scritto più di una recensione
            for (Recensione rec : recensioni) {
                if (!emailUtenti.containsKey(rec.getIdUtente())) {
                    Utente u = utenteDAO.doRetrieveByKey(rec.getIdUtente());
                    emailUtenti.put(rec.getIdUtente(), u != null ? u.getEmail() : "-");
                }
            }
            request.setAttribute("emailUtenti", emailUtenti);
        }
    }
}
