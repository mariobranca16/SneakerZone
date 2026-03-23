package controller;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Carrello;
import model.Bean.IndirizzoSpedizione;
import model.Bean.Prodotto;
import model.Bean.Utente;
import model.DAO.IndirizzoSpedizioneDAO;
import model.DAO.ProdottoDAO;
import model.DAO.ProdottoTagliaDAO;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
 * Gestisce le operazioni sul carrello: aggiunta, rimozione e aggiornamento quantità.
 * Se la richiesta arriva in AJAX (ajax=1), la risposta viene restituita in JSON.
 * In caso contrario, si fa un redirect alla pagina opportuna in base al paramemtro "origine"
 */
@WebServlet(name = "carrello", urlPatterns = "/carrello")
public class CarrelloServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupero della sessione
        HttpSession session = request.getSession();
        // recupero del carrello dalla sessione
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        // flag per determinare se l'origine della richiesta è AJAX
        boolean isAjax = "1".equals(request.getParameter("ajax"));
        String azione = request.getParameter("azione");
        String origine = request.getParameter("origine");

        // recupero e parsing dell'id prodotto;
        long idProdotto;
        try {
            idProdotto = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) { // se non è valido fa redirect al catalogo
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        // recupero della taglia
        String tagliaParam = request.getParameter("taglia");

        // caso in cui il prodotto viene rimosso dal carrello
        if ("rimuovi".equalsIgnoreCase(azione)) {
            if (tagliaParam != null && !tagliaParam.isBlank()) {
                // se la taglia è valida, allora rimuove il prodotto associato all'id
                carrello.rimuoviProdotto(idProdotto, Integer.parseInt(tagliaParam));
            }
            // se la richiesta è tramite AJAX
            if (isAjax) {
                // restituisce i dati aggiornati del carrello con JSON
                boolean vuoto = carrello.getProdotti().isEmpty();
                double totale = carrello.getTotale();
                ValidatoreInput.sendJson(response, 200, String.format(Locale.US,
                        "{\"success\":true,\"vuoto\":%b,\"totale\":%.2f}", vuoto, totale));
                return;
            }
            // altrimenti fa redirect alla pagina del carrello
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        // caso in cui l'utente aggiorna la quantità di un prodotto presente nel carrello
        if ("aggiorna".equalsIgnoreCase(azione)) {
            if (tagliaParam != null && !tagliaParam.isBlank()) {
                int tagliaAggiorna;
                try {
                    tagliaAggiorna = Integer.parseInt(tagliaParam);
                } catch (NumberFormatException e) {
                    if (isAjax) {
                        ValidatoreInput.sendJson(response, 400, "{\"success\":false}");
                        return;
                    }
                    response.sendRedirect(request.getContextPath() + "/carrello");
                    return;
                }

                // recupera la nuova quantità richiesta
                int nuovaQuantita;
                try {
                    nuovaQuantita = Integer.parseInt(request.getParameter("quantita"));
                } catch (NumberFormatException e) {
                    nuovaQuantita = 0;
                }

                /* Rimuove sempre prima l'articolo dal carrello. Se la nuova quantità è valida e maggiore di 0,
                   allora il prodotto viene aggiornato. */
                carrello.rimuoviProdotto(idProdotto, tagliaAggiorna);
                if (nuovaQuantita > 0) {
                    Prodotto prodottoAggiorna = new ProdottoDAO().doRetrieveByKey(idProdotto);
                    if (prodottoAggiorna != null) {
                        // Limita la quantità alla disponibilità effettiva in magazzino
                        int disponibilita = new ProdottoTagliaDAO().doRetrieveDisponibilita(idProdotto, tagliaAggiorna);
                        int qty = Math.min(nuovaQuantita, disponibilita);
                        if (qty > 0) {
                            carrello.aggiungiProdotto(prodottoAggiorna, tagliaAggiorna, qty);
                        }
                    }
                }

                // se la richiesta è tramite AJAX, restituisce i nuovi valori da usare per aggiornare il DOM
                if (isAjax) {
                    // Calcola la quantità effettiva risultante e il subtotale
                    int qtyEffettiva = 0;
                    double subtotale = 0;
                    for (Carrello.ItemCarrello item : carrello.getProdotti()) {
                        if (item.getProdotto().getId() == idProdotto && item.getTaglia() == tagliaAggiorna) {
                            qtyEffettiva = item.getQuantita();
                            subtotale = item.getQuantita() * item.getProdotto().getCosto();
                            break;
                        }
                    }
                    boolean rimosso = qtyEffettiva == 0;
                    boolean vuoto = carrello.getProdotti().isEmpty();
                    double totale = carrello.getTotale();
                    ValidatoreInput.sendJson(response, 200, String.format(Locale.US,
                            "{\"success\":true,\"nuovaQuantita\":%d,\"subtotale\":%.2f,\"totale\":%.2f,\"rimosso\":%b,\"vuoto\":%b}",
                            qtyEffettiva, subtotale, totale, rimosso, vuoto));
                    return;
                }
            }
            // se non c'è una taglia valida da aggiornare, ritorna alla pagina del carrello
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        // se è uscito dai due if, l'azione è di aggiunta del prodotto, quindi la taglia è obbligatoria
        if (tagliaParam == null || tagliaParam.isBlank()) {
            redirectConErrore(request, response, session, idProdotto, origine, "Seleziona una taglia");
            return;
        }

        // converte e valida la taglia selezionata
        int tagliaInt;
        try {
            tagliaInt = Integer.parseInt(tagliaParam);
        } catch (NumberFormatException e) {
            redirectConErrore(request, response, session, idProdotto, origine, "Taglia non valida");
            return;
        }

        // recupera e valida la quantità richiesta
        int quantita;
        try {
            quantita = Integer.parseInt(request.getParameter("quantita"));
        } catch (NumberFormatException e) {
            quantita = 0;
        }

        if (quantita <= 0) {
            redirectConErrore(request, response, session, idProdotto, origine, "Quantita non valida");
            return;
        }

        // Verifica che il prodotto esista
        Prodotto prodotto = new ProdottoDAO().doRetrieveByKey(idProdotto);
        if (prodotto == null) {
            redirectConErrore(request, response, session, idProdotto, origine, "Prodotto non disponibile");
            return;
        }
        // e che la quantità richiesta, associata al prodotto, sia disponibile
        ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();
        int disponibilita = ptDAO.doRetrieveDisponibilita(idProdotto, tagliaInt);
        if (quantita > disponibilita) {
            // affida la gestione del redirect a un metodo privato
            redirectConErrore(request, response, session, idProdotto, origine, "Quantita richiesta superiore alla disponibilita del prodotto");
            return;
        }

        // Aggiunge il prodotto al carrello in sessione
        carrello.aggiungiProdotto(prodotto, tagliaInt, quantita);

        // Redirect in base all'origine della richiesta
        if ("prodotto".equalsIgnoreCase(origine)) {
            response.sendRedirect(request.getContextPath() + "/prodotto?id=" + idProdotto + "&successoCarrello=1");
            return;
        }
        if ("wishlist".equalsIgnoreCase(origine)) {
            response.sendRedirect(request.getContextPath() + "/wishlist?successo=1");
        } else {
            session.setAttribute("flash_messaggio", "Prodotto aggiunto al carrello");
            response.sendRedirect(request.getContextPath() + "/catalogo");
        }
    }

    // Mostra la pagina del carrello.
    // Se l'utente è loggato, carica anche gli indirizzi salvati
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utente utente = (Utente) request.getSession().getAttribute("utenteConnesso");
        if (utente != null) {
            IndirizzoSpedizioneDAO indirizzoDAO = new IndirizzoSpedizioneDAO();
            List<IndirizzoSpedizione> indirizzi = indirizzoDAO.doRetrieveByUtente(utente.getId());
            request.setAttribute("indirizziUtente", indirizzi);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/carrello.jsp").forward(request, response);
    }

    // Metodo privato che gestisce il redirect in caso di errore nell'aggiunta al carrello.
    // Distingue tra prodotto, wishlist e catalogo per scegliere la destinazione giusta.
    private void redirectConErrore(HttpServletRequest request, HttpServletResponse response, HttpSession session, long idProdotto, String origine, String messaggio) throws IOException {
        if ("prodotto".equalsIgnoreCase(origine)) {
            response.sendRedirect(request.getContextPath() + "/prodotto?id=" + idProdotto + "&erroreCarrello=1");
            return;
        }
        if ("wishlist".equalsIgnoreCase(origine)) {
            response.sendRedirect(request.getContextPath() + "/wishlist?erroreCarrello=1");
        } else {
            session.setAttribute("flash_erroreCarrello", messaggio);
            response.sendRedirect(request.getContextPath() + "/catalogo");
        }
    }
}
