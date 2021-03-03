package SnakeClient;

import SnakeShared.ICallback;
import SnakeShared.Keys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ServerConnection reräsentiert eine Verbidnung zu einem Server
 */
public class ServerConnection extends Thread {
    /**
     * Der verwendete Socket
     */
    private Socket GameServer = null;
    /**
     * Ein PrintWriter Objekt für Nachrichten zum Server
     * (Achtung: endet mit einem Zeilenumbruck)
     */
    private PrintWriter writer = null;
    /**
     * Ein BufferesPrintReader Objekt für Nachrichten vom Server
     */
    private BufferedReader reader = null;

    /**
     * Der verwendete Host
     */
    private final String host;
    /**
     * Der verwendete Port
     */
    private final int port;
    /**
     * Der verwendete (Spieler)Name
     */
    private final String name;

    /**
     * Eine Callbackfunktion um den Client bei neuen Nachrichten zu benachrichtigen
     */
    private ICallback callback;

    /**
     * Der Konstruktor
     *
     * @param host     Der zu verwendende Host
     * @param port     Der zu verwendende Port
     * @param name     Der zu verwendende Name
     * @param callback Die zu verwendende CallBack Funktion
     */
    public ServerConnection(String host, int port, String name, ICallback callback) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.callback = callback;

        connectToGameServer();
    }

    /**
     * Die Run-Funktion des Thread. Hier wird auf eue Nachrichten vom Server gewartet und der Client
     * über die eintreffenden Nachrichten informiert
     */
    @Override
    public void run() {
        while (true) {
            final String msg;

            try {
                if ((msg = reader.readLine()) != null) {
                    synchronized (msg) {
                        this.callback.callback(msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Verbindet sich anhand der übergebenen Daten mit einem Spieleserver und
     * setzt alle im weiteren Betrieb genutzten Eigenschaften
     */
    private void connectToGameServer() {
        try {
            this.GameServer = new Socket(this.host, this.port);

            this.writer = new PrintWriter(this.GameServer.getOutputStream());
            this.reader = new BufferedReader(new InputStreamReader(this.GameServer.getInputStream()));

            sendToGameServer(Keys.NAME_KEY + this.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sendet eine Nachricht zu dem verbundenen Server
     *
     * @param msg Die Nachricht
     */
    public synchronized void sendToGameServer(String msg) {
        if (this.GameServer != null && this.GameServer.isConnected()) {
            writer.println(msg);
            writer.flush();
        }
    }
}
