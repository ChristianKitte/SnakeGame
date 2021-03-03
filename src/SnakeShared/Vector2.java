package SnakeShared;

public class Vector2 {
    /**
     * Vektoren sind unveränderlich - jede Rechenoperation erzeugt einen neuen Vektor
     * x und y werden also bei Kontstruktion einmal gesetzt und sind dann unveränderlich
     */
    public final float x;
    public final float y;

    /**
     * Konstruktor privat - wir nutzen eine einfache Factory-Methode of zur Erzeugung
     */
    private Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Factory-Methode:
     * <p>
     * Vector2 v = Vector2.of(2,3);
     * <p>
     * erzeugt den Vektor (2,3)
     */
    public static Vector2 of(float x, float y) {
        return new Vector2(x, y);
    }

    /**
     * Addiert einen Vektor zu diesem Vektor
     *
     * @param other Ein zweiter Vektor
     * @return Ein neuer Vektor als Summe beider Vektoren
     */
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    /**
     * Subtrahiert einen Vektor von diesem Vektor
     *
     * @param other Ein zweiter Vektor
     * @return Ein neuer Vektor als Differenz beider Vektoren
     */
    public Vector2 sub(Vector2 other) {
        return this.add(other.mult(-1));
    }

    /**
     * Gibt einen um alpha rotierten Vektor zurück
     *
     * @param alpha Grad um den im positiven Sinn rotiert werden soll
     * @return Ein neuer Vektor als Ergebnis
     */
    public Vector2 rotate(float alpha) {
        return Matrix2.rotate(alpha).mult(this);
    }

    /**
     * Multipliziert mit einer Zahl
     *
     * @param d Der Multiplikator
     * @return Ein neuer Vektor als Ergebnis
     */
    public Vector2 mult(float d) {
        return new Vector2(this.x * d, this.y * d);
    }

    /**
     * Dividiert durch eine Zahl
     *
     * @param d Der Teiler
     * @return Ein neuer Vektor als Ergebnis
     */
    public Vector2 div(float d) {
        return this.mult(1 / d);
    }

    /**
     * Gibt den inversen Vektor zurück
     *
     * @return Der inverse Vektor
     */
    public Vector2 neg() {
        return this.mult(-1);
    }

    /**
     * Gibt die Länge des Vektors zurück
     *
     * @return Die Länge
     */
    public float length() {
        return (float) Math.sqrt(Math.pow((float) this.x, 2) + Math.pow((float) this.y, 2));
    }

    /**
     * Gibt den Einheitsvektor zurück
     *
     * @return Ein neuer Vektor als Ergebnis
     */
    public Vector2 unit() {
        return this.mult(1 / length());
    }

    /**
     * Berechnet das Skalarprodukt mit einem anderen Vektor
     *
     * @param other Ein anderer Vektor
     * @return Das Skalarprodukt
     */
    public float skalarProd(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * String-Darstellung für Debug-Ausgabe
     */
    @Override
    public String toString() {
        return "Vector2 {" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * wie genau vergleichen wir? Wird in der equals-Methode verwendet
     */
    private static final float EPSILON = 0.0001f;

    /**
     * Vergleich mit anderen Vektoren
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2 vector2 = (Vector2) o;

        if (Math.abs(vector2.x - x) > EPSILON) return false;
        return Math.abs(vector2.y - y) < EPSILON;
    }
}
