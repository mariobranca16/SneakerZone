package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Prodotto;
import model.bean.Utente;
import model.dao.ProdottoDAO;
import model.dao.RecensioneDAO;

import java.io.IOException;

@WebServlet(name = "prodotto", urlPatterns = "/prodotto")
public class ProdottoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        long idProdotto;
        try {
            idProdotto = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        Prodotto prodotto = new ProdottoDAO().doRetrieveByKey(idProdotto);
        if (prodotto == null) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        request.setAttribute("prodotto", prodotto);
        if ("1".equals(request.getParameter("erroreCarrello"))) {
            request.setAttribute("erroreCarrello", "Quantità richiesta superiore alla disponibilità del prodotto.");
        }
        if ("1".equals(request.getParameter("successoCarrello"))) {
            request.setAttribute("messaggio", "Prodotto aggiunto al carrello con successo.");
        }

        RecensioneDAO recensioneDAO = new RecensioneDAO();
        request.setAttribute("recensioni", recensioneDAO.doRetrieveByProdotto(idProdotto));

        HttpSession session = request.getSession(false);
        if (session != null) {
            Utente utente = (Utente) session.getAttribute("utenteConnesso");
            if (utente != null && !utente.isAdmin()) {
                if (recensioneDAO.haAcquistato(utente.getId(), idProdotto)) {
                    if (recensioneDAO.haGiaRecensito(utente.getId(), idProdotto)) {
                        request.setAttribute("haGiaRecensito", true);
                    } else {
                        request.setAttribute("puoRecensire", true);
                    }
                }
            }
        }

        request.getRequestDispatcher("/WEB-INF/jsp/prodotto.jsp").forward(request, response);
    }
}
