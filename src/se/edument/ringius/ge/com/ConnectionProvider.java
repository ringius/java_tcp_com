package se.edument.ringius.ge.com;

public interface ConnectionProvider {
    void startServer();
    void close();
    ComClient accept() throws ConnectionBrokenException;
    ComClient createUnconnectedClient();
}
