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

/*
 * Elimina un indirizzo di spedizione, verificando prima che appartenga a un utente loggato.
 */
@WebServlet(name = "eliminaIndirizzo", urlPatterns = "/myAccount/indirizzo/elimina")
public class EliminaIndirizzoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // recupera la sessione esistente e l'utente autenticato
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;
        // se l'utente non è loggato, viene reindirizzato al login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // recupera e valida l'id dell'indirizzo da eliminare
        long idIndirizzo;
        try {
            idIndirizzo = Long.parseLong(request.getParameter("idIndirizzo"));
        } catch (NumberFormatException | NullPointerException e) {
            idIndirizzo = -1;
        }

        // elimina solo se l'id è valido e l'indirizzo appartiene all'utente loggato
        if (idIndirizzo > 0) {
            IndirizzoSpedizioneDAO dao = new IndirizzoSpedizioneDAO();
            IndirizzoSpedizione is = dao.doRetrieveByKey(idIndirizzo);
            if (is != null && is.getIdUtente() == utente.getId()) {
                try {
                    dao.doDelete(idIndirizzo);
                } catch (RuntimeException e) {
                    // l'indirizzo è collegato a un ordine esistente e non può essere eliminato
                    session.setAttribute("flashErrore", "Impossibile eliminare l'indirizzo: è associato a un ordine esistente");
                    session.setAttribute("tabAttiva", "indirizzo");
                    response.sendRedirect(request.getContextPath() + "/myAccount");
                    return;
                }
            }
        }

        // imposta i flash message per mostrare la conferma e riaprire la tab indirizzi
        session.setAttribute("modificaEffettuata", true);
        session.setAttribute("tabAttiva", "indirizzo");
        response.sendRedirect(request.getContextPath() + "/myAccount");
    }
}
