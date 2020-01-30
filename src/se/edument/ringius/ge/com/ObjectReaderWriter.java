/**
 * Class responsible for packing/unpacking data objects and sending them to another application using tcp/ip
 */

package se.edument.ringius.ge.com;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ObjectReaderWriter extends SocketReaderWriter {
    private static final String TAG = ObjectReaderWriter.class.getName();
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ObjectReaderWriter() {
    }

    //todo: handle exception
    @Override
    public void setSocket(Socket socket) {
        super.setSocket(socket);
        init();
    }

    private void init() {
        if (socket == null) {
            System.out.println(TAG + ": socket == null. No initialization performed");
            return;
        }
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean sendObject(Object object) throws ConnectionBrokenException {
        if (out != null) {
            try {
                out.writeUnshared(object);
                out.flush();
                return true;
            } catch (EOFException e) {
                System.out.println("Connection broken: " + e);
                throw new ConnectionBrokenException(e.getMessage());
            } catch (SocketException e) {
                throw new ConnectionBrokenException("Socket already closed " + e.getMessage() + ")");
            } catch (IOException e) {
                throw new ConnectionBrokenException("IO Error (" + e.getMessage() + ")");
            }
        }
        return false;
    }


    @Override
    public Object readObject() throws ConnectionBrokenException {
        if (in != null) {
            try {
                return in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new ConnectionBrokenException(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void close() {
        super.close();
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(TAG + ": Failed to close in/out stream");
        }
    }
}
