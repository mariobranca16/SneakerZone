package model.Bean;
public enum StatoOrdine {
    IN_ELABORAZIONE("in elaborazione"),
    SPEDITO("spedito"),
    CONSEGNATO("consegnato"),
    ANNULLATO("annullato");
    private final String label;
    StatoOrdine(String label) {
        this.label = label;
    }
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
    public boolean isAnnullabile() {
        return this != ANNULLATO && this != SPEDITO && this != CONSEGNATO;
    }
}
