/**
 * base class for ReaderWriter implementations. The class acts as base class for
 * implementation of dat transport classes, responsible for packing, sending, receiving and unpacking data that
 * is transferred through tcp/ip sockets.
 */

package se.edument.ringius.ge.com;

import java.io.IOException;
import java.net.Socket;

public abstract class SocketReaderWriter implements ComReaderWriter {

    private static final String TAG = SocketReaderWriter.class.getName();
    protected Socket socket;

    abstract public boolean sendObject(Object data) throws ConnectionBrokenException;
    abstract public Object readObject() throws ConnectionBrokenException;

    @Override
    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(TAG + ": Failed to close socket: " + e);
            }
        }
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isConnected() {
        if (socket != null) {
            return socket.isConnected();
        } else {
            return false;
        }
    }

}
