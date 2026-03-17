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

@WebServlet(name = "registrazione", value = "/registrazione")
public class RegistrazioneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String nome = ValidatoreInput.normalizzaTesto(request.getParameter("nome"));
        String cognome = ValidatoreInput.normalizzaTesto(request.getParameter("cognome"));
        String email = ValidatoreInput.normalizzaTesto(request.getParameter("email"));
        String password = request.getParameter("password");
        String telefono = ValidatoreInput.normalizzaTelefono(request.getParameter("telefono"));
        String dataNascitaStr = ValidatoreInput.normalizzaTesto(request.getParameter("dataNascita"));

        boolean hasError = false;

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

        UtenteDAO utenteDAO = new UtenteDAO();
        if (!ValidatoreInput.isEmailValida(email)) {
            request.setAttribute("erroreEmail", !ValidatoreInput.contieneTesto(email)
                    ? "Campo obbligatorio."
                    : "Formato email non valido.");
            hasError = true;
        } else if (utenteDAO.doExistsByEmail(email)) {
            request.setAttribute("erroreEmail", "Email già registrata.");
            hasError = true;
        }

        if (password == null || password.isBlank()) {
            request.setAttribute("errorePassword", "Campo obbligatorio.");
            hasError = true;
        } else if (!ValidatoreInput.isPasswordForte(password)) {
            request.setAttribute("errorePassword",
                    "Usa 8-64 caratteri con maiuscola, minuscola, numero e simbolo, senza spazi.");
            hasError = true;
        }

        if (!ValidatoreInput.isTelefonoValido(telefono)) {
            request.setAttribute("erroreTelefono", !ValidatoreInput.contieneTesto(telefono)
                    ? "Campo obbligatorio."
                    : "Numero di telefono non valido.");
            hasError = true;
        }

        LocalDate dataNascita = null;
        if (!ValidatoreInput.contieneTesto(dataNascitaStr)) {
            request.setAttribute("erroreDataNascita", "Campo obbligatorio.");
            hasError = true;
        } else {
            try {
                dataNascita = LocalDate.parse(dataNascitaStr);
                if (ValidatoreInput.isMinorenne(dataNascita)) {
                    request.setAttribute("erroreDataNascita", "Devi avere almeno 18 anni per registrarti.");
                    hasError = true;
                }
            } catch (Exception e) {
                request.setAttribute("erroreDataNascita", "Data di nascita non valida.");
                hasError = true;
            }
        }

        if (hasError) {
            request.setAttribute("formNome", nome);
            request.setAttribute("formCognome", cognome);
            request.setAttribute("formEmail", email);
            request.setAttribute("formTelefono", request.getParameter("telefono"));
            request.setAttribute("formDataNascita", dataNascitaStr);
            request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
            return;
        }

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
        utente.setPassword(null);

        HttpSession session = request.getSession();
        session.setAttribute("utenteConnesso", utente);
        response.sendRedirect(request.getContextPath() + "/home");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
    }
}
