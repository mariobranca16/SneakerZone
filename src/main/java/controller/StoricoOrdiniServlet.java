package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Ordine;
import model.Bean.Utente;
import model.DAO.IndirizzoSpedizioneDAO;
import model.DAO.OrdineDAO;

import java.io.IOException;
import java.util.List;

/*
 * Mostra lo storico degli ordini dell'utente loggato.
 * Per ogni ordine recupera anche l'indirizzo di spedizione associato.
 */
@WebServlet(name = "ordini", urlPatterns = "/ordini")
public class StoricoOrdiniServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la sessione corrente e l'utente autenticato
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;
        // se non c'è un utente loggato, rimanda alla pagina di login
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=ordini");
            return;
        }

        // recupera tutti gli ordini dell'utente
        List<Ordine> ordini = new OrdineDAO().doRetrieveByUtente(utente.getId());

        IndirizzoSpedizioneDAO isDAO = new IndirizzoSpedizioneDAO();
        // per ogni ordine dell'utente, recupera l'indirizzo di spedizione
        for (Ordine ordine : ordini) {
            ordine.setIndirizzo(isDAO.doRetrieveByKey(ordine.getIdIndirizzoSpedizione()));
        }

        request.setAttribute("ordini", ordini);
        request.getRequestDispatcher("/WEB-INF/jsp/ordini.jsp").forward(request, response);
    }
}
