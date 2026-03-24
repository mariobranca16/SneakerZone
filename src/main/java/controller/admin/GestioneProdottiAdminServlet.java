package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bean.Prodotto;
import model.DAO.ProdottoDAO;

import java.io.IOException;
import java.util.List;

/*
 * Gestisce la lista di tutti i prodotti mostrata nel pannello admin.
 * Permette anche la cancellazione di ciascuno.
 */
@WebServlet(name = "gestioneProdottiAdmin", urlPatterns = "/admin/prodotti")
public class GestioneProdottiAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // recupero di tutti i prodotti dal db
        List<Prodotto> prodotti = new ProdottoDAO().doRetrieveAll();
        request.setAttribute("prodotti", prodotti);
        request.setAttribute("titoloPagina", "Gestione prodotti");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_prodotti.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // legge l'azione richiesta e l'id del prodotto
        String azione = request.getParameter("azione");
        String idParam = request.getParameter("id");

        // se uno dei parametri manca, ritorna alla pagina di gestione prodotti
        if (azione == null || azione.isBlank() || idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        long id;
        // parsing dell'id del prodotto
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            // se l'id non è valido, ritorna alla pagina di gestione prodotti
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        // recupero del prodotto dal db per verificare che esista
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        Prodotto prodotto = prodottoDAO.doRetrieveByKey(id);
        if (prodotto == null) {
            request.getSession().setAttribute("flashErrore", "Prodotto non trovato");
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }

        // esegue la cancellazione solo se l'azione richiesta corrisponde ad "elimina"
        if ("elimina".equalsIgnoreCase(azione)) {
            prodottoDAO.doDelete(id);
            request.getSession().setAttribute("flashSuccesso", "Prodotto eliminato con successo");
        } else {
            // se l'azione non è valida, ritorna alla pagina di gestione prodotti
            response.sendRedirect(request.getContextPath() + "/admin/prodotti");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/prodotti");
    }
}
