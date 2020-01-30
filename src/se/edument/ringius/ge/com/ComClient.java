package se.edument.ringius.ge.com;

public class ComClient implements Runnable {

    private static int clientCount = 0;
    private static final String TAG = ComClient.class.getName();

    private String name;
    private ComReaderWriter readerWriter;
    private DataConsumer consumer;

    private Thread readThread;

    ComClient(String name) {
        this.name = name + "-" + clientCount++;
    }

    public void finalize() {
        if (readerWriter != null) {
            readerWriter.close();
        }
    }

    void setComReaderWriter(ComReaderWriter rw) {
        this.readerWriter = rw;
    }

    public String getName() {
        return name;
    }

    public ComClient setName(String name) {
        this.name = name;
        return this;
    }

    public ComClient registerConsumer(DataConsumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public ComClient subscribe(Class topic) {
        sendObject(new SubscribeRequest(topic, true));
        return this;
    }

    public ComClient unsubscribe(Class topic) {
        sendObject(new SubscribeRequest(topic, false));
        return this;
    }

    public ComClient unsubscribeFromAll() {
        sendObject(new CancelSubscriptionsRequest());
        return this;
    }

    public boolean sendObject(Object object) {
        try {
            return readerWriter.sendObject(object);
        } catch (ConnectionBrokenException e) {
            readerWriter.close();
            return false;
        }
    }

    public ComClient start() {
        if (readerWriter != null && readerWriter.isConnected()) {
            readThread = new Thread(this, name);
            readThread.start();
            return this;
        } else {
            System.out.println(TAG + "Client is not connected. Receive thread not started");
            return this;
        }
    }

    @Override
    public void run() {
        System.out.println(name + ". Start receive thread");
        try {
            while (true) {
                Object object = readerWriter.readObject();
                if (object != null) {
                    if (consumer != null) {
                        consumer.onData(this, object);
                    }
                } else {
                    System.out.println(name + ". Serialization error");
                }
            }
        } catch (ConnectionBrokenException e) {
            System.out.println("Remote terminal closed");
            if (consumer != null) {
                consumer.disconnect(this);
            } else {
                readerWriter.close();
            }
        }
    }

    public void close() {
        if (readerWriter != null) {
            readerWriter.close();
        }
    }
}
