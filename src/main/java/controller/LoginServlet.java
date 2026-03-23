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

/*
 * Gestisce il login dell'utente.
 * Se il login va a buon fine, reindirizza alla pagina richiesta prima del login oppure alla home.
 */
@WebServlet(name = "login", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // se l'utente è già autenticato, non ha senso mostrare di nuovo il form
            if (session.getAttribute("utenteConnesso") != null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            // per pulire eventuali vecchi messaggi di errore rimasti in sessione
            session.removeAttribute("erroreLogin");
        }
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    // Controlla i dati inseriti, prova ad autenticare l'utente e gestisce il redirect finale.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email != null) {
            email = email.trim();
        }

        // Validazione semplice, entrambi i campi sono obbligatori
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("errore", "Email o password non corretti. Riprova.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        // Verifica che le credenziali sono corrette tramite i DAO
        UtenteDAO utenteDAO = new UtenteDAO();
        Utente utente = utenteDAO.doRetrieveByEmailAndPassword(email, password);

        if (utente != null) {
            // se il login riesce, salva l'utente in sessione
            HttpSession session = request.getSession(true);
            session.setAttribute("utenteConnesso", utente);
            //  e aggiorna anche il numero di elementi nella wishlist
            session.setAttribute("wishlistCount", new WishlistDAO().countByUtente(utente.getId()));

            // Gestione del redirect dopo il login

            String redirect = (String) session.getAttribute("redirectDopoLogin");
            session.removeAttribute("redirectDopoLogin");
            // se prima del login, era stata richiesta una pagina protetta, torna lì
            if (redirect != null && !redirect.isBlank()) {
                response.sendRedirect(redirect);
            } else { // altrimenti lo porta alla home
                session.setAttribute("messaggioHome", "Bentornato, " + utente.getNome() + "!");
                response.sendRedirect(request.getContextPath() + "/home");
            }
            return;
        }

        // se le credenziali non sono corrette, rimostra il form mantenendo l'email già inserita
        request.setAttribute("errore", "Email o password non corretti. Riprova.");
        request.setAttribute("emailInserita", email);
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }
}
