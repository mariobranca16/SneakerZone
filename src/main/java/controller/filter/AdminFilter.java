package controller.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Utente;

import java.io.IOException;

/*
 * Filtro che protegge tutte le pagine dell'area admin.
 * Permette l'accesso solo agli utenti loggati con ruolo di admin.
 */
@WebFilter(urlPatterns = "/admin/*")
public class AdminFilter extends HttpFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // recupera la sessione solo se esiste e prova a leggere l'utente connesso
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;

        // se non c'è nessun utente in sessione, rimanda alla pagina di login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // se l'utente è loggato ma non è admin, blocca l'accesso
        if (!utente.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // se i controlli sono passati, allora lascia proseguire la richiesta
        chain.doFilter(request, response);
    }
}
