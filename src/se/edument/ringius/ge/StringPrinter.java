package se.edument.ringius.ge;

import se.edument.ringius.ge.com.ComClient;
import se.edument.ringius.ge.com.DataConsumer;

public class StringPrinter implements DataConsumer {
    @Override
    public void onData(ComClient receiver, Object data) {
        if (data instanceof String) {
            String s = (String) data;
            System.out.println(s);
        }
    }

    @Override
    public void disconnect(ComClient client) {
        System.out.println("disconnected");
    }
}
