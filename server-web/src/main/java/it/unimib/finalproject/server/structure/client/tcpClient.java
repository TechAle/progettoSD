package it.unimib.finalproject.server.structure.client;

import java.io.*;
import java.net.Socket;

/**
 * Estendendolo permette una creazione di un client tcp
 * Veramente rapida con tante utils già pronte all'utilizzo
 * @author Alessandro Condello
 * @since 28/05/23
 * @last-modified 29/05/23
 */
public abstract class tcpClient extends baseClient {
    private PrintWriter writer;
    private Socket socket;
    private BufferedReader reader;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * @param address l'ip del server a cui ci connetteremo
     * @param port la porta del server a cui ci connetteremo
     */
    public tcpClient(String address, int port) {
        super(address, port);
    }


    @Override
    public void start() {
        try {
            socket = new Socket(this.address, this.port);
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.writer = new PrintWriter(outputStream, true);
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
            this.privateBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * @return La linea che il server ci invia
     */
    @Override
    protected String getMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void onClose() {
        try {
            writer.close();
            reader.close();
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void body();
}
