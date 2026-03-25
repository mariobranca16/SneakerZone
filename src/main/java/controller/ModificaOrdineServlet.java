package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Ordine;
import model.Bean.Utente;
import model.DAO.OrdineDAO;

import java.io.IOException;

/*
 * Gestisce la modifica di un ordine da parte dell'utente.
 * Al momento si occupa solo dell'annullamento.
 */
@WebServlet(name = "modifica-ordine", urlPatterns = "/modifica-ordine")
public class ModificaOrdineServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la sessione corrente e l'utente autenticato
        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;
        // se non c'è un utente loggato, rimanda alla pagina di login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // controlla che l'azione richiesta sia effettivamente l'annullamento
        String action = request.getParameter("action");
        if (action == null || !action.equals("annulla")) {
            response.sendRedirect(request.getContextPath() + "/ordini");
            return;
        }

        // parsing dell'id dell'ordine da modificare
        long idOrdine;
        try {
            idOrdine = Long.parseLong(request.getParameter("idOrdine"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/ordini");
            return;
        }

        // recupera l'ordine dal db
        OrdineDAO ordineDAO = new OrdineDAO();
        Ordine ordine = ordineDAO.doRetrieveByKey(idOrdine);
        if (ordine == null) {
            response.sendRedirect(request.getContextPath() + "/ordini");
            return;
        }

        // controlla che l'ordine appartenga davvero all'utente loggato
        long idUtenteLoggato = utente.getId();
        if (ordine.getIdUtente() != idUtenteLoggato) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // controlla che lo stato attuale dell'ordine permetta l'annullamento (se non è già stato spedito o consegnato)
        if (ordine.getStato() == null || !ordine.getStato().isAnnullabile()) {
            response.sendRedirect(request.getContextPath() + "/ordini");
            return;
        }

        // annulla l'ordine e ripristina lo stock delle taglie
        ordineDAO.doAnnulla(ordine);
        response.sendRedirect(request.getContextPath() + "/ordini");
    }

    // reindirizza l'utente alla pagina degli ordini.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/ordini");
    }
}
