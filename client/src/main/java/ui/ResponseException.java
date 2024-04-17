package ui;

public class ResponseException extends Exception {
    final private int statusCode;
    public ResponseException (int status, String message) {
        super(message);
        this.statusCode = status;
    }
    public int StatusCode() {
        return statusCode;
    }
}