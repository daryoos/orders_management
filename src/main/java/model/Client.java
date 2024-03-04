package model;

import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    //private static final AtomicInteger count = new AtomicInteger(0);
    private int id;
    private String name;

    public Client() {
    }

    public Client(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public Client(String name) {
        //this.id = count.incrementAndGet();
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
