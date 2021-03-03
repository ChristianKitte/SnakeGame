package SnakeShared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Managed die Futtererzeugung und -verwaltung
 */
public class FeedProvider extends Thread {
    /**
     * Die Größe der Futterfläche
     */
    private AtomicInteger areaWidth = new AtomicInteger(0);
    /**
     * Die Höhe der Futterfläche
     */
    private AtomicInteger areaHeight = new AtomicInteger(0);
    /**
     * Der kleinste Wert eines Futters
     */
    private AtomicInteger minValue = new AtomicInteger(0);
    /**
     * Der größte Wert eines Futters
     */
    private AtomicInteger maxValue = new AtomicInteger(0);
    /**
     * Die anfängliche Menge an Futter
     */
    private AtomicInteger initialFeed = new AtomicInteger(0);
    /**
     * Der Mindesabstand zu einem Rand
     */
    private AtomicInteger borderOffset = new AtomicInteger(0);
    /**
     * Eine Liste mit Futter
     */
    private List<Feed> feed = Collections.synchronizedList(new ArrayList<>());

    /**
     * Der Konstruktor
     *
     * @param width        Die Breite der Futterfläche
     * @param height       Die Höhe der Futterfläche
     * @param borderOffset Der Midnesabstand zum Rand
     * @param minValue     Der Midnestwert einer Futtereinheit
     * @param maxValue     Der maximale Wert einer Futtereinheit
     * @param initialFeed  Die Menge an anfänglichen Futter
     */
    public FeedProvider(int width, int height, int borderOffset, int minValue, int maxValue, int initialFeed) {
        this.areaWidth.set(width);
        this.areaHeight.set(height);
        this.borderOffset.set(borderOffset);
        this.minValue.set(minValue);
        this.maxValue.set(maxValue);
        this.initialFeed.set(initialFeed);

        createInitialFeed();
    }

    /**
     * Die Run-Klasse des Thread. Sorgt dafür, dass immer die Mindestmenge
     * an Futter vorhanden ist
     */
    @Override
    public void run() {
        while (true) {
            synchronized (this.feed) {
                AtomicInteger i = new AtomicInteger();
                for (i.set(this.feed.size()); i.get() < this.initialFeed.get(); i.getAndIncrement()) {
                    this.feed.add(createFeed());
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Löscht das Übergebene Futter aus der Liste der Futtereinheiten
     *
     * @param feed Das zu löschende Futter
     */
    public synchronized void deleteFeed(Feed feed) {
        synchronized (this.feed) {
            this.feed.remove(feed);
        }
    }

    /**
     * Gibt das aktuell vorhandene Futter als Liste zurück
     *
     * @return Das Futter
     */
    public List<Feed> getFeed() {
        synchronized (this.feed) {
            return this.feed;
        }
    }

    /**
     * Erzeugt die in initialFeed angegebene Menge an Futter
     */
    private void createInitialFeed() {
        synchronized (this.feed) {
            AtomicInteger i = new AtomicInteger();
            for (i.set(0); i.get() < this.initialFeed.get(); i.getAndIncrement()) {
                this.feed.add(createFeed());
            }
        }
    }

    /**
     * Erzeugt eine Futtereinheit innerhalb der Futterfläsche mit einem zufälligen
     * Wert und einer eindeutigen ID und gibt dies zurück
     *
     * @return Die neue Futtereinheit
     */
    private Feed createFeed() {
        synchronized (this.feed) {
            AtomicInteger posX = new AtomicInteger();
            posX.set((int) ((Math.random() + 0.1) * areaWidth.get()));
            if (posX.get() > areaWidth.get()) {
                posX.set(areaWidth.get());
            } else if (posX.get() < 0) {
                posX.set(0);
            }

            AtomicInteger posY = new AtomicInteger();
            posY.set((int) ((Math.random() + 0.1) * areaHeight.get()));
            if (posY.get() > areaHeight.get()) {
                posY.set(areaHeight.get());
            } else if (posY.get() < 0) {
                posY.set(0);
            }

            AtomicInteger value = new AtomicInteger();
            value.set((int) ((Math.random() + 0.1) * maxValue.get()));
            if (value.get() < minValue.get()) {
                value.set(minValue.get());
            }

            AtomicInteger x = new AtomicInteger(posX.get() - this.borderOffset.get());
            AtomicInteger y = new AtomicInteger(posY.get() - this.borderOffset.get());
            return new Feed(x, y, value);
        }
    }

    /**
     * Erezugt einen String mit den Positionen aller Futtereinheiten
     *
     * @return Den String der Futtereinheiten
     */
    public String getCreationString() {
        String retVal = Keys.FEED_KEY;

        retVal += Integer.toString(this.feed.size()) + ";";

        synchronized (this.feed) {
            for (Feed feed : this.feed) {
                retVal += Float.toString(feed.getX().get()) + ";";
                retVal += Float.toString(feed.getY().get()) + ";";
            }
        }

        return retVal;
    }
}
