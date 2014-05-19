package lv.abuzdin.systemprogramming.client.presentation;

public class Message {

    private boolean client;
    private String value;

    public Message(boolean client, String value) {
        this.client = client;
        this.value = value;
    }

    public boolean isClient() {
        return client;
    }

    public void setClient(boolean client) {
        this.client = client;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
