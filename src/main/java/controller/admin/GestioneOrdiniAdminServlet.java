package controller.admin;

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
            if (!emailUtenti.containsKey(o.getIdUtente())) {
                Utente u = utenteDAO.doRetrieveByKey(o.getIdUtente());
                emailUtenti.put(o.getIdUtente(), u != null ? u.getEmail() : "#" + o.getIdUtente());
            }
        }

        request.setAttribute("ordini", ordini);
        request.setAttribute("emailUtenti", emailUtenti);
        request.setAttribute("stati", StatoOrdine.values());
        request.setAttribute("titoloPagina", "Gestione ordini");

        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_ordini.jsp").forward(request, response);
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

        StatoOrdine nuovoStato = StatoOrdine.fromString(statoParam);
        if (nuovoStato == null) {
            request.getSession().setAttribute("flashErrore", "Stato ordine non valido");
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        OrdineDAO ordineDAO = new OrdineDAO();
        Ordine ordine = ordineDAO.doRetrieveByKey(idOrdine);
        if (ordine == null) {
            request.getSession().setAttribute("flashErrore", "Ordine non trovato");
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        try {
            ordineDAO.doUpdateStato(idOrdine, nuovoStato);
        } catch (RuntimeException e) {
            request.getSession().setAttribute("flashErrore", "Impossibile aggiornare lo stato: stock non coerente con la transizione richiesta.");
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        request.getSession().setAttribute("flashSuccesso", "Stato ordine aggiornato con successo");
        response.sendRedirect(request.getContextPath() + "/admin/ordini");
    }
}
