package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Utente;
import model.DAO.UtenteDAO;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "gestioneUtentiAdmin", urlPatterns = "/admin/utenti")
public class GestioneUtentiAdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Utente> utenti = new UtenteDAO().doRetrieveAll();
        request.setAttribute("utenti", utenti);
        request.setAttribute("titoloPagina", "Gestione utenti");

        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_utenti.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String azione = request.getParameter("azione");
        String idParam = request.getParameter("id");

        if (azione == null || azione.isBlank() || idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        long idUtente;
        try {
            idUtente = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        boolean azioneValida = "elimina".equalsIgnoreCase(azione);

        if (!azioneValida) {
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        HttpSession sessione = request.getSession(false);
        Utente utenteConnesso = (sessione != null) ? (Utente) sessione.getAttribute("utenteConnesso") : null;

        if (utenteConnesso != null && utenteConnesso.getId() == idUtente) {
            request.getSession().setAttribute("flashErrore", "Non puoi eseguire questa operazione sul tuo account");
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        UtenteDAO utenteDAO = new UtenteDAO();
        Utente target = utenteDAO.doRetrieveByKey(idUtente);
        if (target == null) {
            request.getSession().setAttribute("flashErrore", "Utente non trovato");
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        if ("elimina".equalsIgnoreCase(azione)) {
            utenteDAO.doDelete(idUtente);
            request.getSession().setAttribute("flashSuccesso", "Utente eliminato con successo");
        }

        response.sendRedirect(request.getContextPath() + "/admin/utenti");
    }
}
