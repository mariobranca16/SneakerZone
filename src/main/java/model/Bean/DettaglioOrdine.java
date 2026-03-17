package model.Bean;

import java.util.Objects;

public class DettaglioOrdine {
    private long idOrdine;
    private long idProdotto;
    private int taglia;
    private int quantita;
    private double costo;
    private Prodotto prodotto;

    public DettaglioOrdine() {
    }

    public DettaglioOrdine(long idOrdine, long idProdotto, int taglia, int quantita, double costo) {
        this.idOrdine = idOrdine;
        this.idProdotto = idProdotto;
        this.taglia = taglia;
        this.quantita = quantita;
        this.costo = costo;
    }

    public long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(long idProdotto) {
        this.idProdotto = idProdotto;
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

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public void setProdotto(Prodotto prodotto) {
        this.prodotto = prodotto;
    }

    public double getSubtotale() {
        return costo * quantita;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DettaglioOrdine d = (DettaglioOrdine) o;
        return idOrdine == d.idOrdine &&
                idProdotto == d.idProdotto &&
                this.taglia == d.taglia;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrdine, idProdotto, taglia);
    }

    @Override
    public String toString() {
        return "DettaglioOrdine [IdOrdine: " + idOrdine +
                ", IdProdotto: " + idProdotto +
                ", Taglia: " + taglia +
                ", Quantità: " + quantita +
                ", Costo: " + costo +
                ", Prodotto: " + (prodotto != null ? prodotto.getNome() : null) + "]";
    }
}
