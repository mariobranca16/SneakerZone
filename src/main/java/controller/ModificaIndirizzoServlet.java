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

/*
 * Aggiorna un indirizzo di spedizione esistente, verificando che appartenga all'utente loggato.
 */
@WebServlet(name = "modificaIndirizzo", urlPatterns = "/myAccount/indirizzo/modifica")
public class ModificaIndirizzoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la sessione corrente e l'utente autenticato
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;
        // se non c'è un utente loggato, rimanda alla pagina di login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // parsing dell'id indirizzo da modificare; se manca o non è valido, usa un valore non valido
        long idIndirizzo;
        try {
            idIndirizzo = Long.parseLong(request.getParameter("idIndirizzo"));
        } catch (NumberFormatException | NullPointerException e) {
            idIndirizzo = -1;
        }

        // legge i dati inviati dal form
        String destinatario = request.getParameter("destinatario");
        String via = request.getParameter("via");
        String cap = request.getParameter("cap");
        String citta = request.getParameter("citta");
        String provincia = request.getParameter("provincia");
        String paese = request.getParameter("paese");

        boolean hasError = false;

        // validazione di tutti i campi dell'indirizzo
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

        // se ci sono errori, rimostra la pagina account, lasciando aperta la tab degli indirizzi
        if (hasError) {
            request.setAttribute("tabAttiva", "indirizzo");
            request.setAttribute("apriEditIndirizzo", true);
            request.setAttribute("utente", utente);
            request.setAttribute("indirizzi", new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId()));
            request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
            return;
        }

        // se i controlli sono passati, verifica che l'indirizzo appartenga davvero
        // all'utente loggato e poi salva le modifiche
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

        // redirect in base alla pagina di origine (checkout o profilo) grazie al parametro "from"
        String from = request.getParameter("from");
        if ("checkout".equals(from)) {
            response.sendRedirect(request.getContextPath() + "/checkout");
        } else {
            session.setAttribute("modificaEffettuata", true);
            session.setAttribute("tabAttiva", "indirizzo");
            response.sendRedirect(request.getContextPath() + "/myAccount");
        }
    }
}
