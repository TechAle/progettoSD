package clientWeb.utils.client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Estendendolo permette una creazione di un client udp
 * Veramente rapida con tante utils già pronte all'utilizzo
 * @author Alessandro Condello
 * @since 28/05/23
 * @last-modified 29/05/23
 */
public abstract class udpClient extends baseClient {
    private DatagramSocket socket;
    private InetAddress serverAddr;


    /**
     * @param address l'ip del server a cui ci connetteremo
     * @param port la porta del server a cui ci connetteremo
     */
    public udpClient(String address, int port) {
        super(address, port);
    }

    /**
     * Inizializzazione della connessione col server
     * E viene chiamato il body che il programmatore può modificare
     */
    @Override
    public void start() {
        try {
            this.socket = new DatagramSocket();
            this.serverAddr = InetAddress.getByName(this.address);
            this.privateBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void sendMessage(String message) {
        try {
            byte[] requestData = message.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length,
                    this.serverAddr, this.port);
            socket.send(requestPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Una stringa di lunghezza massima 1024 byte
     */
    @Override
    protected String getMessage() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

            socket.receive(responsePacket);

            return new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void onClose() {
        this.socket.close();
    }

    protected abstract void body();
}
