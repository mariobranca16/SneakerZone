package model.bean;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Carrello implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static class ItemCarrello implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Prodotto prodotto;
        private int taglia;
        private int quantita;

        public ItemCarrello() {}

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemCarrello ic = (ItemCarrello) o;
            long thisId = (this.prodotto != null) ? this.prodotto.getId() : -1L;
            long itemId = (ic.prodotto != null) ? ic.prodotto.getId() : -1L;
            return thisId == itemId && this.taglia == ic.taglia;
        }

        @Override
        public int hashCode() {
            long id = (this.prodotto != null) ? this.prodotto.getId() : -1L;
            return Objects.hash(id, taglia);
        }
    }

    private List<ItemCarrello> prodotti = new ArrayList<>();

    public Carrello() {}

    public Carrello(List<ItemCarrello> prodotti) {
        if (prodotti != null) this.prodotti = new ArrayList<>(prodotti);
    }

    public List<ItemCarrello> getProdotti() {
        return Collections.unmodifiableList(prodotti);
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
}
