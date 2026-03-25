package model.Bean;

import java.io.Serial;
import java.io.Serializable;

/*
 * Rappresenta la lista desideri associata a un utente.
 */
public class Wishlist implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private long idUtente;
    private long idProdotto;

    public Wishlist() {
    }

    public Wishlist(long idUtente, long idProdotto) {
        this.idUtente = idUtente;
        this.idProdotto = idProdotto;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

    public long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(long idProdotto) {
        this.idProdotto = idProdotto;
    }

    @Override
    public String toString() {
        return "Wishlist [IdUtente : " + idUtente +
                ", IdProdotto : " + idProdotto + "]";
    }
}
