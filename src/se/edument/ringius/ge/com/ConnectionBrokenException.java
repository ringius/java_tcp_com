package se.edument.ringius.ge.com;

public class ConnectionBrokenException extends Exception {
    ConnectionBrokenException(String msg) {
        super(msg);
    }

    ConnectionBrokenException(Exception e) {
        super(e);
    }
}
