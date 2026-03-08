package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Utente;
import model.dao.UtenteDAO;

import java.io.IOException;

@WebServlet(name = "login", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email != null) {
            email = email.trim();
        }

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("errore", "Email o password non corretti. Riprova.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        UtenteDAO utenteDAO = new UtenteDAO();
        Utente utente = utenteDAO.doRetrieveByEmailAndPassword(email, password);

        if (utente != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("utenteConnesso", utente);
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.setAttribute("errore", "Email o password non corretti. Riprova.");
        request.setAttribute("emailInserita", email);
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }
}
