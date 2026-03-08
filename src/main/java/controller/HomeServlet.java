package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bean.Prodotto;
import model.DAO.ProdottoDAO;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "home", urlPatterns = "/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        List<Prodotto> prodottiInEvidenza = prodottoDAO.doRetrieveProdottiInEvidenza(4);

        request.setAttribute("prodottiInEvidenza", prodottiInEvidenza);
        request.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(request, response);
    }
}
