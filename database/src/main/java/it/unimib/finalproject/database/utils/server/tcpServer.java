package it.unimib.finalproject.database.utils.server;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe base per un server tcp
 * @author Alessandro Condello
 * @since 28/05/23
 * @last-modified 29/05/23
 */
public abstract class tcpServer extends baseServer {

    ServerSocket socket;

    /**
     * @param porta La porta dove il server ascolterà
     */
    public tcpServer(int porta, Class<tcpSlave> toIstance) {
        super(porta, false, toIstance);
    }

    /**
     * A server tcp does not need a body
     */
    @Override
    void body() {
    }

    /**
     * This method gets called after we accept a new client
     * @param clientSocket the client
     * @return If we want to keep the client or close the connection
     */
    boolean onNewClient(Socket clientSocket) {
        return true;
    }

    /**
     * This method gets called after we have accepted a new client (and started a thread for him)
     * @param clientSocket the client
     */
    void afterNewClient(Socket clientSocket) {
    }

    /**
     * Inizializza il server ed accetta i vari client
     */
    @Override
    public void start() {
        try {
            this.socket = new ServerSocket(this.port);


            while (true) {
                Socket clientSocket = this.socket.accept();

                // Start a new thread to handle the client connection
                if (this.onNewClient(clientSocket)) {
                    tcpSlave clientThread = this.toIstance.getDeclaredConstructor(tcpServer.class, Socket.class).newInstance(this, clientSocket);
                    clientThread.start();
                    this.afterNewClient(clientSocket);
                } else {
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}

