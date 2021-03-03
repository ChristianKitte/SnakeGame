package SnakeShared;

/*
    Eine einfache Klasse zur Verwaltung von 2x2-Matrizen
 */
public class Matrix2 {
    // die Werte der Matrix - wir speichern sie nicht als Array, sondern direkt in 4 Variablen
    public final float a11; // erste Zeile, erste Spalte
    public final float a21; // zweite Zeile, erste Spalte
    public final float a12; // erste Zeile, zweite Spalte
    public final float a22; // zweite Zeile, zweite Spalte

    /**
     * Privater Konstruktor
     *
     * @param a11 erste Zeile, erste Spalte
     * @param a12 zweite Zeile, erste Spalte
     * @param a21 erste Zeile, zweite Spalte
     * @param a22 zweite Zeile, zweite Spalte
     */
    private Matrix2(float a11, float a12, float a21, float a22) {
        this.a11 = a11;
        this.a21 = a21;
        this.a12 = a12;
        this.a22 = a22;
    }

    /**
     * Berechnet den Sinus von Alpha in float
     *
     * @param alpha Gradangabe
     * @return Der Sinus von alpha
     */
    private static float sin(float alpha) {
        return (float) Math.sin(alpha);
    }

    /**
     * Berechnet den Cosinus von Alpha float
     *
     * @param alpha Gradangabe
     * @return Der Cosinus von alpha
     */
    private static float cos(float alpha) {
        return (float) Math.cos(alpha);
    }

    /**
     * Factory-Methode zur Erzeugung einer MAtrix
     *
     * @param a11 erste Zeile, erste Spalte
     * @param a12 zweite Zeile, erste Spalte
     * @param a21 erste Zeile, zweite Spalte
     * @param a22 zweite Zeile, zweite Spalte
     * @return Die resultierende Matrix
     */
    public static Matrix2 of(float a11, float a12, float a21, float a22) {
        return new Matrix2(a11, a12, a21, a22);
    }

    /**
     * Die Einheitsmatrix
     *
     * @return Gibt die Einheitsmatrix zurück
     */
    public static Matrix2 unit() {
        return Matrix2.of(
                1, 0,
                0, 1
        );
    }

    /**
     * Erzeugt eine Rotationsmatrix
     *
     * @param alpha Der Rotationswinkel
     * @return Die Rotationsmatrix
     */
    public static Matrix2 rotate(float alpha) {
        double alphaRadians = Math.toRadians(alpha);
        float sinVal = sin((float) alphaRadians);
        float cosVal = cos((float) alphaRadians);

        return new Matrix2(cosVal, -1 * sinVal, sinVal, cosVal);
    }

    /**
     * Erzeugt eine Skalierungsmatrix
     *
     * @param sx Skalierung in x
     * @param sy Skalierung in y
     * @return Die Skalierungsmatrix
     */
    public static Matrix2 scale(float sx, float sy) {
        return new Matrix2(sx, 0, 0, sy);
    }

    /**
     * Multipliziert mit einer Matrix
     *
     * @param m Eine andere Matrix
     * @return Das Ergebnis als neue Matrix
     */
    public Matrix2 mult(Matrix2 m) {
        float new11 = this.a11 * m.a11 + a12 * m.a21;
        float new12 = this.a11 * m.a12 + a12 * m.a22;
        float new21 = this.a21 * m.a11 + a22 * m.a21;
        float new22 = this.a21 * m.a12 + a22 * m.a22;

        return new Matrix2(new11, new12, new21, new22);
    }

    /**
     * Multipliziert mit einem Vektor
     *
     * @param v Ein anderer Vektor
     * @return Der resultierende Vektor
     */
    public Vector2 mult(Vector2 v) {
        float new11 = this.a11 * v.x + this.a12 * v.y;
        float new21 = this.a21 * v.x + this.a22 * v.y;

        return Vector2.of(new11, new21);
    }

    /**
     * Vergleicht die MAtrix mit einer anderen
     *
     * @param o Eine andere Matrix
     * @return True, wenn sie gleich sind, sonst False
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Matrix2 matrix2 = (Matrix2) o;

        if (Float.compare(matrix2.a11, a11) != 0) return false;
        if (Float.compare(matrix2.a21, a21) != 0) return false;
        if (Float.compare(matrix2.a12, a12) != 0) return false;

        return Float.compare(matrix2.a22, a22) == 0;
    }

    /**
     * Debugausgabe
     *
     * @return Die textliche Entsprechung
     */
    @Override
    public String toString() {
        return "Matrix2{" +
                "a11=" + a11 +
                ", a21=" + a21 +
                ", a12=" + a12 +
                ", a22=" + a22 +
                '}';
    }
}