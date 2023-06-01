package database.utils.structure;

public record prenotazione(int idPrenotazione, int posto) {
    @Override
    public String toString() {
        return String.format(":%s:%s", idPrenotazione, posto);
    }
}
