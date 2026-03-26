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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/*
 * Aggiorna i dati personali dell'utente.
 * Valida tutti i campi inseriti e controlla che l'email non sia già usata da un altro account.
*/
@WebServlet(name = "aggiornaDatiPersonali", urlPatterns = "/myAccount/datiPersonali")
public class ModificaDatiPersonaliServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // recupera la sessione corrente e l'utente autenticato
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;
        // se non c'è un utente loggato, rimanda alla pagina di login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // legge i dati inviati dal form e li normalizza prima dei controlli
        String nome = ValidatoreInput.normalizzaTesto(request.getParameter("nome"));
        String cognome = ValidatoreInput.normalizzaTesto(request.getParameter("cognome"));
        String email = ValidatoreInput.normalizzaTesto(request.getParameter("email"));
        String telefono = ValidatoreInput.normalizzaTelefono(request.getParameter("telefono"));
        String dataNascitaStr = ValidatoreInput.normalizzaTesto(request.getParameter("dataDiNascita"));

        boolean hasError = false;

        // validazione dei campi nome e cognome
        if (!ValidatoreInput.contieneTesto(nome)) {
            request.setAttribute("erroreNome", "Campo obbligatorio.");
            hasError = true;
        } else if (!ValidatoreInput.isNomeValido(nome)) {
            request.setAttribute("erroreNome", "Il nome deve avere 2-50 caratteri e contenere solo lettere.");
            hasError = true;
        }
        if (!ValidatoreInput.contieneTesto(cognome)) {
            request.setAttribute("erroreCognome", "Campo obbligatorio.");
            hasError = true;
        } else if (!ValidatoreInput.isNomeValido(cognome)) {
            request.setAttribute("erroreCognome", "Il cognome deve avere 2-50 caratteri e contenere solo lettere.");
            hasError = true;
        }

        // validazione della mail, deve rispettare il formato e non essere già usata da un altro account
        UtenteDAO dao = new UtenteDAO();
        if (!ValidatoreInput.contieneTesto(email)) {
            request.setAttribute("erroreEmail", "L'email non puo essere vuota.");
            hasError = true;
        } else if (!ValidatoreInput.isEmailValida(email)) {
            request.setAttribute("erroreEmail", "Formato email non valido.");
            hasError = true;
        } else if (dao.doExistsByEmailExcludingId(email, utente.getId())) {
            request.setAttribute("erroreEmail", "Email gia registrata.");
            hasError = true;
        }

        // validazione del telefono
        if (!ValidatoreInput.contieneTesto(telefono)) {
            request.setAttribute("erroreTelefono", "Il telefono non puo essere vuoto.");
            hasError = true;
        } else if (!ValidatoreInput.isTelefonoValido(telefono)) {
            request.setAttribute("erroreTelefono", "Numero di telefono non valido.");
            hasError = true;
        }

        // validazione della data di nascita; controlla anche che l'utente sia maggiorenne
        LocalDate dataNascita = null;
        if (!ValidatoreInput.contieneTesto(dataNascitaStr)) {
            request.setAttribute("erroreDataNascita", "La data di nascita e obbligatoria.");
            hasError = true;
        } else {
            try {
                dataNascita = LocalDate.parse(dataNascitaStr);
                if (ValidatoreInput.isMinorenne(dataNascita)) {
                    request.setAttribute("erroreDataNascita", "Devi avere almeno 18 anni.");
                    hasError = true;
                }
            } catch (DateTimeParseException e) {
                request.setAttribute("erroreDataNascita", "Formato data non valido.");
                hasError = true;
            }
        }

        // se ci sono errori, rimostra il form con i dati già inseriti e mantiene aperta la tab attiva
        if (hasError) {
            request.setAttribute("tabAttiva", "dati-personali");
            request.setAttribute("utente", utente);
            request.setAttribute("formNome", nome);
            request.setAttribute("formCognome", cognome);
            request.setAttribute("formEmail", email);
            request.setAttribute("formTelefono", request.getParameter("telefono"));
            request.setAttribute("formDataNascita", dataNascitaStr);
            request.setAttribute("indirizzi", new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId()));
            request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
            return;
        }

        // se tutti i controlli sono passati, aggiorna l'oggetto utente
        utente.setNome(nome);
        utente.setCognome(cognome);
        utente.setEmail(email);
        utente.setTelefono(telefono);
        utente.setDataDiNascita(dataNascita);
        dao.doUpdate(utente);

        session.setAttribute("utenteConnesso", utente);
        session.setAttribute("modificaEffettuata", true);
        session.setAttribute("tabAttiva", "dati-personali");
        response.sendRedirect(request.getContextPath() + "/myAccount");
    }
}
