package model.Bean;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/*
 * Rappresenta un ordine con i suoi dettagli e l'indirizzo di spedizione.
 */
public class Ordine implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private long id;
    private long idUtente;
    private long idIndirizzoSpedizione;
    private LocalDate dataOrdine;
    private StatoOrdine stato;
    private List<DettaglioOrdine> dettagliOrdine;
    private IndirizzoSpedizione indirizzo;

    public Ordine() {
    }

    public Ordine(long id, long idUtente, long idIndirizzoSpedizione, LocalDate dataOrdine, StatoOrdine stato) {
        this.id = id;
        this.idUtente = idUtente;
        this.idIndirizzoSpedizione = idIndirizzoSpedizione;
        this.dataOrdine = dataOrdine;
        this.stato = stato;
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

    public long getIdIndirizzoSpedizione() {
        return idIndirizzoSpedizione;
    }

    public void setIdIndirizzoSpedizione(long idIndirizzoSpedizione) {
        this.idIndirizzoSpedizione = idIndirizzoSpedizione;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    // restituisce la data dell'ordine nel formato dd/MM/yyyy usato nelle JSP
    public String getDataOrdineFormattata() {
        return dataOrdine != null ? dataOrdine.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public StatoOrdine getStato() {
        return stato;
    }

    public void setStato(StatoOrdine stato) {
        this.stato = stato;
    }

    public List<DettaglioOrdine> getDettagliOrdine() {
        return dettagliOrdine;
    }

    public void setDettagliOrdine(List<DettaglioOrdine> dettagli) {
        this.dettagliOrdine = dettagli;
    }

    public IndirizzoSpedizione getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(IndirizzoSpedizione indirizzo) {
        this.indirizzo = indirizzo;
    }

    // calcola il totale dell'ordine sommando i subtotali delle singole righe
    public double calcolaTotaleOrdine() {
        if (dettagliOrdine == null || dettagliOrdine.isEmpty())
            return 0.0;
        double totale = 0.0;
        for (DettaglioOrdine d : dettagliOrdine) {
            totale += d.getSubtotale();
        }
        return totale;
    }

    public double getTotaleOrdine() {
        return calcolaTotaleOrdine();
    }

    public int getNumeroArticoli() {
        return dettagliOrdine != null ? dettagliOrdine.size() : 0;
    }

    public boolean isAnnullato() {
        return stato == StatoOrdine.ANNULLATO;
    }

    public boolean isSpedito() {
        return stato == StatoOrdine.SPEDITO || stato == StatoOrdine.CONSEGNATO;
    }

    public boolean isConsegnato() {
        return stato == StatoOrdine.CONSEGNATO;
    }

    public boolean isInElaborazione() {
        return stato == StatoOrdine.IN_ELABORAZIONE;
    }

    public String getStatoCssClass() {
        if (stato == null) return "";
        return switch (stato) {
            case CONSEGNATO -> "stato-consegnato";
            case SPEDITO -> "stato-spedito";
            case IN_ELABORAZIONE -> "stato-elaborazione";
            case ANNULLATO -> "stato-annullato";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Ordine ordine = (Ordine) o;
        return id == ordine.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ordine [ID: " + id +
                ", IdUtente: " + idUtente +
                ", IdIndirizzoSpedizione: " + idIndirizzoSpedizione +
                ", DataOrdine: " + dataOrdine +
                ", Stato: " + stato +
                ", Totale: " + calcolaTotaleOrdine() + "]";
    }
}
