package controller.admin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bean.Ordine;
import model.Bean.StatoOrdine;
import model.Bean.Utente;
import model.DAO.OrdineDAO;
import model.DAO.UtenteDAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "gestioneOrdiniAdmin", urlPatterns = "/admin/ordini")
public class GestioneOrdiniAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Ordine> ordini = new OrdineDAO().doRetrieveAll();

        UtenteDAO utenteDAO = new UtenteDAO();
        Map<Long, String> emailUtenti = new HashMap<>();
        for (Ordine o : ordini) {
            emailUtenti.computeIfAbsent(o.getIdUtente(), uid -> {
                Utente u = utenteDAO.doRetrieveByKey(uid);
                return u != null ? u.getEmail() : "#" + uid;
            });
        }

        request.setAttribute("ordini", ordini);
        request.setAttribute("emailUtenti", emailUtenti);
        request.setAttribute("stati", StatoOrdine.values());
        request.setAttribute("titoloPagina", "Gestione ordini");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_ordini.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String statoParam = request.getParameter("stato");

        if (idParam == null || idParam.isBlank() || statoParam == null || statoParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        long idOrdine;
        try {
            idOrdine = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        StatoOrdine nuovoStato;
        try {
            nuovoStato = StatoOrdine.fromString(statoParam);
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        OrdineDAO ordineDAO = new OrdineDAO();
        Ordine ordine = ordineDAO.doRetrieveByKey(idOrdine);
        if (ordine == null) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        ordineDAO.doUpdateStato(idOrdine, nuovoStato);

        response.sendRedirect(request.getContextPath() + "/admin/ordini");
    }
}
