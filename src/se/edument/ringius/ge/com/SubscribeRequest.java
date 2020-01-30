package se.edument.ringius.ge.com;

import java.io.Serializable;

public class SubscribeRequest implements Serializable {
    String className;
    boolean subscribeFlag;

    public SubscribeRequest() {
    }

    SubscribeRequest(Class type, boolean sub) {
        className = type.getName();
        subscribeFlag = sub;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean getSubscribeFlag() {
        return subscribeFlag;
    }

    public void setSubscribeFlag(boolean subscribeFlag) {
        this.subscribeFlag = subscribeFlag;
    }
}
