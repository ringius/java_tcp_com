package se.edument.ringius.ge.com;

public interface ComReaderWriter {

    /** Send one data object **/
    boolean sendObject(Object data) throws ConnectionBrokenException;

    /** Read one data object and return it to calling method **/
    Object readObject() throws ConnectionBrokenException;

    void close();

    boolean isConnected();
}
