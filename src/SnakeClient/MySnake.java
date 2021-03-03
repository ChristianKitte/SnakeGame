package SnakeClient;

import SnakeShared.Keys;
import SnakeShared.Screen;
import SnakeShared.Snake;
import SnakeShared.Vector2;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repäsentiert ein Client und die Startklasse der Clientanwendung
 */
public class MySnake extends Application {
    /**
     * Definiert den Titel der Anwendung
     */
    private final String HEADER_TITLE = "Snake";

    /**
     * Hält das aktuelle Futter
     */
    List<Point2D> feeds = Collections.synchronizedList(new ArrayList<>());

    /**
     * Gibt an, ob Punkte angezeigt werden aollen
     */
    private boolean ShowPoints = true;

    /**
     * Eine Auflistung aller Schlangen
     */
    private List<Snake> snakes = Collections.synchronizedList(new ArrayList<>());
    /**
     * Der grafische Kontekt zur Grafikausgabe
     */
    private GraphicsContext gc;

    /**
     * Der Host des Spieleservers
     */
    private String host = "127.0.0.1";
    /**
     * Der Port des Spieleservers
     */
    private int port = 8888;
    /**
     * Ein Defaultname für den Spieler
     */
    private String name = "SNOOPY";
    /**
     * Ein ServerConnection Objekt, das den GameServer repräsentiert
     */
    private ServerConnection gameServer = null;

    /**
     * Einstiegsfunktion der JavaFX Anwendung
     *
     * @param primaryStage Das Stageobjekt der Anwendung
     * @throws Exception Löst im Fehlerfall eine Exception aus, die nicht behandelt wird
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setWidth(Screen.SCREEN_X.get());
        primaryStage.setHeight(Screen.SCREEN_Y.get());
        primaryStage.setTitle(HEADER_TITLE);
        primaryStage.setResizable(false);

        Image anotherIcon = new Image(getClass().getResourceAsStream("duke_44x80.png"));
        primaryStage.getIcons().add(anotherIcon);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });

        VBox content = new VBox(Screen.SPACING_VALUE, getButtons(primaryStage), getCanvas(primaryStage));
        content.setStyle("-fx-background-color: #17e7ff");
        content.setPadding(new Insets(Screen.PADDING_VALUE));

        Group root = new Group();
        root.getChildren().add(content);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:
                    gameServer.sendToGameServer(Keys.ROTATION_KEY_LEFT);
                    break;
                case RIGHT:
                    gameServer.sendToGameServer(Keys.ROTATION_KEY_RIGHT);
                    break;
            }
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case LEFT:
                    gameServer.sendToGameServer(Keys.ROTATION_KEY_NONE);
                    break;
                case RIGHT:
                    gameServer.sendToGameServer(Keys.ROTATION_KEY_NONE);
                    break;
            }
        });

        primaryStage.setScene(scene);

        primaryStage.sizeToScene();
        primaryStage.show();

        setName();
        setIP();

        this.gameServer = new ServerConnection(this.host, this.port, this.name, (s) -> {
            if (s.startsWith(Keys.FEED_KEY)) {
                this.RefreshFeed(s);
            } else if (s.startsWith(Keys.SNAKE_KEY)) {
                this.RefreshSnake(s);
            }
        });

        this.gameServer.start();
    }

    /**
     * Erzeugt aus einem Feedstring eine Auflistung von Feed
     *
     * @param feetString Der Feedstring mit Futterpositionen
     */
    private void RefreshFeed(String feetString) {
        Platform.runLater(() -> {
            synchronized (this.feeds) {
                this.feeds.clear();

                String[] strings = feetString.substring(5).split(";");

                AtomicInteger size = new AtomicInteger(Integer.parseInt(strings[0]));
                AtomicInteger position = new AtomicInteger(0);

                AtomicInteger i = new AtomicInteger();
                for (i.set(0); i.get() < size.get(); i.getAndIncrement()) {
                    double x = Double.parseDouble(strings[position.get()]);
                    double y = Double.parseDouble(strings[position.incrementAndGet()]);

                    this.feeds.add(new Point2D(x, y));
                }

                this.draw();
            }
        });
    }

    /**
     * Erzeugt aus einem Snakestring eine Auflistung von Schlangen
     *
     * @param snakeString Der Snakestring mit Schlangen
     */
    private void RefreshSnake(String snakeString) {
        Platform.runLater(() -> {
            String[] snakes = snakeString.split("#");

            synchronized (this.snakes) {
                this.snakes.clear();
                for (String snake : snakes) {
                    this.snakes.add(new Snake(snake));
                }

                this.draw();
            }
        });
    }

