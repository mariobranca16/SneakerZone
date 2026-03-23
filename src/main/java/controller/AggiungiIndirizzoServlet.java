package controller;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Carrello;
import model.Bean.IndirizzoSpedizione;
import model.Bean.Utente;
import model.DAO.IndirizzoSpedizioneDAO;

import java.io.IOException;
import java.util.List;

/*
 * Aggiunge un nuovo indirizzo di spedizione.
 * Può essere chiamata sia dalla pagina del profilo sia dal checkout.
 * Il redirect finale cambia il base al parametro from che indica l'origine della richiesta.
*/
@WebServlet(name = "aggiungi-indirizzo", urlPatterns = "/aggiungi-indirizzo")
public class AggiungiIndirizzoServlet extends HttpServlet {

    // redirect alla pagina del profilo
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/myAccount");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // recupera la sessione e l'utente autenticato
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;
        // se l'utente non è loggato, viene rimandato al login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // recupero dei parametri inviati dal form
        String destinatario = request.getParameter("destinatario");
        String via = request.getParameter("via");
        String cap = request.getParameter("cap");
        String citta = request.getParameter("citta");
        String provincia = request.getParameter("provincia");
        String paese = request.getParameter("paese");
        String from = request.getParameter("from");

        boolean hasError = false;

        // validazione di tutti i campi dell'indirizzo di spedizione
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

        // In caso di errori, ricarica la pagina della richiesta con i campi già compilati
        if (hasError) {
            request.setAttribute("apriFormIndirizzo", "nuovo");
            List<IndirizzoSpedizione> indirizzi = new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId());
            if ("profile".equals(from)) {
                // Ritorna alla pagina account con la scheda indirizzi aperta
                request.setAttribute("tabAttiva", "indirizzo");
                request.setAttribute("utente", utente);
                request.setAttribute("indirizzi", indirizzi);
                request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
            } else {
                // Ritorna al checkout ricaricando il carrello e gli indirizzi
                Carrello carrello = (Carrello) session.getAttribute("carrello");
                if (!indirizzi.isEmpty()) {
                    request.setAttribute("indirizzoPrecompilato", indirizzi.get(0));
                }
                request.setAttribute("indirizzi", indirizzi);
                request.setAttribute("carrello", carrello);
                request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
            }
            return;
        }

        // i controlli sono passati, quindi salva il nuovo indirizzo
        IndirizzoSpedizione indirizzo = new IndirizzoSpedizione();
        indirizzo.setIdUtente(utente.getId());
        indirizzo.setDestinatario(destinatario);
        indirizzo.setVia(via);
        indirizzo.setCitta(citta);
        indirizzo.setProvincia(provincia);
        indirizzo.setCap(cap);
        indirizzo.setPaese(paese);
        new IndirizzoSpedizioneDAO().doSave(indirizzo);

        // Redirect in base alla pagina di provenienza grazie al parametro "from".
        if ("profile".equals(from)) {
            session.setAttribute("modificaEffettuata", true);
            session.setAttribute("tabAttiva", "indirizzo");
            response.sendRedirect(request.getContextPath() + "/myAccount");
        } else {
            response.sendRedirect(request.getContextPath() + "/checkout");
        }
    }
}
