package controller;

import controller.util.ValidatoreInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bean.*;
import model.DAO.IndirizzoSpedizioneDAO;
import model.DAO.OrdineDAO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "checkout", urlPatterns = "/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello == null || carrello.getProdotti().isEmpty()) {
            request.setAttribute("erroreCarrello", "Il tuo carrello e vuoto. Aggiungi almeno un prodotto per procedere.");
            request.getRequestDispatcher("/WEB-INF/jsp/carrello.jsp").forward(request, response);
            return;
        }

        request.setAttribute("carrello", carrello);
        request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        Utente utente = (Utente) session.getAttribute("utenteConnesso");

        if (carrello == null || carrello.getProdotti().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        boolean hasError = false;

        String destinatario = ValidatoreInput.normalizzaTesto(request.getParameter("destinatario"));
        String via = ValidatoreInput.normalizzaTesto(request.getParameter("via"));
        String cap = ValidatoreInput.normalizzaTesto(request.getParameter("cap"));
        String citta = ValidatoreInput.normalizzaTesto(request.getParameter("citta"));
        String provincia = ValidatoreInput.normalizzaTesto(request.getParameter("provincia"));
        String paese = ValidatoreInput.normalizzaTesto(request.getParameter("paese"));

        if (!ValidatoreInput.isDestinatarioValido(destinatario)) {
            request.setAttribute("erroreDestinatario", "Inserisci nome e cognome del destinatario.");
            hasError = true;
        }
        if (!ValidatoreInput.isViaValida(via)) {
            request.setAttribute("erroreVia", "Inserisci un indirizzo completo di numero civico (es. Via Roma 1).");
            hasError = true;
        }
        if (!ValidatoreInput.isCapValido(cap)) {
            request.setAttribute("erroreCap", "Il CAP deve essere di esattamente 5 cifre.");
            hasError = true;
        }
        if (!ValidatoreInput.isLocalitaValida(citta)) {
            request.setAttribute("erroreCitta", "La citta deve avere almeno 2 caratteri e contenere solo lettere.");
            hasError = true;
        }
        if (!ValidatoreInput.isProvinciaValida(provincia)) {
            request.setAttribute("erroreProvincia", "La provincia deve avere 2-5 lettere (es. RM).");
            hasError = true;
        }
        if (!ValidatoreInput.isLocalitaValida(paese)) {
            request.setAttribute("errorePaese", "Il paese deve avere almeno 2 caratteri e contenere solo lettere.");
            hasError = true;
        }

        String nomeCarta = ValidatoreInput.normalizzaTesto(request.getParameter("nomeCarta"));
        String numeroCarta = request.getParameter("numeroCarta");
        String scadenza = ValidatoreInput.normalizzaTesto(request.getParameter("scadenza"));

        if (!ValidatoreInput.isNomeCartaValido(nomeCarta)) {
            request.setAttribute("erroreNomeCarta", "Inserisci nome e cognome come appaiono sulla carta (es. Mario Rossi).");
            hasError = true;
        }
        if (!ValidatoreInput.isNumeroCartaValido(numeroCarta)) {
            request.setAttribute("erroreNumeroCarta", "Inserisci un numero di carta valido (16 cifre).");
            hasError = true;
        }

        String erroreScadenza = ValidatoreInput.getErroreScadenzaCarta(scadenza);
        if (erroreScadenza != null) {
            request.setAttribute("erroreScadenza", erroreScadenza);
            hasError = true;
        }

        String cvv = ValidatoreInput.normalizzaTesto(request.getParameter("cvv"));
        if (!ValidatoreInput.isCvvValido(cvv)) {
            request.setAttribute("erroreCvv", "CVV non valido (3 o 4 cifre).");
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("carrello", carrello);
            request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
            return;
        }

        IndirizzoSpedizione indirizzo = new IndirizzoSpedizione();
        indirizzo.setIdUtente(utente.getId());
        indirizzo.setDestinatario(destinatario);
        indirizzo.setVia(via);
        indirizzo.setCap(cap);
        indirizzo.setCitta(citta);
        indirizzo.setProvincia(provincia);
        indirizzo.setPaese(paese);
        new IndirizzoSpedizioneDAO().doSave(indirizzo);

        Ordine ordine = new Ordine();
        ordine.setIdUtente(utente.getId());
        ordine.setIdIndirizzoSpedizione(indirizzo.getId());
        ordine.setDataOrdine(LocalDate.now());
        ordine.setStato(StatoOrdine.IN_ELABORAZIONE);

        List<DettaglioOrdine> dettagli = new ArrayList<>();
        for (Carrello.ItemCarrello item : carrello.getProdotti()) {
            DettaglioOrdine d = new DettaglioOrdine();
            d.setIdProdotto(item.getProdotto().getId());
            d.setTaglia(item.getTaglia());
            d.setQuantita(item.getQuantita());
            d.setCosto(item.getProdotto().getCosto());
            dettagli.add(d);
        }
        ordine.setDettagliOrdine(dettagli);

        try {
            new OrdineDAO().doSave(ordine);
        } catch (RuntimeException e) {
            request.setAttribute("erroreStock", "Uno o piu prodotti non sono piu disponibili nella quantita richiesta. Aggiorna il carrello e riprova.");
            request.setAttribute("carrello", carrello);
            request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
            return;
        }

        carrello.svuotaCarrello();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        request.setAttribute("dataOrdine", ordine.getDataOrdine().format(dtf));
        request.setAttribute("ordine", ordine);
        request.getRequestDispatcher("/WEB-INF/jsp/conferma_ordine.jsp").forward(request, response);
    }
}
