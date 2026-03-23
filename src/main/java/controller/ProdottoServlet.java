package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Prodotto;
import model.Bean.Utente;
import model.DAO.ProdottoDAO;
import model.DAO.RecensioneDAO;

import java.io.IOException;

/*
 * Mostra la pagina di dettaglio di un prodotto.
 * Se l'utente è loggato, controlla anche se può lasciare una recensione
 */
@WebServlet(name = "prodotto", urlPatterns = "/prodotto")
public class ProdottoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // legge l'id del prodotto dalla richiesta
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) { // se è nullo, ritorna al catalogo
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        // parsing dell'id prodotto
        long idProdotto;
        try {
            idProdotto = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        // recupera il prodotto associato all'id dal db e controlla che esista
        Prodotto prodotto = new ProdottoDAO().doRetrieveByKey(idProdotto);
        if (prodotto == null) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }
        request.setAttribute("prodotto", prodotto);

        // eventuali messaggi di feedback dopo l'aggiunta al carrello
        if ("1".equals(request.getParameter("erroreCarrello"))) {
            request.setAttribute("erroreCarrello", "Quantità richiesta superiore alla disponibilità del prodotto");
        }
        if ("1".equals(request.getParameter("successoCarrello"))) {
            request.setAttribute("messaggio", "Prodotto aggiunto al carrello");
        }

        // Carica le recensioni del prodotto
        RecensioneDAO recensioneDAO = new RecensioneDAO();
        request.setAttribute("recensioni", recensioneDAO.doRetrieveByProdotto(idProdotto));

        // se l'utente è autenticato e non è admin, controlla se può recensire il prodotto
        HttpSession session = request.getSession(false);
        if (session != null) {
            Utente utente = (Utente) session.getAttribute("utenteConnesso");
            if (utente != null && !utente.isAdmin()) {
                // solo chi ha acquistato il prodotto può lasciare una recensione
                if (recensioneDAO.haAcquistato(utente.getId(), idProdotto)) {
                    if (recensioneDAO.haGiaRecensito(utente.getId(), idProdotto)) {
                        // se ha già recensito, non può farlo di nuovo
                        request.setAttribute("haGiaRecensito", true);
                    } else { // altrimenti abilita il form per l'inserimento
                        request.setAttribute("puoRecensire", true);
                    }
                }
            }
        }

        request.getRequestDispatcher("/WEB-INF/jsp/prodotto.jsp").forward(request, response);
    }
}
