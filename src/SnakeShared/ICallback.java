package SnakeShared;

/**
 * Ein funktionales Interface als Callback-Interface
 */
public interface ICallback {
    /**
     * Aufzurufende Methode im Falle eines Callbacks
     *
     * @param msg Die zu übergebene Nachricht
     */
    void callback(String msg);
}
