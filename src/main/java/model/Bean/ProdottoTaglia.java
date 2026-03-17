package model.Bean;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ProdottoTaglia implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long idProdotto;
    private int taglia;
    private int quantita;

    public ProdottoTaglia() {
    }

    public ProdottoTaglia(long idProdotto, int taglia, int quantita) {
        this.idProdotto = idProdotto;
        this.taglia = taglia;
        this.quantita = quantita;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProdottoTaglia that = (ProdottoTaglia) o;
        return idProdotto == that.idProdotto && taglia == that.taglia;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProdotto, taglia);
    }

    @Override
    public String toString() {
        return "ProdottoTaglia [IdProdotto: " + idProdotto + ", Taglia: " + taglia + ", Quantita: " + quantita + "]";
    }
}
