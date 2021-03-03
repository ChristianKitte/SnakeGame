package SnakeShared;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repräsentiert eine Einheit Futter
 */
public class Feed {
    /**
     * Eindeutiger Schlüssel (ID) des Futters
     */
    private final String id;
    /**
     * Die Position des Futters
     */
    private final Vector2 position;

    /**
     * Wert des Futters
     */
    private final AtomicInteger value;

    /**
     * Konstruktor
     *
     * @param x     X-Koordinate des Futters
     * @param y     Y-Koordinate des Futters
     * @param value Wert des Futters
     */
    public Feed(AtomicInteger x, AtomicInteger y, AtomicInteger value) {
        this.position = Vector2.of(x.get(), y.get());
        this.value = value;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Gibt den Wert des Futters zurück
     *
     * @return Der Wert
     */
    public synchronized AtomicInteger getValue() {
        return this.value;
    }

    /**
     * Vergleicht zwei Futtereinheiten auf Grund Ihres Schlüssels
     *
     * @param obj Das zu vergleichende Objekt
     * @return True, wenn es sich um das gleiche Futter handelt, sonst False
     */
    @Override
    public synchronized boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else if (this == obj) {
            return true;
        }

        Feed f = (Feed) obj;
        if (this.id.equals(f.id)) {
            return true;
        }

        return false;
    }

    /**
     * Überschreibt die Hashmethode. (Hash anhand der ID)
     *
     * @return Den Hashwert
     */
    @Override
    public synchronized int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Die X-Position des Futters
     *
     * @return Der X-Wert
     */
    public synchronized AtomicInteger getX() {
        return new AtomicInteger((int) this.position.x);
    }

    /**
     * Die Y-Position des Futters
     *
     * @return Der Y-Wert
     */
    public synchronized AtomicInteger getY() {
        return new AtomicInteger((int) this.position.y);
    }

    /**
     * Gibt Truezurück, falls das Futter von der übergebenen Position aus erreichbar ist
     *
     * @param X Die X-Position
     * @param Y Die Y-Position
     * @return True, wenn erreichbar
     */
    public synchronized boolean reachable(AtomicInteger X, AtomicInteger Y) {
        Boolean retVal = false;

        AtomicInteger radius = new AtomicInteger(Screen.SEGMENT_RADIUS.get());

        AtomicInteger minX = new AtomicInteger(X.get() - radius.get());
        AtomicInteger maxX = new AtomicInteger(X.get() + radius.get());
        AtomicInteger minY = new AtomicInteger(Y.get() - radius.get());
        AtomicInteger maxY = new AtomicInteger(Y.get() + radius.get());

        synchronized (retVal) {
            if (this.getX().get() > minX.get() && this.getX().get() < maxX.get() && this.getY().get() > minY.get() && this.getY().get() < maxY.get()) {
                retVal = true;
            }
        }

        return retVal;
    }
}
