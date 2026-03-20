package model.Bean;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    public void aggiungiProdotto(Prodotto p, int taglia, int quantita) {
        if (p == null || quantita == 0)
            return;
        for (ItemCarrello i : prodotti) {
            if (i.getProdotto().getId() == p.getId() && i.getTaglia() == taglia) {
                int nuova = i.getQuantita() + quantita;
                if (nuova <= 0)
                    prodotti.remove(i);
                else
                    i.setQuantita(nuova);
                return;
            }
        }
        if (quantita > 0)
            prodotti.add(new ItemCarrello(p, taglia, quantita));
    }
    public void rimuoviProdotto(long idProdotto, int taglia) {
        prodotti.removeIf(i -> i.getProdotto().getId() == idProdotto && i.getTaglia() == taglia);
    }
    public void svuotaCarrello() {
        prodotti.clear();
    }
    public double getTotale() {
        double totale = 0;
        for (ItemCarrello item : prodotti) {
            totale += item.getQuantita() * item.getProdotto().getCosto();
        }
        return totale;
    }
    public int getTotaleArticoli() {
        int totaleArticoli = 0;
        for (ItemCarrello item : prodotti) {
            totaleArticoli += item.getQuantita();
        }
        return totaleArticoli;
    }
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
        public double getSubtotale() {
            return prodotto != null ? prodotto.getCosto() * quantita : 0.0;
        }
    }
}
