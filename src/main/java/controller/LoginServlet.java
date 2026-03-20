package controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Utente;
import model.DAO.UtenteDAO;
import model.DAO.WishlistDAO;
import java.io.IOException;
@WebServlet(name = "login", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute("utenteConnesso") != null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            session.removeAttribute("erroreLogin");
        }
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
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
            session.setAttribute("wishlistCount", new WishlistDAO().countByUtente(utente.getId()));
            String redirect = (String) session.getAttribute("redirectDopoLogin");
            session.removeAttribute("redirectDopoLogin");
            if (redirect != null && !redirect.isBlank()) {
                response.sendRedirect(redirect);
            } else {
                session.setAttribute("messaggioHome", "Bentornato, " + utente.getNome() + "!");
                response.sendRedirect(request.getContextPath() + "/home");
            }
            return;
        }
        request.setAttribute("errore", "Email o password non corretti. Riprova.");
        request.setAttribute("emailInserita", email);
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }
}
