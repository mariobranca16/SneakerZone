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
        Utente utente = (Utente) session.getAttribute("utenteConnesso");

        if (carrello == null || carrello.getProdotti().isEmpty()) {
            request.setAttribute("erroreCarrello", "Il tuo carrello e vuoto. Aggiungi almeno un prodotto per procedere.");
            request.getRequestDispatcher("/WEB-INF/jsp/carrello.jsp").forward(request, response);
            return;
        }

        request.setAttribute("indirizzi", new IndirizzoSpedizioneDAO().doRetrieveByUtente(utente.getId()));
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

        IndirizzoSpedizioneDAO indirizzoDAO = new IndirizzoSpedizioneDAO();

        boolean hasError = false;

        String nomeCarta = ValidatoreInput.normalizzaTesto(request.getParameter("nomeCarta"));
        String numeroCarta = request.getParameter("numeroCarta");
        String scadenza = ValidatoreInput.normalizzaTesto(request.getParameter("scadenza"));
        String cifre = ValidatoreInput.normalizzaNumeroCarta(numeroCarta);

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
            mostraCheckout(request, response, utente, carrello, indirizzoDAO);
            return;
        }

        String idIndirizzoParam = request.getParameter("idIndirizzo");
        if (idIndirizzoParam == null || idIndirizzoParam.isBlank()) {
            request.setAttribute("erroreIndirizzo", "Seleziona un indirizzo di spedizione.");
            mostraCheckout(request, response, utente, carrello, indirizzoDAO);
            return;
        }

        long idIndirizzo;
        try {
            idIndirizzo = Long.parseLong(idIndirizzoParam);
        } catch (NumberFormatException e) {
            request.setAttribute("erroreIndirizzo", "Indirizzo non valido.");
            mostraCheckout(request, response, utente, carrello, indirizzoDAO);
            return;
        }

        IndirizzoSpedizione indirizzoScelto = indirizzoDAO.doRetrieveByKey(idIndirizzo);
        if (indirizzoScelto == null || indirizzoScelto.getIdUtente() != utente.getId()) {
            request.setAttribute("erroreIndirizzo", "Indirizzo non valido.");
            mostraCheckout(request, response, utente, carrello, indirizzoDAO);
            return;
        }

        Ordine ordine = new Ordine();
        ordine.setIdUtente(utente.getId());
        ordine.setIdIndirizzoSpedizione(idIndirizzo);
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
            mostraCheckout(request, response, utente, carrello, indirizzoDAO);
            return;
        }

        carrello.svuotaCarrello();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        request.setAttribute("dataOrdine", ordine.getDataOrdine().format(dtf));
        request.setAttribute("ordine", ordine);
        request.getRequestDispatcher("/WEB-INF/jsp/conferma_ordine.jsp").forward(request, response);
    }

    private void mostraCheckout(HttpServletRequest request, HttpServletResponse response,
                                Utente utente, Carrello carrello,
                                IndirizzoSpedizioneDAO indirizzoDAO)
            throws ServletException, IOException {
        request.setAttribute("indirizzi", indirizzoDAO.doRetrieveByUtente(utente.getId()));
        request.setAttribute("carrello", carrello);
        request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
    }
}
