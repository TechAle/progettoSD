package serverWeb.structure.client;

/**
 * Classe base per un client socket
 * @author Alessandro Condello
 * @since 28/05/23
 * @last-modified 29/05/23
 */
abstract class baseClient {

    protected final String address;
    protected final int port;

    /**
     * @param address L'ip a cui il client si connetterà
     * @param port La porta a cui il client si connetterà
     */
    public baseClient(String address, int port) {
        this.address = address;
        this.port = port;
    }


    /**
     * Inizializzazione della connessione col server
     * E viene chiamato il body che il programmatore può modificare
     */
    abstract void start();

    /**
     * Una volta finite le varie operazioni che il client deve fare, si deve disconnettere.
     * Viene chiamato privateBody da start
     */
    void privateBody() {
        body();
        onClose();
    }

    /**
     * Scrivere qui il codice che vuoi il client esegua
     */
    abstract void body();

    /**
     * @param message Messaggio da inviare
     */
    abstract void sendMessage(String message);

    /**
     * Vari close per una terminazione graceful
     */
    abstract void onClose();


    abstract String getMessage();
}
