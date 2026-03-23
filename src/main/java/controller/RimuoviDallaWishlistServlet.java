package controller;

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
 * Rimuove un prodotto dalla wishlist dell'utente e aggiorna il contatore salvato in sessione.
 */
@WebServlet(name = "remove-from-wishlist", urlPatterns = "/remove-from-wishlist")
public class RimuoviDallaWishlistServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la sessione corrente e l'utente autenticato
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utenteConnesso");
        // se non c'è un utente loggato, rimanda alla pagina di login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=wishlist");
            return;
        }

        // legge l'id del prodotto da rimuovere
        String idProdottoParam = request.getParameter("idProdotto");
        if (idProdottoParam == null || idProdottoParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/wishlist");
            return;
        }

        // parsing dell'id
        long idProdotto;
        try {
            idProdotto = Long.parseLong(idProdottoParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/wishlist");
            return;
        }

        // rimuove il prodotto dalla wishlist dell'utente
        Wishlist wishlist = new Wishlist();
        wishlist.setIdUtente(utente.getId());
        wishlist.setIdProdotto(idProdotto);
        WishlistDAO wishlistDAO = new WishlistDAO();
        wishlistDAO.removeFromWishlist(wishlist);
        // aggiorna il contatore dei prodotti presenti
        int nuovoConteggio = wishlistDAO.countByUtente(utente.getId());
        session.setAttribute("wishlistCount", nuovoConteggio);

        response.sendRedirect(request.getContextPath() + "/wishlist");
    }
}
