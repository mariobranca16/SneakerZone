package controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Carrello;

import java.io.IOException;

@WebFilter("/*")
public class SessionFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("carrello") == null) {
            session.setAttribute("carrello", new Carrello());
        }

        chain.doFilter(request, response);
    }
}
