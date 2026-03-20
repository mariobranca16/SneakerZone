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

@WebServlet(name = "carrello", urlPatterns = "/carrello")
public class CarrelloServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        boolean isAjax = "1".equals(request.getParameter("ajax"));
        String azione = request.getParameter("azione");
        String origine = request.getParameter("origine");

        long idProdotto;
        try {
            idProdotto = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        String tagliaParam = request.getParameter("taglia");

        if ("rimuovi".equalsIgnoreCase(azione)) {
            if (tagliaParam != null && !tagliaParam.isBlank()) {
                carrello.rimuoviProdotto(idProdotto, Integer.parseInt(tagliaParam));
            }
            if (isAjax) {
                boolean vuoto = carrello.getProdotti().isEmpty();
                double totale = carrello.getTotale();
                ValidatoreInput.sendJson(response, 200, String.format(Locale.US,
                        "{\"success\":true,\"vuoto\":%b,\"totale\":%.2f}", vuoto, totale));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

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
                int nuovaQuantita;
                try {
                    nuovaQuantita = Integer.parseInt(request.getParameter("quantita"));
                } catch (NumberFormatException e) {
                    nuovaQuantita = 0;
                }
                carrello.rimuoviProdotto(idProdotto, tagliaAggiorna);
                if (nuovaQuantita > 0) {
                    Prodotto prodottoAggiorna = new ProdottoDAO().doRetrieveByKey(idProdotto);
                    if (prodottoAggiorna != null) {
                        int disponibilita = new ProdottoTagliaDAO().doRetrieveDisponibilita(idProdotto, tagliaAggiorna);
                        int qty = Math.min(nuovaQuantita, disponibilita);
                        if (qty > 0) {
                            carrello.aggiungiProdotto(prodottoAggiorna, tagliaAggiorna, qty);
                        }
                    }
                }
                if (isAjax) {
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
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        if (tagliaParam == null || tagliaParam.isBlank()) {
            redirectConErrore(request, response, session, idProdotto, origine, "Seleziona una taglia");
            return;
        }

        int tagliaInt;
        try {
            tagliaInt = Integer.parseInt(tagliaParam);
        } catch (NumberFormatException e) {
            redirectConErrore(request, response, session, idProdotto, origine, "Taglia non valida");
            return;
        }

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

        Prodotto prodotto = new ProdottoDAO().doRetrieveByKey(idProdotto);
        if (prodotto == null) {
            redirectConErrore(request, response, session, idProdotto, origine, "Prodotto non disponibile");
            return;
        }

        ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();
        int disponibilita = ptDAO.doRetrieveDisponibilita(idProdotto, tagliaInt);

        if (quantita > disponibilita) {
            redirectConErrore(request, response, session, idProdotto, origine, "Quantita richiesta superiore alla disponibilita del prodotto");
            return;
        }

        carrello.aggiungiProdotto(prodotto, tagliaInt, quantita);

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

    private void redirectConErrore(HttpServletRequest request, HttpServletResponse response,
                                   HttpSession session, long idProdotto, String origine, String messaggio)
            throws IOException {
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
