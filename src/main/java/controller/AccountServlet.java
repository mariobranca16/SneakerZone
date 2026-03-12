package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.IndirizzoSpedizione;
import model.bean.MetodoPagamento;
import model.bean.Utente;
import model.dao.IndirizzoSpedizioneDAO;
import model.dao.MetodoPagamentoDAO;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "myAccount", urlPatterns = "/myAccount")
public class AccountServlet extends HttpServlet {
    private void caricaDatiPagina(HttpServletRequest request, Utente utente) {
        request.setAttribute("utente", utente);
        List<IndirizzoSpedizione> indirizzi =
                new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId());
        request.setAttribute("indirizzi", indirizzi);
        MetodoPagamento mp = new MetodoPagamentoDAO().doRetrieveByUtente(utente.getId());
        request.setAttribute("metodoPagamento", mp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Boolean conferma = (Boolean) session.getAttribute("modificaEffettuata");
        if (Boolean.TRUE.equals(conferma)) {
            request.setAttribute("modificaEffettuata", true);
            session.removeAttribute("modificaEffettuata");
        }

        String tabAttiva = (String) session.getAttribute("tabAttiva");
        if (tabAttiva != null) {
            request.setAttribute("tabAttiva", tabAttiva);
            session.removeAttribute("tabAttiva");
        }

        caricaDatiPagina(request, utente);
        request.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(request, response);
    }
}
