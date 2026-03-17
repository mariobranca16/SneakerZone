package controller;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Utente;
import model.DAO.IndirizzoSpedizioneDAO;
import model.DAO.UtenteDAO;

import java.io.IOException;

@WebServlet(name = "aggiornaPassword", urlPatterns = "/myAccount/password")
public class ModificaPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String passwordAttuale = request.getParameter("passwordAttuale");
        String nuovaPassword = request.getParameter("nuovaPassword");
        String confermaPassword = request.getParameter("confermaPassword");

        boolean hasError = false;
        UtenteDAO dao = new UtenteDAO();

        if (passwordAttuale == null || passwordAttuale.isBlank()) {
            request.setAttribute("errorePasswordAttuale", "Inserisci la password attuale.");
            hasError = true;
        } else if (dao.doRetrieveByEmailAndPassword(utente.getEmail(), passwordAttuale) == null) {
            request.setAttribute("errorePasswordAttuale", "La password attuale non e corretta.");
            hasError = true;
        }

        if (nuovaPassword == null || nuovaPassword.isBlank()) {
            request.setAttribute("erroreNuovaPassword", "Inserisci una nuova password.");
            hasError = true;
        } else if (passwordAttuale != null && passwordAttuale.equals(nuovaPassword)) {
            request.setAttribute("erroreNuovaPassword", "La nuova password deve essere diversa da quella attuale.");
            hasError = true;
        } else if (!ValidatoreInput.isPasswordForte(nuovaPassword)) {
            request.setAttribute("erroreNuovaPassword",
                    "Usa 8-64 caratteri con maiuscola, minuscola, numero e simbolo, senza spazi.");
            hasError = true;
        }

        if (nuovaPassword != null && !nuovaPassword.isBlank() && !nuovaPassword.equals(confermaPassword)) {
            request.setAttribute("erroreConfermaPassword", "Le password non coincidono.");
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("tabAttiva", "password");
            request.setAttribute("utente", utente);
            request.setAttribute("indirizzi", new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId()));
            request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
            return;
        }

        dao.doUpdatePassword(utente.getId(), nuovaPassword);

        session.setAttribute("modificaEffettuata", true);
        session.setAttribute("tabAttiva", "password");
        response.sendRedirect(request.getContextPath() + "/myAccount");
    }
}
