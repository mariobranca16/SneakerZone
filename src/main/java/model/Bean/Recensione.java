package model.Bean;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Recensione implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private long idUtente;
    private long idProdotto;
    private String titolo;
    private int valutazione;
    private String commento;
    private LocalDate dataRecensione;

    public Recensione() {
    }

    public Recensione(long id, long idUtente, long idProdotto, String titolo, int valutazione, String commento, LocalDate dataRecensione) {
        this.id = id;
        this.idUtente = idUtente;
        this.idProdotto = idProdotto;
        this.titolo = titolo;
        this.valutazione = valutazione;
        this.commento = commento;
        this.dataRecensione = dataRecensione;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public int getValutazione() {
        return valutazione;
    }

    public void setValutazione(int valutazione) {
        this.valutazione = valutazione;
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public LocalDate getDataRecensione() {
        return dataRecensione;
    }

    public void setDataRecensione(LocalDate dataRecensione) {
        this.dataRecensione = dataRecensione;
    }

    @Override
    public String toString() {
        return "Recensione [ID: " + id +
                ", IdUtente: " + idUtente +
                ", IdProdotto: " + idProdotto +
                ", Titolo: " + titolo +
                ", Valutazione: " + valutazione +
                ", DataRecensione: " + dataRecensione + "]";
    }
}
