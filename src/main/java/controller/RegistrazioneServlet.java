package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Utente;
import model.dao.UtenteDAO;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "registrazione", value = "/registrazione")
public class RegistrazioneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String telefono = request.getParameter("telefono");
        String dataNascitaStr = request.getParameter("dataNascita");

        if (nome == null || cognome == null || email == null || password == null ||
                telefono == null || dataNascitaStr == null || nome.isBlank() || cognome.isBlank() ||
                email.isBlank() || password.isBlank() || telefono.isBlank() || dataNascitaStr.isBlank()) {

            request.setAttribute("errore", "Compila tutti i campi obbligatori");
            request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
            return;
        }

        LocalDate dataNascita;
        try {
            dataNascita = LocalDate.parse(dataNascitaStr);
        } catch (Exception e) {
            request.setAttribute("errore", "Data di nascita non valida");
            request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
            return;
        }

        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            request.setAttribute("errore", "Formato email non valido");
            request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
            return;
        }

        if (password.length() < 8) {
            request.setAttribute("errore", "La password deve avere almeno 8 caratteri");
            request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
            return;
        }

        if (!telefono.matches("[+]?[\\d\\s\\-]{8,15}")) {
            request.setAttribute("errore", "Numero di telefono non valido");
            request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
            return;
        }

        if (LocalDate.now().minusYears(18).isBefore(dataNascita)) {
            request.setAttribute("errore", "Devi avere almeno 18 anni per registrarti");
            request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
            return;
        }

        UtenteDAO utenteDAO = new UtenteDAO();
        if (utenteDAO.doExistsByEmail(email)) {
            request.setAttribute("errore", "Email già registrata");
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

        HttpSession session = request.getSession();
        session.setAttribute("utenteConnesso", utente);
        response.sendRedirect(request.getContextPath() + "/home");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/registrazione.jsp").forward(request, response);
    }
}
