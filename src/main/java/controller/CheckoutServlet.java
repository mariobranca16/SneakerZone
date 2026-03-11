package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.*;
import model.dao.IndirizzoSpedizioneDAO;
import model.dao.MetodoPagamentoDAO;
import model.dao.OrdineDAO;

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
        request.setAttribute("metodoPagamento", new MetodoPagamentoDAO().doRetrieveByUtente(utente.getId()));
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
        MetodoPagamentoDAO pagamentoDAO = new MetodoPagamentoDAO();

        boolean hasError = false;
        String nomeCarta = "";
        String numeroCarta = "";
        String scadenza = "";
        String cifre = "";

        String usaCarta = request.getParameter("usaCarta");

        if ("salvata".equals(usaCarta)) {
            MetodoPagamento mpSalvato = pagamentoDAO.doRetrieveByUtente(utente.getId());
            if (mpSalvato == null) {
                request.setAttribute("erroreNomeCarta", "Nessuna carta salvata trovata. Inserisci i dati manualmente.");
                hasError = true;
            } else {
                nomeCarta = mpSalvato.getNomeCarta();
                numeroCarta = mpSalvato.getNumeroCarta();
                cifre = ValidatoreInput.normalizzaNumeroCarta(numeroCarta);
                scadenza = mpSalvato.getScadenza();

                String erroreScadenza = ValidatoreInput.getErroreScadenzaCarta(scadenza);
                if (erroreScadenza != null) {
                    request.setAttribute("erroreScadenza", erroreScadenza);
                    hasError = true;
                }
            }
        } else {
            nomeCarta = ValidatoreInput.normalizzaTesto(request.getParameter("nomeCarta"));
            numeroCarta = request.getParameter("numeroCarta");
            scadenza = ValidatoreInput.normalizzaTesto(request.getParameter("scadenza"));

            if (!ValidatoreInput.isNomeCartaValido(nomeCarta)) {
                request.setAttribute("erroreNomeCarta", "Inserisci nome e cognome come appaiono sulla carta (es. Mario Rossi).");
                hasError = true;
            }

            cifre = ValidatoreInput.normalizzaNumeroCarta(numeroCarta);
            if (!ValidatoreInput.isNumeroCartaValido(numeroCarta)) {
                request.setAttribute("erroreNumeroCarta", "Inserisci un numero di carta valido (16 cifre).");
                hasError = true;
            }

            String erroreScadenza = ValidatoreInput.getErroreScadenzaCarta(scadenza);
            if (erroreScadenza != null) {
                request.setAttribute("erroreScadenza", erroreScadenza);
                hasError = true;
            }
        }

        String cvv = ValidatoreInput.normalizzaTesto(request.getParameter("cvv"));
        if (!ValidatoreInput.isCvvValido(cvv)) {
            request.setAttribute("erroreCvv", "CVV non valido (3 o 4 cifre).");
            hasError = true;
        }

        if (hasError) {
            mostraCheckout(request, response, utente, carrello, indirizzoDAO, pagamentoDAO);
            return;
        }

        String idIndirizzoParam = request.getParameter("idIndirizzo");
        if (idIndirizzoParam == null || idIndirizzoParam.isBlank()) {
            request.setAttribute("erroreIndirizzo", "Seleziona un indirizzo di spedizione.");
            mostraCheckout(request, response, utente, carrello, indirizzoDAO, pagamentoDAO);
            return;
        }

        long idIndirizzo;
        try {
            idIndirizzo = Long.parseLong(idIndirizzoParam);
        } catch (NumberFormatException e) {
            request.setAttribute("erroreIndirizzo", "Indirizzo non valido.");
            mostraCheckout(request, response, utente, carrello, indirizzoDAO, pagamentoDAO);
            return;
        }

        IndirizzoSpedizione indirizzoScelto = indirizzoDAO.doRetrieveByKey(idIndirizzo);
        if (indirizzoScelto == null || indirizzoScelto.getIdUtente() != utente.getId()) {
            request.setAttribute("erroreIndirizzo", "Indirizzo non valido.");
            mostraCheckout(request, response, utente, carrello, indirizzoDAO, pagamentoDAO);
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
            mostraCheckout(request, response, utente, carrello, indirizzoDAO, pagamentoDAO);
            return;
        }

        if (!"salvata".equals(usaCarta) && !cifre.isEmpty()) {
            MetodoPagamento mp = new MetodoPagamento();
            mp.setIdUtente(utente.getId());
            mp.setNomeCarta(nomeCarta.trim());
            mp.setNumeroCarta(cifre);
            mp.setScadenza(scadenza);
            pagamentoDAO.doSaveOrUpdate(mp);
        }

        carrello.svuotaCarrello();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        request.setAttribute("dataOrdine", ordine.getDataOrdine().format(dtf));
        request.setAttribute("ordine", ordine);
        request.getRequestDispatcher("/WEB-INF/jsp/conferma_ordine.jsp").forward(request, response);
    }

    private void mostraCheckout(HttpServletRequest request, HttpServletResponse response,
                                Utente utente, Carrello carrello,
                                IndirizzoSpedizioneDAO indirizzoDAO, MetodoPagamentoDAO pagamentoDAO)
            throws ServletException, IOException {
        request.setAttribute("indirizzi", indirizzoDAO.doRetrieveByUtente(utente.getId()));
        request.setAttribute("metodoPagamento", pagamentoDAO.doRetrieveByUtente(utente.getId()));
        request.setAttribute("carrello", carrello);
        request.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(request, response);
    }
}
