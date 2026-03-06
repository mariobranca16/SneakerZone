package model.bean;

import java.io.Serial;
import java.io.Serializable;

public class ProdottoCategoria implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long idProdotto;
    private long idCategoria;

    public ProdottoCategoria() {}

    public ProdottoCategoria(long idProdotto, long idCategoria) {
        this.idProdotto = idProdotto;
        this.idCategoria = idCategoria;
    }

    public long getIdProdotto() {
        return idProdotto;
    }
    public void setIdProdotto(long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public long getIdCategoria() {
        return idCategoria;
    }
    public void setIdCategoria(long idCategoria) {
        this.idCategoria = idCategoria;
    }

    @Override
    public String toString() {
        return "ProdottoCategoria [IdProdotto: " + idProdotto +
               ", IdCategoria: " + idCategoria + "]";
    }
}
