package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bean.Recensione;
import model.DAO.RecensioneDAO;

import java.io.IOException;

@WebServlet(name = "eliminaRecensioneAdmin", urlPatterns = "/admin/rimuovi-recensione")
public class EliminaRecensioneAdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("idRecensione");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        long idRecensione;
        try {
            idRecensione = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        RecensioneDAO recensioneDAO = new RecensioneDAO();
        Recensione recensione = recensioneDAO.doRetrieveByKey(idRecensione);
        if (recensione == null) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        recensioneDAO.doDelete(idRecensione);
        response.sendRedirect(request.getContextPath() + "/admin/prodotto?id=" + recensione.getIdProdotto());
    }
}
