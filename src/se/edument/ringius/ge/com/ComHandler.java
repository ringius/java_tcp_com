package se.edument.ringius.ge.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//todo: improve synchronization
public class ComHandler implements DataConsumer {
    private ComServer comServer;
    HashMap<String, ArrayList<ComClient> > subscriptions;
    ArrayList<ComClient> connections;

    public ComHandler(ComServer comServer) {
        this.comServer = comServer;
        comServer.registerHandler(this);
        subscriptions = new HashMap<>();
        connections = new ArrayList<>();
    }

    public void start() {
        comServer.start();
    }

    //todo: synchronize
    public void broadcast(Object object) {
        synchronized (subscriptions) {
            List<ComClient> subscribers = subscriptions.get(object.getClass().getName());
            if (subscribers != null) {
                for (ComClient c : subscribers) {
                    c.sendObject(object);
                }
            }
        }
    }

    public void registerClient(ComClient comClient) {
        synchronized (connections) {
            connections.add(comClient);
        }
    }

    private void subscribe(ComClient client, SubscribeRequest request) {
        synchronized(subscriptions) {
            if (request.subscribeFlag) { //add subscription
                if (!subscriptions.containsKey(request.className)) {
                    ArrayList<ComClient> subs = new ArrayList<>();
                    subs.add(client);
                    subscriptions.put(request.className, subs);
                } else {
                    ArrayList<ComClient> s = subscriptions.get(request.className);
                    if (!s.contains(client)) { //to avoid duplicates being sent if client subsribes several times.
                        s.add(client);
                        subscriptions.put(client.getClass().getName(), s);
                    }
                }
            } else {
                if (subscriptions.containsKey(request.className)) {
                    ArrayList<ComClient> s = subscriptions.get(request.className);
                    s.remove(client);
                    subscriptions.put(client.getClass().getName(), s);
                }
            }
        }
    }

    public void onData(ComClient receiver, Object data) {
        //todo: separate onData from broadcast by using receive buffer and handle it in a separate thread.
        String className = data.getClass().getName();

        //special handling of subscribe messages
        if (data instanceof SubscribeRequest) {
            subscribe(receiver, (SubscribeRequest)data);
        } else if(data instanceof CancelSubscriptionsRequest) {
            unsubscribe(receiver);
        } else {
            if (subscriptions.containsKey(className)) {
                broadcast(data);
            }
        }

        /* Example of handler - should be implemented in another class I think...

        switch(className) {
            case "DataObject":
                DataObject o = (DataObject) data;
                o.increaseIntValue();
                broadcast(o);
                break;
            case "SubscribeRequest":
                SubscribeRequest s = (SubscribeRequest) data;
                subscribe(receiver, s);
                break;
            default:
                System.out.println("Unknown data-object received:" + data);
                break;

        }
        */
    }

    @Override
    public void disconnect(ComClient client) {
        unsubscribe(client);
        comServer.disconnectClient(client);
        synchronized(connections) {
            connections.remove(client);
        }
    }

    private void unsubscribeFromAll() {
        synchronized(connections) {
            for (ComClient c : connections) {
                unsubscribe(c);
            }
        }
    }

    private void unsubscribe(ComClient client) {
        for(String topic : subscriptions.keySet()) {
            subscriptions.get(topic).remove(client);
        }
    }

    public void stop() {
        unsubscribeFromAll();
        comServer.stop();
    }

    public void disconnectAll() {
        for (ComClient client : connections) {
            client.close();
        }
        connections.clear();
    }
}