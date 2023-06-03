package database.structure;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 03/06/23
 */
public record prenotazione(int idPrenotazione, int posto) {
    @Override
    public String toString() {
        return String.format(":%s:%s", idPrenotazione, posto);
    }
}
