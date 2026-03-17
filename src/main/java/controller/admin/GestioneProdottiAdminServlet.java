package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bean.Prodotto;
import model.DAO.ProdottoDAO;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "gestioneProdottiAdmin", urlPatterns = "/admin/prodotti")
public class GestioneProdottiAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Prodotto> prodotti = new ProdottoDAO().doRetrieveAll();
        request.setAttribute("prodotti", prodotti);
        request.setAttribute("titoloPagina", "Gestione prodotti");

        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_prodotti.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String azione = request.getParameter("azione");
        String idParam = request.getParameter("id");

        if (azione == null || azione.isBlank() || idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        ProdottoDAO prodottoDAO = new ProdottoDAO();
        Prodotto prodotto = prodottoDAO.doRetrieveByKey(id);
        if (prodotto == null) {
            request.getSession().setAttribute("flashErrore", "Prodotto non trovato");
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        if ("elimina".equalsIgnoreCase(azione)) {
            prodottoDAO.doDelete(id);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/prodotti");
    }
}
