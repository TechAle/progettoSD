package it.unimib.finalproject.database.structure.server;

import java.net.InetAddress;

/**
 * @author Alessandro Condello
 * @since 1/06/23
 * @last-modified 03/06/23
 */
public record message(String message, InetAddress ip, int port) {
}
