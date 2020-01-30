package se.edument.ringius.ge.com;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class JsonReaderWriter extends SocketReaderWriter  {
    private static final String TAG = JsonReaderWriter.class.getName();
    private ObjectMapper objectMapper;

    private OutputStream out;
    private InputStream in;

    //todo: handle exception
    @Override
    public void setSocket(Socket socket) {
        super.setSocket(socket);
        init();
    }

    private void init() {
        objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        if (socket == null) {
            System.out.println(TAG + ": socket == null. No initialization performed");
            return;
        }
        try {
            out = new BufferedOutputStream(socket.getOutputStream());
            in = socket.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean sendObject(Object object) throws ConnectionBrokenException {
        if (out != null && socket != null) {
            try {
                String className = object.getClass().getName();
                Map<String, Object> map = objectMapper.convertValue(object, Map.class);
                Map<String, Object> newMap = new HashMap<>();
                newMap.put(className, map);
                String json = objectMapper.writeValueAsString(newMap);
                out.write(json.getBytes());
                out.flush();
                return true;
            } catch (EOFException e) {
                System.out.println("Controlled: " + e);
                throw new ConnectionBrokenException(e.getMessage());
            } catch (SocketException e) {
                throw new ConnectionBrokenException("Socket already closed " + e.getMessage() + ")");
            } catch (IOException e) {
                throw new ConnectionBrokenException("IO Error (" + e.getMessage() + ")");
            } catch (IllegalArgumentException e) {
                System.out.println("Serialization failed for " + object.getClass().getName());
            }
        }
        return false;
    }

    @Override
    public Object readObject() throws ConnectionBrokenException {
        byte data[] = new byte[8192];
        if (in != null) {
            try {
                in.read(data);
                String json = new String(data);

                Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
                });

                for (String o : map.keySet()) {
                    Class c = Class.forName(o);
                    Object dto = map.get(o);
                    Object dataObject = objectMapper.convertValue(dto, c);
                    return dataObject;
                }
            } catch (IOException e) {
                throw new ConnectionBrokenException(e.getMessage());
            } catch (ClassNotFoundException | IllegalArgumentException e) {
                System.out.println("json de-serialization failed: " + e);
            }
        }
        return null;
    }
}
