package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bean.Recensione;
import model.DAO.RecensioneDAO;

import java.io.IOException;

/*
 * Permette all'amministratore di eliminare una recensione.
 * Dopo l'eliminazione, fa il redirect alla pagina del prodotto.
 */
@WebServlet(name = "eliminaRecensioneAdmin", urlPatterns = "/admin/rimuovi-recensione")
public class EliminaRecensioneAdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // legge l'id della recensione da cancellare
        String idParam = request.getParameter("idRecensione");
        if (idParam == null || idParam.isBlank()) { // se manca oppure è vuoto, fa redirect alla pagina di gestione prodotti
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        long idRecensione;
        // parsing dell'id della recensione
        try {
            idRecensione = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        // recupero della recensione del db
        RecensioneDAO recensioneDAO = new RecensioneDAO();
        Recensione recensione = recensioneDAO.doRetrieveByKey(idRecensione);
        // se la recensione non esiste non ha senso procedere, quindi ritorna alla pagina di gestione prodotti
        if (recensione == null) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        // elimina la recensione dal db e salva un messaggio di conferma in sessione
        recensioneDAO.doDelete(idRecensione);
        request.getSession().setAttribute("flashSuccesso", "Recensione eliminata con successo");
        response.sendRedirect(request.getContextPath() + "/admin/prodotto?id=" + recensione.getIdProdotto());
    }
}
