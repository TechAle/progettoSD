package database.cheatsheet.server;

/**
 * @author Alessandro Condello
 * @since 28/05/23
 * @last-modified 29/05/23
 * Classe base per un server socket
 */
abstract class baseServer {
    protected final int port;
    protected final boolean async;
    protected final Class<tcpSlave> toIstance;

    /**
     * @param porta     La porta a cui il server ascolterà
     * @param async     Se vogliamo fare eseguire la ricezione dei emssaggi in maniera asincrona (solo udp)
     *                  È necessario metterlo qui in comune anche per tcp per via di autoStart
     * @param toIstance Usata dal server tcp, serve per dire a che classe fare riferimento quando si crea un client
     */
    protected baseServer(int porta, boolean async, Class<tcpSlave> toIstance) {
        this.port = porta;
        this.async = async;
        this.toIstance = toIstance;
    }

    /**
     * Scrivere qui il codice che vuoi il server esegua aggiuntivamente
     */
    abstract void body();

    /**
     * Inizializzazione del server
     */
    abstract void start();
}
