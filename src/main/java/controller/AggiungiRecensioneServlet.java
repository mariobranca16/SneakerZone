package controller;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.Prodotto;
import model.Bean.Recensione;
import model.Bean.Utente;
import model.DAO.ProdottoDAO;
import model.DAO.RecensioneDAO;

import java.io.IOException;
import java.time.LocalDate;

/*
 * Gestisce l'inserimento di una recensione.
 * Prima di salvare, verifica che l'utente abbia acquistato il prodotto e non lo abbia già recensito.
 */
@WebServlet(name = "aggiungi-recensione", urlPatterns = "/aggiungi-recensione")
public class AggiungiRecensioneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // recupero e validazione dell'id prodotto, se non è valido reindirizza al catalogo
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }
        long idProdotto;
        try {
            idProdotto = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        Prodotto prodotto = new ProdottoDAO().doRetrieveByKey(idProdotto);
        if (prodotto == null) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        // verifica che l'utente sia loggato e non sia admin (gli admin non recensiscono)
        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;
        if (utente == null || utente.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/prodotto?id=" + idProdotto);
            return;
        }

        // verifica che l'utente abbia acquistato il prodotto e non lo abbia già recensito
        RecensioneDAO recensioneDAO = new RecensioneDAO();
        if (!recensioneDAO.haAcquistato(utente.getId(), idProdotto) ||
                recensioneDAO.haGiaRecensito(utente.getId(), idProdotto)) {
            response.sendRedirect(request.getContextPath() + "/prodotto?id=" + idProdotto);
            return;
        }

        // lettura e validazione dei campi della recensione
        String titolo = ValidatoreInput.normalizzaTesto(request.getParameter("titolo"));
        String valutazioneStr = ValidatoreInput.normalizzaTesto(request.getParameter("valutazione"));
        String commento = ValidatoreInput.normalizzaTesto(request.getParameter("commento"));

        // mantiene i valori nella request per ripopolare il form in caso di errore
        request.setAttribute("recensioneTitolo", titolo);
        request.setAttribute("recensioneCommento", commento);
        request.setAttribute("recensioneValutazione", valutazioneStr);

        boolean hasError = false;

        // validazione del formato del titolo
        if (!ValidatoreInput.isTitoloRecensioneValido(titolo)) {
            request.setAttribute("erroreTitoloRecensione", !ValidatoreInput.contieneTesto(titolo)
                    ? "Il titolo e obbligatorio."
                    : "Il titolo deve contenere da 3 a 255 caratteri.");
            hasError = true;
        }

        // validazione del formato della valutazione, deve essere un intero compreso tra 1 e 5
        int valutazione = 0;
        if (!ValidatoreInput.contieneTesto(valutazioneStr)) {
            request.setAttribute("erroreValutazioneRecensione", "Seleziona una valutazione da 1 a 5.");
            hasError = true;
        } else {
            try {
                valutazione = Integer.parseInt(valutazioneStr);
            } catch (NumberFormatException e) {
                valutazione = 0;
            }
            if (valutazione < 1 || valutazione > 5) {
                request.setAttribute("erroreValutazioneRecensione", "Seleziona una valutazione da 1 a 5.");
                hasError = true;
            }
        }

        // validazione del formato del commento (max 2000 caratteri)
        if (!ValidatoreInput.isCommentoRecensioneValido(commento)) {
            request.setAttribute("erroreCommentoRecensione", "Il commento non puo superare 2000 caratteri.");
            hasError = true;
        }

        // in caso di errori, ricarica la pagina prodotto con il form compilato
        if (hasError) {
            request.setAttribute("erroreRecensione", "Verifica i campi della recensione e riprova.");
            request.setAttribute("prodotto", prodotto);
            request.setAttribute("recensioni", recensioneDAO.doRetrieveByProdotto(prodotto.getId()));
            request.setAttribute("puoRecensire", true);
            request.getRequestDispatcher("/WEB-INF/jsp/prodotto.jsp").forward(request, response);
            return;
        }

        // crea e salva la recensione
        Recensione r = new Recensione();
        r.setIdUtente(utente.getId());
        r.setIdProdotto(idProdotto);
        r.setTitolo(titolo);
        r.setValutazione(valutazione);
        r.setCommento(ValidatoreInput.contieneTesto(commento) ? commento : null);
        r.setDataRecensione(LocalDate.now());
        recensioneDAO.doSave(r);

        response.sendRedirect(request.getContextPath() + "/prodotto?id=" + idProdotto + "&successoRecensione=1");
    }
}
