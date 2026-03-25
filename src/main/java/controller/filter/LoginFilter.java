package controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Utente;

import java.io.IOException;

/*
 * Filtro che protegge le pagine riservate agli utenti autenticati.
 * Se l'utente non è loggato, lo manda al login e salva la pagina richiesta.
 */
@WebFilter(urlPatterns = {
        "/myAccount",
        "/myAccount/datiPersonali",
        "/myAccount/password",
        "/myAccount/indirizzo/modifica",
        "/myAccount/indirizzo/elimina",
        "/checkout",
        "/ordini",
        "/wishlist",
        "/aggiungi-indirizzo",
        "/add-to-wishlist",
        "/remove-from-wishlist",
        "/modifica-ordine",
        "/aggiungi-recensione",
        "/rimuovi-recensione"
})
public class LoginFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // recupera la sessione solo se esiste e prova a leggere l'utente connesso
        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;

        // se l'utente non è loggato, salva la pagina richiesta e lo manda al login
        if (utente == null) {
            request.getSession().setAttribute("redirectDopoLogin", request.getRequestURI());
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // se il controllo è passato, allora l'utente è autenticato, quindi lascia proseguire la richiesta
        chain.doFilter(request, response);
    }
}
