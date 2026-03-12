package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Ordine;
import model.bean.Utente;
import model.dao.IndirizzoSpedizioneDAO;
import model.dao.OrdineDAO;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ordini", urlPatterns = "/ordini")
public class StoricoOrdiniServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utenteConnesso") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=ordini");
            return;
        }

        List<Ordine> ordini = new OrdineDAO().doRetrieveByUtente(utente.getId());
        IndirizzoSpedizioneDAO isDAO = new IndirizzoSpedizioneDAO();

        for(Ordine ordine : ordini){
            ordine.setIndirizzo(isDAO.doRetrieveByKey(ordine.getIdIndirizzoSpedizione()));
        }

        request.setAttribute("ordini",ordini);
        request.getRequestDispatcher("/WEB-INF/jsp/ordini.jsp").forward(request,response);

    }
}
