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

import java.io.IOException;

@WebServlet(name = "aggiungi-indirizzo", urlPatterns = "/aggiungi-indirizzo")
public class AggiungiIndirizzoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String from = request.getParameter("from");
        if (from != null) request.setAttribute("from", from);
        request.getRequestDispatcher("/WEB-INF/jsp/aggiungi_indirizzo.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String destinatario = request.getParameter("destinatario");
        String via = request.getParameter("via");
        String cap = request.getParameter("cap");
        String citta = request.getParameter("citta");
        String provincia = request.getParameter("provincia");
        String paese = request.getParameter("paese");
        String from = request.getParameter("from");

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
            request.setAttribute("erroreCitta", "La città deve avere almeno 2 caratteri e contenere solo lettere.");
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
            request.setAttribute("formDestinatario", destinatario);
            request.setAttribute("formVia", via);
            request.setAttribute("formCap", cap);
            request.setAttribute("formCitta", citta);
            request.setAttribute("formProvincia", provincia);
            request.setAttribute("formPaese", paese);
            if (from != null) request.setAttribute("from", from);
            request.getRequestDispatcher("/WEB-INF/jsp/aggiungi_indirizzo.jsp").forward(request, response);
            return;
        }

        IndirizzoSpedizione indirizzo = new IndirizzoSpedizione();
        indirizzo.setIdUtente(utente.getId());
        indirizzo.setDestinatario(destinatario);
        indirizzo.setVia(via);
        indirizzo.setCitta(citta);
        indirizzo.setProvincia(provincia);
        indirizzo.setCap(cap);
        indirizzo.setPaese(paese);
        new IndirizzoSpedizioneDAO().doSave(indirizzo);
        if ("profile".equals(from)) {
            session.setAttribute("modificaEffettuata", true);
            session.setAttribute("tabAttiva", "indirizzo");
            response.sendRedirect(request.getContextPath() + "/myAccount");
        } else {
            response.sendRedirect(request.getContextPath() + "/checkout");
        }
    }
}
