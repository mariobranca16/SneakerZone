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

@WebServlet(name = "remove-from-wishlist", urlPatterns = "/remove-from-wishlist")
public class RimuoviDallaWishlistServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utenteConnesso");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=wishlist");
            return;
        }

        String idProdottoParam = request.getParameter("idProdotto");
        if (idProdottoParam == null || idProdottoParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/wishlist");
            return;
        }
        long idProdotto;
        try {
            idProdotto = Long.parseLong(idProdottoParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/wishlist");
            return;
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setIdUtente(utente.getId());
        wishlist.setIdProdotto(idProdotto);

        WishlistDAO wishlistDAO = new WishlistDAO();
        wishlistDAO.removeFromWishlist(wishlist);

        int nuovoConteggio = wishlistDAO.countByUtente(utente.getId());
        session.setAttribute("wishlistCount", nuovoConteggio);

        response.sendRedirect(request.getContextPath() + "/wishlist");
    }
}
