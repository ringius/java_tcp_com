package se.edument.ringius.ge.com;

public class TransportDescriptor {
    private ComReaderWriter readerWriter;
    private ConnectionProvider connectionProvider;

    public TransportDescriptor(ComReaderWriter rw, ConnectionProvider listener) {
        readerWriter = rw;
        connectionProvider = listener;
    }

    public ComReaderWriter getComReaderWriter() {
        return readerWriter;
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }
}
