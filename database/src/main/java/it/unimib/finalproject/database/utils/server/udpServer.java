package it.unimib.finalproject.database.utils.server;


import it.unimib.finalproject.database.structure.server.message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe base per un server udp
 * @author Alessandro Condello
 * @since 28/05/23
 * @last-modified 29/05/23
 */
public abstract class udpServer extends baseServer {

    @SuppressWarnings("FieldCanBeLocal")
    private final int BUFFER_LENGTH = 1024; // Questo può essere modificato a seconda delle esigenze del programmatore
    private DatagramSocket socket;
    final Object locker = new Object();

    List<message> incomingMessages = new ArrayList<>();



    /**
     *
     * @param porta La porta del server
     * @param async Se vogliamo il server asincorno oppure no
     *              Asincrono = Riceviamo i messaggi asincronamente, e nel mentre facciamo altre cose
     */
    public udpServer(int porta, boolean async) {
        super(porta, async, null);
    }

    protected abstract void body();

    /**
     * @param response Il messaggio che vogliamo inviare
     * @param address L'ip del cliente a cui vogliamo inviare il messaggio
     * @param port La porta
     */
    public void sendMessage(String response, InetAddress address, int port) {
        try {
            byte[] responseData = response.getBytes();

            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,address, port);
            socket.send(responsePacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Una struttura chiamata "message" che contiene tutte le informazioni riguardante un messaggio
     */
    @SuppressWarnings("UnusedReturnValue")
    public message waitMessage() {
        try {
            do {
                byte[] buffer = new byte[this.BUFFER_LENGTH];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                message output = new message(message, packet.getAddress(), packet.getPort());
                if (async) {
                    synchronized (locker) {
                        this.incomingMessages.add(output);
                    }
                } else
                    return output;
            }while (true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void start() {
        try {
            this.socket = new DatagramSocket(this.port);
            if (async)
                new Thread(this::waitMessage).start();
            body();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}

