package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.IndirizzoSpedizione;
import model.Bean.Utente;
import model.DAO.IndirizzoSpedizioneDAO;

import java.io.IOException;
import java.util.List;

/*
 * Mostra la pagina profilo dell'utente con dati personali e indirizzi di spedizione salvati.
 * Gestisce i flash message provenienti dalla servlet di modifica e la scheda da aprire.
 */
@WebServlet(name = "myAccount", urlPatterns = "/myAccount")
public class AccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la sessione senza crearne una nuova
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;

        // controllo che l'utente sia autenticato anche se lo fa già il filtro, per maggiore sicurezza
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // gestione del flash message di conferma della modifica avvenuta
        Boolean conferma = (Boolean) session.getAttribute("modificaEffettuata");
        if (Boolean.TRUE.equals(conferma)) {
            request.setAttribute("modificaEffettuata", true);
            session.removeAttribute("modificaEffettuata");
        }

        // gestione della tab attiva da aprire nella pagina del profilo
        String tabAttiva = (String) session.getAttribute("tabAttiva");
        if (tabAttiva != null) {
            request.setAttribute("tabAttiva", tabAttiva);
            session.removeAttribute("tabAttiva");
        }

        request.setAttribute("utente", utente);
        List<IndirizzoSpedizione> indirizzi = new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId());
        request.setAttribute("indirizzi", indirizzi);
        request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
    }
}
