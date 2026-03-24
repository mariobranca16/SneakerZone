package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bean.Ordine;
import model.Bean.StatoOrdine;
import model.Bean.Utente;
import model.DAO.OrdineDAO;
import model.DAO.UtenteDAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Gestione la lista di tutti gli ordini nel pannello admin.
 * Permette anche di aggiornare lo stato di ciascuno.
 */
@WebServlet(name = "gestioneOrdiniAdmin", urlPatterns = "/admin/ordini")
public class GestioneOrdiniAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupero di tutti gli ordini dal db
        List<Ordine> ordini = new OrdineDAO().doRetrieveAll();
        // costruisce una mappa email-utenti per evitare di fare troppe query
        UtenteDAO utenteDAO = new UtenteDAO();
        Map<Long, String> emailUtenti = new HashMap<>();
        for (Ordine o : ordini) {
            if (!emailUtenti.containsKey(o.getIdUtente())) { // carica l'email solo se è già stata recuperata
                Utente u = utenteDAO.doRetrieveByKey(o.getIdUtente());
                emailUtenti.put(o.getIdUtente(), u != null ? u.getEmail() : "#" + o.getIdUtente());
            }
        }

        request.setAttribute("ordini", ordini);
        request.setAttribute("emailUtenti", emailUtenti);
        request.setAttribute("stati", StatoOrdine.values());
        request.setAttribute("titoloPagina", "Gestione ordini");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/gestione_ordini.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // lettura id ordine e nuovo stato dalla richiesta
        String idParam = request.getParameter("id");
        String statoParam = request.getParameter("stato");

        // se uno dei parametri manca, ritorna alla pagina di gestione ordini
        if (idParam == null || idParam.isBlank() || statoParam == null || statoParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        long idOrdine;
        // parsing dell'id dell'ordine
        try {
            idOrdine = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            // se l'id non è valido, ritorna alla pagina di gestione ordini
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        // controlla che lo stato ricevuto sia uno degli stati previsti per l'ordine
        StatoOrdine nuovoStato = StatoOrdine.fromString(statoParam);
        if (nuovoStato == null) {
            request.getSession().setAttribute("flashErrore", "Stato ordine non valido");
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        // recupero dell'ordine dal db per verificare che esista
        OrdineDAO ordineDAO = new OrdineDAO();
        Ordine ordine = ordineDAO.doRetrieveByKey(idOrdine);
        if (ordine == null) {
            request.getSession().setAttribute("flashErrore", "Ordine non trovato");
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }

        // aggiorna lo stato dell'ordine
        try {
            ordineDAO.doUpdateStato(idOrdine, nuovoStato);
        } catch (RuntimeException e) {
            // se non riesce per problemi di stock, imposta il messaggio di errore in sessione
            request.getSession().setAttribute("flashErrore", "Impossibile aggiornare lo stato: stock non coerente con la transizione richiesta.");
            response.sendRedirect(request.getContextPath() + "/admin/ordini");
            return;
        }
        // se invece tutto va bene, imposta il messaggio di conferma
        request.getSession().setAttribute("flashSuccesso", "Stato ordine aggiornato con successo");
        response.sendRedirect(request.getContextPath() + "/admin/ordini");
    }
}
