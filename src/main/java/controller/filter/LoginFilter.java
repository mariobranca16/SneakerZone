package controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Utente;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/myAccount",
        "/myAccount/datiPersonali",
        "/myAccount/password",
        "/myAccount/indirizzo/modifica",
        "/myAccount/indirizzo/elimina",
        "/myAccount/pagamento",
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
        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}