    /**
     * Erzeugt die Buttons der Oberfläche
     *
     * @param primaryStage Das Stageobjekt der Buttons
     * @return Eine HBox mit den Buttons
     */
    private HBox getButtons(Stage primaryStage) {
        HBox hbox = new HBox(Screen.SPACING_VALUE);
        hbox.setPadding(new Insets(Screen.PADDING_VALUE));

        Button btnExit = new Button("Schließen");
        btnExit.setOnAction(e -> {
            primaryStage.close();
            Platform.exit();
            System.exit(0);
        });

        hbox.getChildren().addAll(btnExit);
        return hbox;
    }

    /**
     * Erzeugt eine Canvas und setzt den grafischen Kontekt
     *
     * @param primaryStage Das Stageobjekt der Canvas
     * @return Ein Canvas Object
     */
    private Canvas getCanvas(Stage primaryStage) {
        Canvas canvas = new Canvas(primaryStage.getWidth(), primaryStage.getHeight());
        this.gc = canvas.getGraphicsContext2D();

        return canvas;
    }

    /**
     * Fragt den Spielernamen ab und belegt die Eigenschaft
     */
    private void setName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Spielername");
        dialog.setContentText("Bitte geben Sie einen Namen ein:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (!result.get().trim().equals("")) {
                this.name = result.get();
            } else {
                setName();
            }
        } else {
            Platform.exit();
            System.exit(0);
        }
    }

    /**
     * Fragt die IP-Adresse ab (verwendet 127.0.0.1 bei Abbruch oder "")
     */
    private void setIP() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("IP-Adresse");
        dialog.setContentText("Bitte geben Sie die IP-Adresse an: ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (!result.get().trim().equals("")) {
                this.host = result.get();
            }
        }
    }

    /**
     * Macht die grafische Ausgabe der Schlangen und des Futters
     */
    public void draw() {
        gc.setFill(Color.ANTIQUEWHITE);
        gc.fillRect(0, 0, Screen.SCREEN_X.get(), Screen.SCREEN_Y.get());

        synchronized (feeds) {
            for (Point2D point2D : feeds) {
                gc.strokeRect(point2D.getX(), point2D.getY(), 5, 5);
            }
        }

        synchronized (this.snakes) {
            for (Snake snake : this.snakes) {
                synchronized (snake) {
                    AtomicInteger i = new AtomicInteger();

                    for (i.set(0); i.get() < snake.getLength().get(); i.getAndIncrement()) {
                        Vector2 segment = snake.getSegment(i);
                        gc.strokeOval(segment.x, segment.y, Screen.SEGMENT_RADIUS.get(), Screen.SEGMENT_RADIUS.get());

                        gc.setFill(Color.CORNFLOWERBLUE);
                        gc.fillOval(segment.x, segment.y, Screen.SEGMENT_RADIUS.get(), Screen.SEGMENT_RADIUS.get());
                    }
                    printSnakeName(snake);
                }
            }
        }
    }

    /**
     * Druckt den Namen einer Schlange
     *
     * @param snake Die Schlange, für die der Name gesetzt werden soll
     */
    private void printSnakeName(Snake snake) {
        AtomicInteger countSegments = new AtomicInteger(snake.getLength().get());
        AtomicInteger targetSegment = new AtomicInteger(0);
        if (countSegments.get() > 2) {
            targetSegment = new AtomicInteger(countSegments.get() / 2);
        }

        gc.setFont(Screen.FONT_PLAYER_FONT);
        Text textOutput = new Text();

        if (ShowPoints) {
            textOutput.setText(snake.getName() + ": " + Integer.toString(snake.getFeedPoints().get()));
        } else {
            textOutput.setText(snake.getName());
        }

        double textLength = textOutput.getBoundsInLocal().getWidth();
        double textHeight = textOutput.getBoundsInLocal().getHeight();

        gc.setFill(Screen.FONT_PLAYER_BACKCOLOR);
        gc.fillRect(snake.getSegment(targetSegment).x, snake.getSegment(targetSegment).y, textLength, textHeight); // Schrift in Mitte

        gc.setFill(Screen.FONT_PLAYER_COLOR);
        gc.fillText(textOutput.getText(), snake.getSegment(targetSegment).x, snake.getSegment(targetSegment).y + textHeight); // Schrift in Mitte
    }
}
