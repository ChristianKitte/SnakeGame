package SnakeServer;

import SnakeShared.Keys;
import SnakeShared.Rotation;
import SnakeShared.Snake;
import SnakeShared.Vector2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Repräsentiert einen Spieler mit der zughörigen Schlange
 */
public class Player extends Thread {
    /**
     * Die Anzahl der erzeugten Instanzen
     */
    private static int clientNumber = 0;

    /**
     * Der Socket des Spielers
     */
    private Socket client = null;

    /**
     * Die Schlange des Spielers
     */
    private Snake snake = null;

    /**
     * Die letzte Rotationsangebe des Spielers
     */
    private Rotation rotation = Rotation.NONE;

    /**
     * Ein Verweis auf das Spiel
     */
    private Game game = null;

    /**
     * Der Name des Spielers
     */
    private String name = null;

    /**
     * Ein PrintWriter zu Übermittlung von Nachrichten zum Client
     */
    private PrintWriter writer = null;

    /**
     * Ein BufferedReader zum Empfang von Nachrichten des Clients
     */
    private BufferedReader reader = null;

    /**
     * Die lezte Nachricht
     */
    private String lastMSG = null;

    /**
     * Der Konstruktor
     *
     * @param client Der Socket des Clients
     * @param game   Ein Verweis auf das Spiel
     */
    public Player(Socket client, Game game) {
        this.game = game;
        this.client = client;
        this.name = (String) ("Spieler: " + clientNumber);
        this.snake = new Snake(Vector2.of(100, 100), 1, this.name);

        try {
            this.writer = new PrintWriter(client.getOutputStream());
            this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            SendMessage("Connected to server: " + this.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Die Run-Methode des Threads. Empfängt und verarbeitet Nachrichten
     * vom Client
     */
    @Override
    public void run() {
        String msg = null;

        while (true) {
            try {
                while ((msg = reader.readLine()) != null) {
                    if (msg.startsWith(Keys.NAME_KEY)) {
                        this.name = msg.substring(5);
                        this.snake.setName(this.name);
                    } else if (msg.equals(Keys.ROTATION_KEY_LEFT)) {
                        this.rotation = Rotation.LEFT;
                    } else if (msg.equals(Keys.ROTATION_KEY_RIGHT)) {
                        this.rotation = Rotation.RIGHT;
                    } else if (msg.equals(Keys.ROTATION_KEY_NONE)) {
                        this.rotation = Rotation.NONE;
                    }

                    lastMSG = msg;
                }
            } catch (IOException e) {
                this.game.removePlayer(this);
                break;
            }
        }
    }

    /**
     * Gibt die aktuelle Rotation zurück
     *
     * @return Ein Rotationswert
     */
    public synchronized Rotation getRotation() {
        return this.rotation;
    }

    /**
     * Gibt die Schlange zurück
     *
     * @return Die Schlange
     */
    public synchronized Snake getSnake() {
        return this.snake;
    }

    /**
     * Sendet die Übergebene Textnachricht zum zughörigen Client
     *
     * @param msg Die Nachricht
     */
    public synchronized void SendMessage(String msg) {
        writer.println(msg);
        writer.flush();
    }

    /**
     * Erzeugt eine komplett neue Schlange für den Spieler
     */
    public synchronized void resetSnake() {
        this.snake = new Snake(Vector2.of(100, 100), 1, this.name);
    }
}
