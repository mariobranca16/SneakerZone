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

@WebServlet(name = "rimuovi-recensione", urlPatterns = "/rimuovi-recensione")
public class RimuoviRecensioneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("idRecensione");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        long idRecensione;
        try {
            idRecensione = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        RecensioneDAO recensioneDAO = new RecensioneDAO();
        Recensione recensione = recensioneDAO.doRetrieveByKey(idRecensione);

        if (recensione == null) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        if (recensione.getIdUtente() != utente.getId() && !utente.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/prodotto?id=" + recensione.getIdProdotto());
            return;
        }

        recensioneDAO.doDelete(idRecensione);
        response.sendRedirect(request.getContextPath() + "/prodotto?id=" + recensione.getIdProdotto());
    }
}
