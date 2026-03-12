package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.MetodoPagamento;
import model.bean.Utente;
import model.dao.IndirizzoSpedizioneDAO;
import model.dao.MetodoPagamentoDAO;

import java.io.IOException;

@WebServlet(name = "aggiornaPagamento", urlPatterns = "/myAccount/pagamento")
public class ModificaPagamentoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String nomeCarta = ValidatoreInput.normalizzaTesto(request.getParameter("nomeCarta"));
        String numeroCarta = request.getParameter("numeroCarta");
        String scadenza = ValidatoreInput.normalizzaTesto(request.getParameter("scadenza"));

        boolean hasError = false;

        if (!ValidatoreInput.isNomeCartaValido(nomeCarta)) {
            request.setAttribute("erroreNomeCarta", "Inserisci nome e cognome come appaiono sulla carta (es. Mario Rossi).");
            hasError = true;
        }

        MetodoPagamentoDAO mpDao = new MetodoPagamentoDAO();
        MetodoPagamento existing = mpDao.doRetrieveByUtente(utente.getId());
        String cifre = ValidatoreInput.normalizzaNumeroCarta(numeroCarta);
        String numeroFinale;

        if (!ValidatoreInput.contieneTesto(cifre)) {
            if (existing != null) {
                numeroFinale = existing.getNumeroCarta();
            } else {
                request.setAttribute("erroreNumeroCarta", "Il numero di carta e obbligatorio.");
                hasError = true;
                numeroFinale = null;
            }
        } else if (!ValidatoreInput.isNumeroCartaValido(numeroCarta)) {
            request.setAttribute("erroreNumeroCarta", "Il numero di carta deve essere di 16 cifre.");
            hasError = true;
            numeroFinale = null;
        } else {
            numeroFinale = cifre;
        }

        String erroreScadenza = ValidatoreInput.getErroreScadenzaCarta(scadenza);
        if (erroreScadenza != null) {
            request.setAttribute("erroreScadenza", erroreScadenza);
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("tabAttiva", "pagamento");
            request.setAttribute("utente", utente);
            request.setAttribute("indirizzi", new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId()));
            request.setAttribute("metodoPagamento", mpDao.doRetrieveByUtente(utente.getId()));
            request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
            return;
        }

        MetodoPagamento mp = new MetodoPagamento();
        mp.setIdUtente(utente.getId());
        mp.setNomeCarta(nomeCarta.trim());
        mp.setNumeroCarta(numeroFinale);
        mp.setScadenza(scadenza);
        mpDao.doSaveOrUpdate(mp);

        session.setAttribute("modificaEffettuata", true);
        session.setAttribute("tabAttiva", "pagamento");
        response.sendRedirect(request.getContextPath() + "/myAccount");
    }
}
