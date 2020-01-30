package se.edument.ringius.ge.com;

public interface DataConsumer {
    //todo: replace ComClient with uuid or smiilar.
    void onData(ComClient receiver, Object data);

    void disconnect(ComClient client);
}
