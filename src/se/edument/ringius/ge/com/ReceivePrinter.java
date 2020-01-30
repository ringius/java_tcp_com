package se.edument.ringius.ge.com;

import se.edument.ringius.ge.com.ComClient;
import se.edument.ringius.ge.com.DataConsumer;
import se.edument.ringius.ge.com.DataObject;

public class ReceivePrinter implements DataConsumer {
    @Override
    public void onData(ComClient receiver, Object data) {
        if (data instanceof DataObject) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - ((DataObject)data).getTimeStamp();
            System.out.println("transmission time = " + elapsed);
        }
        System.out.println(receiver.getName() + ": " + data);
    }

    @Override
    public void disconnect(ComClient client) {
        client.close();
    }
}
