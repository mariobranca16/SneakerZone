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

@WebServlet(name = "aggiungi-recensione", urlPatterns = "/aggiungi-recensione")
public class AggiungiRecensioneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

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

        HttpSession session = request.getSession(false);
        Utente utente = session != null ? (Utente) session.getAttribute("utenteConnesso") : null;
        if (utente == null || utente.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/prodotto?id=" + idProdotto);
            return;
        }

        RecensioneDAO recensioneDAO = new RecensioneDAO();
        if (!recensioneDAO.haAcquistato(utente.getId(), idProdotto) ||
                recensioneDAO.haGiaRecensito(utente.getId(), idProdotto)) {
            response.sendRedirect(request.getContextPath() + "/prodotto?id=" + idProdotto);
            return;
        }

        String titolo = ValidatoreInput.normalizzaTesto(request.getParameter("titolo"));
        String valutazioneStr = ValidatoreInput.normalizzaTesto(request.getParameter("valutazione"));
        String commento = ValidatoreInput.normalizzaTesto(request.getParameter("commento"));

        request.setAttribute("recensioneTitolo", titolo);
        request.setAttribute("recensioneCommento", commento);
        request.setAttribute("recensioneValutazione", valutazioneStr);

        boolean hasError = false;

        if (!ValidatoreInput.isTitoloRecensioneValido(titolo)) {
            request.setAttribute("erroreTitoloRecensione", !ValidatoreInput.contieneTesto(titolo)
                    ? "Il titolo e obbligatorio."
                    : "Il titolo deve contenere da 3 a 255 caratteri.");
            hasError = true;
        }

        int valutazione = 0;
        if (!ValidatoreInput.contieneTesto(valutazioneStr)) {
            request.setAttribute("erroreValutazioneRecensione", "Seleziona una valutazione da 1 a 5.");
            hasError = true;
        } else {
            try {
                valutazione = Integer.parseInt(valutazioneStr);
                if (valutazione < 1 || valutazione > 5) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                request.setAttribute("erroreValutazioneRecensione", "Seleziona una valutazione da 1 a 5.");
                hasError = true;
            }
        }

        if (!ValidatoreInput.isCommentoRecensioneValido(commento)) {
            request.setAttribute("erroreCommentoRecensione", "Il commento non puo superare 2000 caratteri.");
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("erroreRecensione", "Verifica i campi della recensione e riprova.");
            request.setAttribute("prodotto", prodotto);
            request.setAttribute("recensioni", recensioneDAO.doRetrieveByProdotto(prodotto.getId()));
            request.setAttribute("puoRecensire", true);
            request.getRequestDispatcher("/WEB-INF/jsp/prodotto.jsp").forward(request, response);
            return;
        }

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
