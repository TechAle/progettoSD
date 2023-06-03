package database.structure.server;

import java.net.InetAddress;

public record message(String message, InetAddress ip, int port) {
}
