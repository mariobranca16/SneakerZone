package controller;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.IndirizzoSpedizione;
import model.Bean.Utente;
import model.DAO.IndirizzoSpedizioneDAO;
import model.DAO.MetodoPagamentoDAO;

import java.io.IOException;

@WebServlet(name = "modificaIndirizzo", urlPatterns = "/myAccount/indirizzo/modifica")
public class ModificaIndirizzoServlet extends HttpServlet {

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
        String destinatario = request.getParameter("destinatario");
        String via = request.getParameter("via");
        String cap = request.getParameter("cap");
        String citta = request.getParameter("citta");
        String provincia = request.getParameter("provincia");
        String paese = request.getParameter("paese");

        boolean hasError = false;
        if (!ValidatoreInput.isDestinatarioValido(destinatario)) {
            request.setAttribute("erroreDestinatario", "Inserisci nome e cognome del destinatario.");
            hasError = true;
        }
        if (!ValidatoreInput.isViaValida(via)) {
            request.setAttribute("erroreVia", "Inserisci un indirizzo completo di numero civico (es. Via Roma 1).");
            hasError = true;
        }
        if (!ValidatoreInput.isCapValido(cap)) {
            request.setAttribute("erroreCap", "Il CAP deve essere di esattamente 5 cifre.");
            hasError = true;
        }
        if (!ValidatoreInput.isLocalitaValida(citta)) {
            request.setAttribute("erroreCitta", "La citta deve avere almeno 2 caratteri e contenere solo lettere.");
            hasError = true;
        }
        if (!ValidatoreInput.isProvinciaValida(provincia)) {
            request.setAttribute("erroreProvincia", "La provincia deve avere 2-5 lettere (es. RM).");
            hasError = true;
        }
        if (!ValidatoreInput.isLocalitaValida(paese)) {
            request.setAttribute("errorePaese", "Il paese deve avere almeno 2 caratteri e contenere solo lettere.");
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("tabAttiva", "indirizzo");
            request.setAttribute("apriEditIndirizzo", true);
            request.setAttribute("utente", utente);
            request.setAttribute("indirizzi", new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId()));
            request.setAttribute("metodoPagamento", new MetodoPagamentoDAO().doRetrieveByUtente(utente.getId()));
            request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
            return;
        }

        IndirizzoSpedizioneDAO dao = new IndirizzoSpedizioneDAO();
        IndirizzoSpedizione is = dao.doRetrieveByKey(idIndirizzo);
        if (is != null && is.getIdUtente() == utente.getId()) {
            is.setDestinatario(destinatario);
            is.setVia(via);
            is.setCap(cap);
            is.setCitta(citta);
            is.setProvincia(provincia);
            is.setPaese(paese);
            dao.doUpdate(is);
        }

        session.setAttribute("modificaEffettuata", true);
        session.setAttribute("tabAttiva", "indirizzo");
        response.sendRedirect(request.getContextPath() + "/myAccount");
    }
}
