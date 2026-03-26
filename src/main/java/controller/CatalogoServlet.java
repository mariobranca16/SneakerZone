package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Prodotto;
import model.DAO.CategoriaDAO;
import model.DAO.ProdottoDAO;

import java.io.IOException;
import java.util.*;

/*
 * Mostra il catalogo prodotti con filtri per categoria, testo, prezzo e genere.
 * L'ordine dei prodotti è "casuale" ma stabile: viene calcolato una sola volta
 * e salvato nel ServletContext, così non cambia tra una pagina e l'altra.
 * Supporta richieste AJAX per aggiornare solo la lista senza ricaricare la pagina.
 */
@WebServlet(name = "catalogo", urlPatterns = "/catalogo")
public class CatalogoServlet extends HttpServlet {

    // attributo salvato nel ServletContext per l'ordine casuale dei prodotti nel catalogo
    private static final String ORDINE_KEY = "catalogoOrdine";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // legge dalla request i parametri usati come filtri nel catalogo
        String categoria = request.getParameter("categoria");
        String testoRicerca = request.getParameter("q");
        String prezzoMinP = request.getParameter("prezzoMin");
        String prezzoMaxP = request.getParameter("prezzoMax");
        String genere = request.getParameter("genere");

        Double prezzoMin = null;
        // validazione e parsing del filtro del prezzo minimo
        if (prezzoMinP != null && !prezzoMinP.isBlank()) {
            try {
                double v = Double.parseDouble(prezzoMinP.trim());
                if (v >= 0) prezzoMin = v;
            } catch (NumberFormatException ignored) {
            }
        }

        Double prezzoMax = null;
        // validazione e parsing del filtro del prezzo massimo
        if (prezzoMaxP != null && !prezzoMaxP.isBlank()) {
            try {
                double v = Double.parseDouble(prezzoMaxP.trim());
                if (v >= 0) prezzoMax = v;
            } catch (NumberFormatException ignored) {
            }
        }

        ProdottoDAO prodottoDAO = new ProdottoDAO();
        boolean anyFilter = (categoria != null && !categoria.isBlank())
                || (testoRicerca != null && !testoRicerca.isBlank())
                || prezzoMin != null || prezzoMax != null
                || (genere != null && !genere.isBlank());

        // se ci sono filtri attivi usa il metodo apposito, altrimenti recupera tutti i prodotti
        List<Prodotto> prodotti = anyFilter
                ? prodottoDAO.doRetrieveByFiltriRandom(categoria, testoRicerca, prezzoMin, prezzoMax, genere)
                : prodottoDAO.doRetrieveAllRandom();

        // Applica l'ordinamento casuale dei prodotti recuperati
        ordinaProdotti(prodotti);

        // Salvataggio dei prodotti, dei filtri impostati e delle categorie rilevate
        request.setAttribute("prodotti", prodotti);
        request.setAttribute("tutteCategorie", new CategoriaDAO().doRetrieveAllUsed());
        request.setAttribute("filtroCategoria", categoria);
        request.setAttribute("filtroQ", testoRicerca);
        request.setAttribute("filtroPrezzoMin", prezzoMinP);
        request.setAttribute("filtroPrezzoMax", prezzoMaxP);
        request.setAttribute("filtroGenere", genere);

        // recupera la sessione senza crearne una nuova se non esiste
        HttpSession session = request.getSession(false);
        if (session != null) {
            // recupera un eventuale messaggio di errore relativo al carrello
            String flashErrore = (String) session.getAttribute("flash_erroreCarrello");
            if (flashErrore != null) {
                request.setAttribute("erroreCarrello", flashErrore);
                session.removeAttribute("flash_erroreCarrello");
            }
            // recupera un eventuale messaggio generico di successo
            String flashMessaggio = (String) session.getAttribute("flash_messaggio");
            if (flashMessaggio != null) {
                request.setAttribute("messaggio", flashMessaggio);
                session.removeAttribute("flash_messaggio");
            }
        }

        // Messaggio di successo di aggiunta prodotto alla wishlist tramite parametro dell'url
        if ("1".equals(request.getParameter("successoWishlist")))
            request.setAttribute("messaggio", "Prodotto aggiunto alla wishlist");

        // controlla se la richiesta arriva tramite AJAX
        if ("1".equals(request.getParameter("ajax"))) {
            // in questo caso restituisce solo il frammento con i risultati del catalogo
            request.getRequestDispatcher("/WEB-INF/jsp/catalogo_risultati.jsp").forward(request, response);
            return;
        }
        // se invece la richiesta non arriva tramite AJAX, mostra l'intera pagina del catalogo
        request.getRequestDispatcher("/WEB-INF/jsp/catalogo.jsp").forward(request, response);
    }

    // metodo privato per ordinare i prodotti nel catalogo secondo un ordine casuale stabile salvato nel contesto applicativo
    private void ordinaProdotti(List<Prodotto> prodotti) {
        @SuppressWarnings("unchecked")
        // recupera l'ordinamento presente nel contesto, salvato tramite l'attributo
        List<Long> ordine = (List<Long>) getServletContext().getAttribute(ORDINE_KEY);

        // se non è ancora presente, lo crea partendo da tutti i prodotti
        if (ordine == null) {
            List<Prodotto> tutti = new ProdottoDAO().doRetrieveAll();
            List<Long> ids = new ArrayList<>();
            // estrae gli id di tutti i prodotti
            for (Prodotto p : tutti) {
                ids.add(p.getId());
            }
            // mescola casualmente gli id
            Collections.shuffle(ids);
            // salva i risultati nel contesto applicativo
            getServletContext().setAttribute(ORDINE_KEY, ids);
            ordine = ids;
        }

        // associa ad ogni id prodotto la sua posizione nell'ordinamento salvato
        Map<Long, Integer> posizioni = new HashMap<>();
        for (int i = 0; i < ordine.size(); i++) {
            posizioni.put(ordine.get(i), i);
        }

        // ordina i prodotti in base alla posizione del loro id
        prodotti.sort(Comparator.comparingInt(
                p -> posizioni.getOrDefault(p.getId(), Integer.MAX_VALUE)
        )); // quelli che non sono presenti nell'ordinamento finiscono in coda
    }
}
