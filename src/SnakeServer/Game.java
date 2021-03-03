package SnakeServer;

import SnakeShared.FeedProvider;
import SnakeShared.Screen;
import SnakeShared.Snake;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Game extends Thread {
    /**
     * Der einzige Feedprovider des Spiels
     */
    private final FeedProvider feedProvider;
    /**
     * Die Spielgeschwindigkeit
     */
    private final long SPEED = 15;

    /**
     * Eine Liste der aktuellen Spieler
     */
    private List<Player> playerList = Collections.synchronizedList(new ArrayList<>());

    /**
     * Der Konstruktor. Initialisiert den Futterprovider und startet seinen
     * Thread
     */
    public Game() {
        feedProvider = new FeedProvider(Screen.SCREEN_X.get(), Screen.SCREEN_Y.get(), 80, 1, 3, 20);
        feedProvider.start();
    }

    /**
     * Fügt dem Spiel einen neuen Spieler hinzu
     *
     * @param player Der hinzuzufügende Spieler
     */
    public void addPlayer(Player player) {
        String p = player.getName();
        this.playerList.add(player);
        this.doBroadcast("Enter: " + p);
    }

    /**
     * Entfernt einen Spieler aus dem Spiel
     *
     * @param player Der zu entfernende Spieler
     */
    public void removePlayer(Player player) {
        String p = player.getName();
        this.playerList.remove(player);
        this.doBroadcast("Leave: " + p);
    }

    /**
     * Die Run-Methode des Thread. Geht die Spielerauflistung und ruft für jede Schlange
     * deren goAndFeet-Methode auf
     */
    @Override
    public void run() {
        while (true) {
            for (Player player : playerList) {
                Snake snake = player.getSnake();

                if (snake.getLength().get() > 0) {
                    snake.goAndFeet(player.getRotation(), 0.1f, this.feedProvider);

                    if (snake.getHead().x < 0 || snake.getHead().x > Screen.SCREEN_X.get()
                            || snake.getHead().y < 0 || snake.getHead().y > Screen.SCREEN_Y.get()) {
                        //Game Over  ==> Schlange, wird auf den Urzustand zurück gesetzt
                        player.resetSnake();
                    }


                }
            }

            doBroadcast();

            try {
                sleep(SPEED);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sendet an alle Spieler den neuen Status bezüglich Futter und Schlange
     */
    private synchronized void doBroadcast() {
        String snakes = "";

        for (Player player : playerList) {
            snakes += player.getSnake().getCreationString() + "#";
        }

        for (Player player : playerList) {
            player.SendMessage(this.feedProvider.getCreationString());
            player.SendMessage(snakes.substring(0, snakes.length() - 1));
        }
    }

    /**
     * Sendet an alle Spieler die übergebene Nachricht
     *
     * @param msg Die Nachricht
     */
    private synchronized void doBroadcast(String msg) {
        for (Player player : playerList) {
            player.SendMessage(msg);
        }
    }
}
