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

/*
 * Mostra la wishlist dell'utente autenticato.
 * Per ogni prodotto carica anche le taglie disponibili.
 */
@WebServlet(name = "wishlist", urlPatterns = "/wishlist")
public class WishlistServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la sessione corrente e l'utente autenticato
        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;
        // se non c'è un utente loggato, rimanda alla pagina di login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // recupera dal db tutti i prodotti salvati nella wishlist dell'utente
        WishlistDAO wishlistDAO = new WishlistDAO();
        List<Prodotto> prodotti = wishlistDAO.doRetrieveProdottiByUtente(utente.getId());
        request.setAttribute("prodotti", prodotti);

        // per ogni prodotto nella wishlist vengono recuperate anche le taglie disponibili
        // (serve per mostrare il form di aggiunta al carrello direttamente dalla wishlist)
        Map<Long, List<ProdottoTaglia>> disponibilitaPerProdotto = new HashMap<>();
        ProdottoTagliaDAO ptDAO = new ProdottoTagliaDAO();
        for (Prodotto prodotto : prodotti) {
            List<ProdottoTaglia> taglie = ptDAO.doRetrieveDisponibilitaByProdotto(prodotto.getId());
            disponibilitaPerProdotto.put(prodotto.getId(), taglie);
        }
        request.setAttribute("disponibilitaPerProdotto", disponibilitaPerProdotto);

        // aggiorna il contatore dei prodotti presenti nella wishlist
        session.setAttribute("wishlistCount", prodotti.size());

        request.getRequestDispatcher("/WEB-INF/jsp/wishlist.jsp").forward(request, response);
    }
}
