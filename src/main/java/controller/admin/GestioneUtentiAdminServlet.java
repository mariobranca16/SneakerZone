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

/*
 * Gestisce la lista degli utenti nel pannello admin.
 * L'admin può cancellare l'account di ognuno, ma non il proprio.
 */
@WebServlet(name = "gestioneUtentiAdmin", urlPatterns = "/admin/utenti")
public class GestioneUtentiAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Utente> utenti = new UtenteDAO().doRetrieveAll(); // recupero di tutti gli utenti dal db
        request.setAttribute("utenti", utenti);
        request.setAttribute("titoloPagina", "Gestione utenti");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_utenti.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // lettura dell'azione richiesta e id dell'utente
        String azione = request.getParameter("azione");
        String idParam = request.getParameter("id");

        // se uno dei parametri necessari manca, ritorna alla pagina di gestione utenti
        if (azione == null || azione.isBlank() || idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        // parsing dell'id dell'utente
        long idUtente;
        try {
            idUtente = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            // se l'id non è valido, ritorna alla pagina di gestione degl utenti
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        // al momento l'unica azione consentita è l'eliminazione
        boolean azioneValida = "elimina".equalsIgnoreCase(azione);
        if (!azioneValida) {
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        // impedisce all'admin di eliminare il proprio account
        HttpSession sessione = request.getSession(false);
        Utente utenteConnesso = (sessione != null) ? (Utente) sessione.getAttribute("utenteConnesso") : null;
        if (utenteConnesso != null && utenteConnesso.getId() == idUtente) {
            request.getSession().setAttribute("flashErrore", "Non puoi eseguire questa operazione sul tuo account");
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        // recupero dell'utente da eliminare dal db
        UtenteDAO utenteDAO = new UtenteDAO();
        Utente target = utenteDAO.doRetrieveByKey(idUtente);
        // se non esiste, segnala l'errore
        if (target == null) {
            request.getSession().setAttribute("flashErrore", "Utente non trovato");
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        // se l'azione richiesta è effettivamente la cancellazione, procede con la rimozione dal db
        if ("elimina".equalsIgnoreCase(azione)) {
            utenteDAO.doDelete(idUtente);
            request.getSession().setAttribute("flashSuccesso", "Utente eliminato con successo");
        }

        response.sendRedirect(request.getContextPath() + "/admin/utenti");
    }
}
