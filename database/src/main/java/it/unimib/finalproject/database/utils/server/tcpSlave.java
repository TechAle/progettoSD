package it.unimib.finalproject.database.utils.server;

import java.io.*;
import java.net.Socket;

/**
 * Classe che viene usata per gestire le varie connessioni tcp al server
 * @author Alessandro Condello
 * @since 28/05/23
 * @last-modified 29/05/23
 */
@SuppressWarnings("FieldCanBeLocal")
public abstract class tcpSlave extends Thread {

    protected tcpServer father;
    protected Socket client;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected BufferedReader reader;
    protected PrintWriter writer;

    /**
     * Inizializza tutte le variabili per l'utilizzo
     * @param father Il tcp server chiamante
     * @param client La connessione socket che abbiamo con il client
     */
    public tcpSlave(tcpServer father, Socket client) {
        this.father = father;
        this.client = client;
        try {
            // Input stream to read data from the client
            this.inputStream = client.getInputStream();

            // Output stream to send data to the client
            this.outputStream = client.getOutputStream();

            // Buffered reader to read data from the input stream
            this.reader = new BufferedReader(new InputStreamReader(inputStream));

            // Print writer to send data through the output stream
            this.writer = new PrintWriter(outputStream, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param message Invia una stringa con println
     */
    protected void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * @return Una linea che il client ci invia
     */
    protected String getMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onClose() {
        try {
            writer.close();
            reader.close();
            this.inputStream.close();
            this.outputStream.close();
            this.client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void body();

    @Override
    public void run() {
        body();
        onClose();
    }

}
