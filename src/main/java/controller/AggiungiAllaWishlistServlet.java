package controller;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Utente;
import model.Bean.Wishlist;
import model.DAO.WishlistDAO;

import java.io.IOException;

/*
 * Aggiunge un prodotto alla wishlist. Supporta sia richieste normali che AJAX.
 * Se l'utente non è loggato, restituisce errore 401 con JSON oppure fa redirect al login.
 */
@WebServlet(name = "add-to-wishlist", urlPatterns = "/add-to-wishlist")
public class AggiungiAllaWishlistServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // controlla se la richiesta arriva tramite AJAX
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        // recupera la sessione e l'utente loggato
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utenteConnesso");

        // se l'utente non è autenticato
        if (utente == null) {
            // e la richiesta arriva tramite AJAX
            if (isAjax) {
                ValidatoreInput.sendJson(response, 401, "{\"success\":false,\"redirect\":\"" + request.getContextPath() + "/login\"}");
                return; // allora manda errore 401
            }
            // altrimenti fa redirect al login
            session.setAttribute("erroreLogin", "Effettua il login per salvare prodotti nella wishlist");
            response.sendRedirect(request.getContextPath() + "/login?redirect=catalogo");
            return;
        }

        // recupero e validazione dell'id del prodotto
        String idProdottoParam = request.getParameter("idProdotto");
        if (idProdottoParam == null || idProdottoParam.isBlank()) {
            if (isAjax) {
                ValidatoreInput.sendJson(response, 400, "{\"success\":false,\"errore\":\"ID prodotto mancante\"}");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        // parsing id prodotto
        long idProdotto;
        try {
            idProdotto = Long.parseLong(idProdottoParam);
        } catch (NumberFormatException e) {
            if (isAjax) {
                ValidatoreInput.sendJson(response, 400, "{\"success\":false,\"errore\":\"ID prodotto non valido\"}");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        // Creazione della wishlist per l'utente e aggiunta del prodotto selezionato
        Wishlist wishlist = new Wishlist();
        wishlist.setIdUtente(utente.getId());
        wishlist.setIdProdotto(idProdotto);
        WishlistDAO wishlistDAO = new WishlistDAO();
        wishlistDAO.addToWishlist(wishlist);
        // aggiorna il contatore salvato in sessione
        int nuovoConteggio = wishlistDAO.countByUtente(utente.getId());
        session.setAttribute("wishlistCount", nuovoConteggio);

        // risposta finale
        if (isAjax) { // tramite AJAX
            ValidatoreInput.sendJson(response, 200, "{\"success\":true,\"aggiunto\":true,\"count\":" + nuovoConteggio + "}");
            return;
        }
        // oppure tramite richiesta normale
        response.sendRedirect(request.getContextPath() + "/catalogo?successoWishlist=1");
    }
}
