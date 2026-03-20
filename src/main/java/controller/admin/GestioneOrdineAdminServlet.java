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
@WebServlet(name = "gestioneOrdineAdmin", urlPatterns = "/admin/ordine")
public class GestioneOrdineAdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }
        long idOrdine;
        try {
            idOrdine = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }
        Ordine ordine = new OrdineDAO().doRetrieveByKey(idOrdine);
        if (ordine == null) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }
        IndirizzoSpedizione indirizzo = new IndirizzoSpedizioneDAO().doRetrieveByKey(ordine.getIdIndirizzoSpedizione());
        ordine.setIndirizzo(indirizzo);
        Utente utente = new UtenteDAO().doRetrieveByKey(ordine.getIdUtente());
        String emailUtente = utente != null ? utente.getEmail() : "#" + ordine.getIdUtente();
        request.setAttribute("ordine", ordine);
        request.setAttribute("emailUtente", emailUtente);
        request.setAttribute("stati", StatoOrdine.values());
        request.setAttribute("titoloPagina", "Dettaglio ordine");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_ordine.jsp").forward(request, response);
    }
}
