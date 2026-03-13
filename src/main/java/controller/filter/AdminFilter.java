package controller.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Utente;

import java.io.IOException;

@WebFilter(urlPatterns = "/admin/*")
public class AdminFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession sessione = req.getSession(false);
        Utente utenteConnesso = (sessione != null) ? (Utente) sessione.getAttribute("utenteConnesso") : null;

        if (utenteConnesso == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (!utenteConnesso.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
