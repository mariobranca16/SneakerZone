package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Ordine;
import model.bean.StatoOrdine;
import model.bean.Utente;
import model.dao.OrdineDAO;

import java.io.IOException;

@WebServlet(name = "modifica-ordine", urlPatterns = "/modifica-ordine")
public class ModificaOrdineServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || !action.equals("annulla")) {
            response.sendRedirect(request.getContextPath() + "/ordini");
            return;
        }

        long idOrdine;
        try {
            idOrdine = Long.parseLong(request.getParameter("idOrdine"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/ordini");
            return;
        }

        OrdineDAO ordineDAO = new OrdineDAO();
        Ordine ordine = ordineDAO.doRetrieveByKey(idOrdine);

        if (ordine == null) {
            response.sendRedirect(request.getContextPath() + "/ordini");
            return;
        }

        long idUtenteLoggato = utente.getId();
        if (ordine.getIdUtente() != idUtenteLoggato) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (!isAnnullabile(ordine.getStato())) {
            response.sendRedirect(request.getContextPath() + "/ordini");
            return;
        }

        ordineDAO.doUpdateStato(idOrdine, StatoOrdine.ANNULLATO);
        response.sendRedirect(request.getContextPath() + "/ordini");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/ordini");
    }

    private boolean isAnnullabile(StatoOrdine stato) {
        if (stato == null) {
            return false;
        }
        return stato != StatoOrdine.ANNULLATO
                && stato != StatoOrdine.SPEDITO
                && stato != StatoOrdine.CONSEGNATO;
    }
}
