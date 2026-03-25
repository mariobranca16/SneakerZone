package model.Bean;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * Carrello salvato nella sessione utente.
 * Durante la navigazione resta in sessione, viene poi trasformato in ordine durante il checkout e svuotato.
 */
public class Carrello implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private List<ItemCarrello> prodotti = new ArrayList<>();

    public Carrello() {
    }

    public Carrello(List<ItemCarrello> prodotti) {
        if (prodotti != null) this.prodotti = new ArrayList<>(prodotti);
    }

    public List<ItemCarrello> getProdotti() {
        return prodotti;
    }

    /*
     * Aggiunge un prodotto al carrello.
     * Se esiste già un prodotto con la stessa taglia, aggiorna la quantità.
     */
    public void aggiungiProdotto(Prodotto p, int taglia, int quantita) {
        if (p == null || quantita == 0)
            return;
        // cerca se l'articolo è già presente con la stessa taglia
        for (ItemCarrello i : prodotti) {
            if (i.getProdotto().getId() == p.getId() && i.getTaglia() == taglia) {
                int nuova = i.getQuantita() + quantita;
                // se la nuova quantità scende a zero o meno, l'articolo viene rimosso
                if (nuova <= 0)
                    prodotti.remove(i);
                else
                    i.setQuantita(nuova);
                return;
            }
        }
        // se invece non c'è ancora, aggiunge il nuovo articolo
        if (quantita > 0)
            prodotti.add(new ItemCarrello(p, taglia, quantita));
    }

    public void rimuoviProdotto(long idProdotto, int taglia) {
        prodotti.removeIf(i -> i.getProdotto().getId() == idProdotto && i.getTaglia() == taglia);
    }

    // Svuota completamente il carrello (chiamato da CheckoutServlet dopo l'ordine confermato).
    public void svuotaCarrello() {
        prodotti.clear();
    }

    // Per calcolare il totale del carrello sommando tutti gli articoli
    public double getTotale() {
        double totale = 0;
        for (ItemCarrello item : prodotti) {
            totale += item.getQuantita() * item.getProdotto().getCosto();
        }
        return totale;
    }

    // per recuperare il numero di articoli presenti nel carrello
    public int getTotaleArticoli() {
        int totaleArticoli = 0;
        for (ItemCarrello item : prodotti) {
            totaleArticoli += item.getQuantita();
        }
        return totaleArticoli;
    }

    /*
     * Rappresenta un singolo prodotto nel carrello con taglia e quantità associate.
     */
    public static class ItemCarrello implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private Prodotto prodotto;
        private int taglia;
        private int quantita;

        public ItemCarrello() {
        }

        public ItemCarrello(Prodotto prodotto, int taglia, int quantita) {
            this.prodotto = prodotto;
            this.taglia = taglia;
            this.quantita = quantita;
        }

        public Prodotto getProdotto() {
            return prodotto;
        }

        public void setProdotto(Prodotto prodotto) {
            this.prodotto = prodotto;
        }

        public int getTaglia() {
            return taglia;
        }

        public void setTaglia(int taglia) {
            this.taglia = taglia;
        }

        public int getQuantita() {
            return quantita;
        }

        public void setQuantita(int quantita) {
            this.quantita = quantita;
        }

        // Calcola il subtotale dell'articolo corrente
        public double getSubtotale() {
            return prodotto != null ? prodotto.getCosto() * quantita : 0.0;
        }
    }
}
