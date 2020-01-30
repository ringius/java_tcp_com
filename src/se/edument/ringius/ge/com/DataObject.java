package se.edument.ringius.ge.com;

// import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.*;

public class DataObject implements Serializable {
    private int intValue;

    private long timeStamp;

    private String stringValue;
    private static String pelle = "olle";

    public DataObject() {
    }

    public DataObject(int intValue) {
        this.intValue = intValue;
        this.stringValue = new String("pelle");
    }

    public DataObject(String stringValue) {
        this.stringValue = stringValue;
    }

    public String toString() {
        return stringValue;
    }

    public void increaseIntValue() {
        this.intValue++;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public void setStringValue(String val) {
        stringValue = val;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setTimeStamp(long ts) {
        this.timeStamp = ts;
    }

    public DataObject setTimeStampToNow() {
        setTimeStamp(System.currentTimeMillis());
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
