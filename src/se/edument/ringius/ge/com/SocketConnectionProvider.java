package se.edument.ringius.ge.com;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketConnectionProvider implements ConnectionProvider {
    private static final String TAG = SocketConnectionProvider.class.getName();
    private String hostName;
    private int port;
    private ServerSocket serverSocket;
    SocketReaderWriter readerWriter;

    public SocketConnectionProvider(SocketReaderWriter rw, int port) {
        this.port = port;
        this.readerWriter = rw;
    }

    public SocketConnectionProvider(SocketReaderWriter rw, String hostName, int port) {
        this(rw, port);
        this.hostName = hostName;
    }

    @Override
    public void startServer() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
            serverSocket = new ServerSocket(port, 10, addr);
        } catch (IOException e) {
            System.out.println(TAG + ": Failed to open server socket: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public ComClient accept() throws ConnectionBrokenException {
        try {
            Socket connection = serverSocket.accept();
            ComClient endPoint = createClient(connection);
            return endPoint;
        } catch (IOException err) {
            throw new ConnectionBrokenException(err);
        }
    }

    @Override
    public void close() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return; //ugly as hell, should be fixed...
        }
    }

    public ComClient createClient(Socket connection) {
        ComClient client = new ComClient("server");
        try {
            SocketReaderWriter rw = readerWriter.getClass().newInstance();
            rw.setSocket(connection);
            client.setComReaderWriter(rw);
            return client;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ComClient createUnconnectedClient(String host) {
        this.hostName = host;
        try {
            ComClient client = new ComClient("client");
            Socket socket = new Socket(host, port);
            SocketReaderWriter rw = readerWriter.getClass().newInstance();
            rw.setSocket(socket);
            client.setComReaderWriter(rw);
            return client;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println(TAG +
                    ": Failed to create ComReaderWriter from class " +
                    readerWriter.getClass().getName());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(TAG + ": Failed to connect socket to " + host + ":" + port);
            return null;
        }
    }

    public ComClient createUnconnectedClient() {
        return createUnconnectedClient(hostName);
    }
}
