package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Prodotto;
import model.Bean.ProdottoTaglia;
import model.Bean.Utente;
import model.DAO.ProdottoTagliaDAO;
import model.DAO.WishlistDAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "wishlist", urlPatterns = "/wishlist")
public class WishlistServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        WishlistDAO wishlistDAO = new WishlistDAO();
        List<Prodotto> prodotti = wishlistDAO.doRetrieveProdottiByUtente(utente.getId());
        request.setAttribute("prodotti", prodotti);

        Map<Long, List<ProdottoTaglia>> disponibilitaPerProdotto = new HashMap<>();
        ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();

        for (Prodotto prodotto : prodotti) {
            List<ProdottoTaglia> taglie = ptDAO.doRetrieveDisponibilitaByProdotto(prodotto.getId());
            disponibilitaPerProdotto.put(prodotto.getId(), taglie);
        }

        request.setAttribute("disponibilitaPerProdotto", disponibilitaPerProdotto);
        session.setAttribute("wishlistCount", prodotti.size());

        request.getRequestDispatcher("/WEB-INF/jsp/wishlist.jsp").forward(request, response);
    }
}
