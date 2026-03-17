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

@WebServlet(name = "add-to-wishlist", urlPatterns = "/add-to-wishlist")
public class AggiungiAllaWishlistServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utenteConnesso");

        if (utente == null) {
            if (isAjax) {
                ValidatoreInput.sendJson(response, 401, "{\"success\":false,\"redirect\":\"" + request.getContextPath() + "/login\"}");
                return;
            }
            session.setAttribute("erroreLogin", "Effettua il login per salvare prodotti nella wishlist");
            response.sendRedirect(request.getContextPath() + "/login?redirect=catalogo");
            return;
        }

        String idProdottoParam = request.getParameter("idProdotto");
        if (idProdottoParam == null || idProdottoParam.isBlank()) {
            if (isAjax) {
                ValidatoreInput.sendJson(response, 400, "{\"success\":false,\"errore\":\"ID prodotto mancante\"}");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

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

        Wishlist wishlist = new Wishlist();
        wishlist.setIdUtente(utente.getId());
        wishlist.setIdProdotto(idProdotto);

        WishlistDAO wishlistDAO = new WishlistDAO();
        wishlistDAO.addToWishlist(wishlist);

        int nuovoConteggio = wishlistDAO.countByUtente(utente.getId());
        session.setAttribute("wishlistCount", nuovoConteggio);

        if (isAjax) {
            ValidatoreInput.sendJson(response, 200, "{\"success\":true,\"aggiunto\":true,\"count\":" + nuovoConteggio + "}");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/catalogo?successoWishlist=1");
    }

}
