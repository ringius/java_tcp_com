package se.edument.ringius.ge.com;

import java.util.HashSet;


public class ComServer implements Runnable {
    private ComHandler handler = null;
    private Thread serverThread;
    private boolean running;
    private HashSet<ComClient> clients;
    private ConnectionProvider connectionProvider = null;

    public ComServer() {
        clients = new HashSet<>();
    }

    public void setConnectionProvider(ConnectionProvider provider) {
        this.connectionProvider = provider;
    }

    public void registerHandler(ComHandler handler) {
        this.handler = handler;
    }

    public void start() {
        if (connectionProvider == null || handler == null) {
            //throw exception...
        }
        serverThread = new Thread(this, "socketServerThread");
        serverThread.start();
    }

    private void disconnectAllClients() {
        synchronized (clients) {
            for (ComClient c : clients) {
                disconnectClient(c);
            }
            clients.clear();
        }
    }

    public void disconnectClient(ComClient client) {
        synchronized (clients) {
            if (clients.contains(client)) {
                client.close();
            }
        }
    }

    @Override
    public void run() {
        running = true;
        connectionProvider.startServer();
        while (running) {
            try {
                ComClient client = connectionProvider.accept();
                client.registerConsumer(handler);
                handler.registerClient(client);
                client.start();
                synchronized (clients) {
                    clients.add(client);
                }
            } catch (ConnectionBrokenException err) {
                System.out.println("ComServer: Exception caught: " + err);
                disconnectAllClients();
                connectionProvider.close();
                running = false;
            }
        }
        System.out.println("serverThread terminates");
    }

    public void stop() {
        running = false;
        connectionProvider.close();
    }

    public void close() {
        for (ComClient client : clients) {
            client.close();
        }
        handler.disconnectAll();
        connectionProvider.close();
    }
}
