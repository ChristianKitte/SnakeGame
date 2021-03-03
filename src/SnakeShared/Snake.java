package SnakeShared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representiert eine Schlange
 */
public class Snake implements Serializable {
    /**
     * Der Name
     */
    private String name = "Snake";
    /**
     * Die Drehgeschwindigkeit
     * Darf nicht final sein, da eine Erzeugung über CreationString
     * möglich sein muss.
     */
    private float TURN_SPEED = 2.0f;

    /**
     * Die aktuelle Position
     */
    Vector2 position;
    /**
     * Die aktuelle Richtung
     */
    Vector2 direction = Vector2.of(1, 0);

    /**
     * Eine Liste der Schlangensegmente
     */
    List<Vector2> segments = Collections.synchronizedList(new ArrayList<>());
    /**
     * Der aktuelle Punktestand
     */
    AtomicInteger feedPoints = new AtomicInteger(0);

    /**
     * Der Konstruktor
     *
     * @param position Die Anfangsposition
     * @param length   Die Anfangslänge
     */
    public Snake(Vector2 position, int length) {
        this.position = position;

        AtomicInteger i = new AtomicInteger();
        for (i.set(0); i.get() < length; i.getAndIncrement()) {
            segments.add(Vector2.of(position.x, position.y));
        }
    }

    /**
     * Der Konstruktor
     *
     * @param position Die Anfangsposition
     * @param length   Die Anfangslänge
     * @param name     Der Name
     */
    public Snake(Vector2 position, int length, String name) {
        this(position, length);
        this.name = name;
    }

    /**
     * Der Konstruktor (erzeugt eine Schlange anhand eines Creationstrings)
     *
     * @param creationString Der Creationstring
     */
    public Snake(String creationString) {
        String[] strings = creationString.substring(Keys.SNAKE_KEY.length()).split(";");

        this.name = strings[0];
        this.TURN_SPEED = Float.parseFloat(strings[1]);
        this.feedPoints.set(Integer.parseInt(strings[2]));

        float x = Float.parseFloat(strings[3]);
        float y = Float.parseFloat(strings[4]);
        this.position = Vector2.of(x, y);

        x = Float.parseFloat(strings[5]);
        y = Float.parseFloat(strings[6]);
        this.direction = Vector2.of(x, y);

        AtomicInteger length = new AtomicInteger(Integer.parseInt(strings[7]));
        AtomicInteger position = new AtomicInteger(8);
        for (int i = 0; i < length.get(); i++) {
            x = Float.parseFloat(strings[position.get()]);
            y = Float.parseFloat(strings[position.incrementAndGet()]);
            this.segments.add(Vector2.of(x, y));

            position.getAndIncrement();
        }
    }

    /**
     * Gibt einen Creationstring für die aktuelle Schlange zurück
     *
     * @return Der Creationstring
     */
    public synchronized String getCreationString() {
        String retVal = Keys.SNAKE_KEY;

        retVal += this.name + ";";
        retVal += Float.toString(this.TURN_SPEED) + ";";
        retVal += Integer.toString(this.feedPoints.get()) + ";";

        retVal += Float.toString(this.position.x) + ";";
        retVal += Float.toString(this.position.y) + ";";

        retVal += Float.toString(this.direction.x) + ";";
        retVal += Float.toString(this.direction.y) + ";";

        retVal += Integer.toString(this.segments.size()) + ";";
        for (Vector2 segment : this.segments) {
            retVal += Float.toString(segment.x) + ";";
            retVal += Float.toString(segment.y) + ";";
        }

        return retVal;
    }

    /**
     * Gibt die Anzahl der Punkte zurück
     *
     * @return Die Punktzahl
     */
    public synchronized AtomicInteger getFeedPoints() {
        return feedPoints;
    }

    /**
     * Gibt den Namen zurück
     *
     * @return Der Name
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Legt den Namen fest
     *
     * @param name Der Name
     */
    public synchronized void setName(String name) {
        this.name = name;
    }

    /**
     * Bewegt die Schlange, lässt sie essen
     *
     * @param direction    Die Richtung
     * @param distance     Die Strecke
     * @param feedProvider Ein Verweis auf den Futterprovider
     */
    public synchronized void goAndFeet(Rotation direction, float distance, FeedProvider feedProvider) {
        //Richtung
        switch (direction) {
            case LEFT:
                this.direction = this.direction.rotate(-1 * TURN_SPEED);
                break;
            case RIGHT:
                this.direction = this.direction.rotate(TURN_SPEED);
                break;
        }

        //Kopf positionieren
        Vector<Vector2> newSegments = new Vector<>();

        Vector2 headMain = this.getHead();
        Vector2 vec3 = headMain.add(this.direction);
        newSegments.add(headMain.add(this.direction));

        //Essen und wachsen
        final List<Feed> removedFeeds = Collections.synchronizedList(new ArrayList<>());
        for (Feed feed : feedProvider.getFeed()) {
            if (feed.reachable(new AtomicInteger((int) headMain.x), new AtomicInteger((int) headMain.y))) {
                removedFeeds.add(feed);
            }
        }

        for (Feed feed : removedFeeds) {
            eat(feed);
            feedProvider.deleteFeed(feed);
        }

        //Schlangensegmente neu berechnen
        AtomicInteger i = new AtomicInteger();
        for (i.set(1); i.get() < this.getLength().get(); i.getAndIncrement()) {
            AtomicInteger segment = new AtomicInteger(i.get() - 1);
            Vector2 head = this.getSegment(segment);
            Vector2 tail = this.getSegment(i);
            Vector2 div = head.sub(tail);

            Vector2 vector2 = tail.add(div.mult(distance));
            newSegments.add(vector2);
        }

        this.segments = newSegments;
    }

    /**
     * Gibt den Kopf der Schlange als Vektor zurück (Entspricht dem Segment 0)
     *
     * @return Der Kopf
     */
    public synchronized Vector2 getHead() {
        return this.segments.get(0);
    }

    /**
     * Gibt das Segment als Vektor an der durch index bezeichneten Stelle zurück
     *
     * @param index Der Index
     * @return Das Segment am Index
     */
    public synchronized Vector2 getSegment(AtomicInteger index) {
        return this.segments.get(index.get());
    }

    /**
     * Gibt die Länge der Schlange zurück (Entspricht der Anzahl der Segmente)
     *
     * @return Die Länge
     */
    public synchronized AtomicInteger getLength() {
        return new AtomicInteger(this.segments.size());
    }

    /**
     * Stößt den Vorgang Essen mit dem übergebenen Feed an
     *
     * @param feed Das aufzunehmende Essen
     */
    public synchronized void eat(Feed feed) {
        AtomicInteger i = new AtomicInteger();

        for (i.set(0); i.get() < feed.getValue().get(); i.incrementAndGet()) {
            this.feedPoints = new AtomicInteger(this.feedPoints.addAndGet(feed.getValue().get()));
            Vector2 v = this.segments.get(this.segments.size() - 1);
            this.segments.add(Vector2.of(v.x, v.y));
        }
    }
}
