package model.Bean;

/*
 * Rappresenta i possibili stati di un ordine.
 */
public enum StatoOrdine {
    IN_ELABORAZIONE("in elaborazione"),
    SPEDITO("spedito"),
    CONSEGNATO("consegnato"),
    ANNULLATO("annullato");
    private final String label;

    StatoOrdine(String label) {
        this.label = label;
    }

    // converte la stringa letta dal DB nel corrispondente valore enum
    // se il valore non è valido, restituisce null.
    public static StatoOrdine fromString(String value) {
        if (value == null) return null;
        try {
            return StatoOrdine.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getLabel() {
        return label;
    }

    // un ordine è annullabile solo finché non è stato spedito, consegnato, oppure già annullato
    public boolean isAnnullabile() {
        return this != ANNULLATO && this != SPEDITO && this != CONSEGNATO;
    }
}
