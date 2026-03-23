package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Recensione;
import model.Bean.Utente;
import model.DAO.RecensioneDAO;

import java.io.IOException;

/*
 * Rimuove una recensione rilasciata su un prodotto acquistato.
 * L'operazione è consentita solo all'autore o a un admin.
 */
@WebServlet(name = "rimuovi-recensione", urlPatterns = "/rimuovi-recensione")
public class RimuoviRecensioneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // legge l'id della recensione da cancellare
        String idParam = request.getParameter("idRecensione");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        // parsing dell'id
        long idRecensione;
        try {
            idRecensione = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        // recupera la sessione corrente e l'utente autenticato
        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;
        // se non c'è un utente loggato, rimanda alla pagina di login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // recupera dal db la recensione associata all'id letto
        RecensioneDAO recensioneDAO = new RecensioneDAO();
        Recensione recensione = recensioneDAO.doRetrieveByKey(idRecensione);
        if (recensione == null) { // se non esiste, rimanda alla pagina del catalogo
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        // permette l'eliminazione solo all'autore della recensione o a un admin
        if (recensione.getIdUtente() != utente.getId() && !utente.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/prodotto?id=" + recensione.getIdProdotto());
            return;
        }

        // elimina la recensione e ritorna alla pagina del prodotto
        recensioneDAO.doDelete(idRecensione);
        response.sendRedirect(request.getContextPath() + "/prodotto?id=" + recensione.getIdProdotto());
    }
}
