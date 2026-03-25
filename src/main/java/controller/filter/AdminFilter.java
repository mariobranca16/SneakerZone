package controller.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
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
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        // recupera la sessione solo se esiste e prova a leggere l'utente connesso
        HttpSession sessione = req.getSession(false);
        Utente utenteConnesso = (sessione != null) ? (Utente) sessione.getAttribute("utenteConnesso") : null;

        // se non c'è nessun utente in sessione, rimanda alla pagina di login
        if (utenteConnesso == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // se l'utente è loggato ma non è admin, blocca l'accesso
        if (!utenteConnesso.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // se i controlli sono passati, allora lascia proseguire la richiesta
        chain.doFilter(request, response);
    }
}
