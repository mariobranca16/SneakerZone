package controller;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Utente;
import model.DAO.UtenteDAO;

import java.io.IOException;
import java.time.LocalDate;

/*
 * Gestisce la registrazione di un nuovo utente.
 * Se va a buon fine, effettua anche il login automatico.
 */
@WebServlet(name = "registrazione", value = "/registrazione")
public class RegistrazioneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // lettura dei dati inviati dal form
        String nome = ValidatoreInput.normalizzaTesto(request.getParameter("nome"));
        String cognome = ValidatoreInput.normalizzaTesto(request.getParameter("cognome"));
        String email = ValidatoreInput.normalizzaTesto(request.getParameter("email"));
        String password = request.getParameter("password"); // la password non va normalizzata (spazi significativi)
        String telefono = ValidatoreInput.normalizzaTelefono(request.getParameter("telefono"));
        String dataNascitaStr = ValidatoreInput.normalizzaTesto(request.getParameter("dataNascita"));

        boolean hasError = false;

        // validazione dei campi nome e cognome
        if (!ValidatoreInput.isNomeValido(nome)) {
            request.setAttribute("erroreNome", !ValidatoreInput.contieneTesto(nome)
                    ? "Campo obbligatorio."
                    : "Il nome deve avere 2-50 caratteri e contenere solo lettere.");
            hasError = true;
        }
        if (!ValidatoreInput.isNomeValido(cognome)) {
            request.setAttribute("erroreCognome", !ValidatoreInput.contieneTesto(cognome)
                    ? "Campo obbligatorio."
                    : "Il cognome deve avere 2-50 caratteri e contenere solo lettere.");
            hasError = true;
        }

        // validazione dell'email
        UtenteDAO utenteDAO = new UtenteDAO();
        if (!ValidatoreInput.isEmailValida(email)) { // controlla il formato
            request.setAttribute("erroreEmail", !ValidatoreInput.contieneTesto(email)
                    ? "Campo obbligatorio."
                    : "Formato email non valido.");
            hasError = true;
        } else if (utenteDAO.doExistsByEmail(email)) { // e che non sia già registrata
            request.setAttribute("erroreEmail", "Email già registrata.");
            hasError = true;
        }

        // validazione della password
        if (password == null || password.isBlank()) { // controlla che sia presente
            request.setAttribute("errorePassword", "Campo obbligatorio.");
            hasError = true;
        } else if (!ValidatoreInput.isPasswordForte(password)) { // e che rispetti il formato previsto
            request.setAttribute("errorePassword",
                    "Usa 8-64 caratteri con maiuscola, minuscola, numero e simbolo, senza spazi.");
            hasError = true;
        }

        // validazione numero di telefono
        if (!ValidatoreInput.isTelefonoValido(telefono)) {
            request.setAttribute("erroreTelefono", !ValidatoreInput.contieneTesto(telefono)
                    ? "Campo obbligatorio."
                    : "Numero di telefono non valido.");
            hasError = true;
        }

        // validazione data di nascita
        LocalDate dataNascita = null;
        if (!ValidatoreInput.contieneTesto(dataNascitaStr)) { // controlla che sia presente
            request.setAttribute("erroreDataNascita", "Campo obbligatorio.");
            hasError = true;
        } else {
            try {
                dataNascita = LocalDate.parse(dataNascitaStr);
                if (ValidatoreInput.isMinorenne(dataNascita)) { // e che l'utente sia maggiorenne
                    request.setAttribute("erroreDataNascita", "Devi avere almeno 18 anni per registrarti.");
                    hasError = true;
                }
            } catch (Exception e) {
                request.setAttribute("erroreDataNascita", "Data di nascita non valida.");
                hasError = true;
            }
        }

        // se ci sono errori, rimostra il form ripopolato con i campi inseriti
        if (hasError) {
            request.setAttribute("formNome", nome);
            request.setAttribute("formCognome", cognome);
            request.setAttribute("formEmail", email);
            request.setAttribute("formTelefono", request.getParameter("telefono"));
            request.setAttribute("formDataNascita", dataNascitaStr);
            request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
            return;
        }

        // se i controlli sono passati, crea e salva il nuovo utente
        Utente utente = new Utente();
        utente.setNome(nome);
        utente.setCognome(cognome);
        utente.setEmail(email);
        utente.setPassword(password);
        utente.setTelefono(telefono);
        utente.setDataDiNascita(dataNascita);
        utente.setDataRegistrazione(LocalDate.now());
        utente.setAdmin(false);
        utenteDAO.doSave(utente);

        // per sicurezza rimuove la password dall'oggetto prima di salvarlo in sessione
        utente.setPassword(null);

        // effettua il login automatico, mostrando il messaggio di benvenuto
        HttpSession session = request.getSession();
        session.setAttribute("utenteConnesso", utente);
        session.setAttribute("messaggioHome", "Benvenuto, " + utente.getNome() + "!");
        response.sendRedirect(request.getContextPath() + "/home");
    }

    // Mostra il form di registrazione
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
    }
}
