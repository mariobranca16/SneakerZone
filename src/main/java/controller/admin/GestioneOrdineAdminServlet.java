package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bean.IndirizzoSpedizione;
import model.Bean.Ordine;
import model.Bean.StatoOrdine;
import model.Bean.Utente;
import model.DAO.IndirizzoSpedizioneDAO;
import model.DAO.OrdineDAO;
import model.DAO.UtenteDAO;

import java.io.IOException;

/*
 * Mostra il dettaglio di un singolo ordine nel pannello admin.
 */
@WebServlet(name = "gestioneOrdineAdmin", urlPatterns = "/admin/ordine")
public class GestioneOrdineAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // legge l'id dell'ordine dalla richiesta
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        long idOrdine;
        // parsing dell'id dell'ordine
        try {
            idOrdine = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            // se l'id non è valido, ritorna alla lista ordini
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        // recupero dell'ordine dal db
        Ordine ordine = new OrdineDAO().doRetrieveByKey(idOrdine);
        if (ordine == null) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        // recupera l'indirizzo di spedizione associato all'ordine
        IndirizzoSpedizione indirizzo = new IndirizzoSpedizioneDAO().doRetrieveByKey(ordine.getIdIndirizzoSpedizione());
        ordine.setIndirizzo(indirizzo);
        Utente utente = new UtenteDAO().doRetrieveByKey(ordine.getIdUtente());
        String emailUtente = utente != null ? utente.getEmail() : "#" + ordine.getIdUtente();

        // recupera l'utente collegato all'ordine per mostrare l'email
        request.setAttribute("ordine", ordine);
        request.setAttribute("emailUtente", emailUtente);
        request.setAttribute("stati", StatoOrdine.values());
        request.setAttribute("titoloPagina", "Dettaglio ordine");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_ordine.jsp").forward(request, response);
    }
}
