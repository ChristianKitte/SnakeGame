package SnakeShared;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Statische Klasse mit Vorgaben für die visuelle Ausgabe
 */
public class Screen {
    /**
     * Breite des Screen
     */
    public static final AtomicInteger SCREEN_X = new AtomicInteger(1024);
    /**
     * Höhe des Screen
     */
    public static final AtomicInteger SCREEN_Y = new AtomicInteger(768);
    /**
     * Radius des Schlangensegmentes
     */
    public static final AtomicInteger SEGMENT_RADIUS = new AtomicInteger(20);
    /**
     * Definiert einen Rahmen für die grafische Ausgabe von Komponenten
     */
    public static final double PADDING_VALUE = 10.0;
    /**
     * Definiert einen Zwischenraum für die Ausgabe von grafischen Komponenten
     */
    public static final double SPACING_VALUE = 10.0;
    /**
     * Schrift und Größe der Spielerausgabe
     */
    public static final Font FONT_PLAYER_FONT = new Font("Arial", 10);
    /**
     * Farbe der Spielerausgabe
     */
    public static final Color FONT_PLAYER_COLOR = Color.RED;
    /**
     * Hintergrundfarbe der Spielerausgabe
     */
    public static final Color FONT_PLAYER_BACKCOLOR = Color.GREENYELLOW;
}
