package SnakeServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Stellt einen einfachen Server zur Aufnahme von Spielern zur Verfügung
 */
public class GameServer extends Thread {
    /**
     * Eine Instanz des Spiels
     */
    Game game = null;

    /**
     * Der Konstruktor
     */
    public GameServer() {
        this.start();

        this.game = new Game();
        this.game.start();
    }

    /**
     * Die Run-Methode des Threads. Empfängt neue Spieler und fügt sie dem Spiel
     * zu
     */
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);

            while (true) {
                Socket client = serverSocket.accept();

                Player player = new Player(client, this.game);
                player.start();

                this.game.addPlayer(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
