package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.IndirizzoSpedizione;
import model.bean.Utente;
import model.dao.IndirizzoSpedizioneDAO;

import java.io.IOException;

@WebServlet(name = "eliminaIndirizzo", urlPatterns = "/myAccount/indirizzo/elimina")
public class EliminaIndirizzoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        long idIndirizzo;
        try {
            idIndirizzo = Long.parseLong(request.getParameter("idIndirizzo"));
        } catch (NumberFormatException | NullPointerException e) {
            idIndirizzo = -1;
        }
        if (idIndirizzo > 0) {
            IndirizzoSpedizioneDAO dao = new IndirizzoSpedizioneDAO();
            IndirizzoSpedizione is = dao.doRetrieveByKey(idIndirizzo);
            if (is != null && is.getIdUtente() == utente.getId()) {
                dao.doDelete(idIndirizzo);
            }
        }

        session.setAttribute("modificaEffettuata", true);
        session.setAttribute("tabAttiva", "indirizzo");
        response.sendRedirect(request.getContextPath() + "/myAccount");
    }
}
