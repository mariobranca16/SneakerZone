package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Prodotto;
import model.DAO.ProdottoDAO;

import java.io.IOException;
import java.util.List;

// Mostra la homepage caricando dei prodotti in evidenza.
@WebServlet(name = "home", urlPatterns = "/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        // Recupera un numero fisso di prodotti in evidenza da mostrare nella homepage, in questo caso 4.
        List<Prodotto> prodottiInEvidenza = prodottoDAO.doRetrieveProdottiInEvidenza(4);

        // Gestione flash message di benvenuto (mostrato una sola volta dopo login/registrazione)
        HttpSession session = request.getSession(false);
        if (session != null) {
            String msg = (String) session.getAttribute("messaggioHome");
            if (msg != null) {
                request.setAttribute("messaggioHome", msg);
                session.removeAttribute("messaggioHome");
            }
        }

        request.setAttribute("prodottiInEvidenza", prodottiInEvidenza);
        request.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(request, response);
    }
}
