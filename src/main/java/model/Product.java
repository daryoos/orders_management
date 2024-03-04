package model;

import java.util.concurrent.atomic.AtomicInteger;

public class Product {
    private int id;
    private String name;
    private int stoc;

    public Product() {

    }
    public Product(String name, int stoc) {
        this.name = name;
        this.stoc = stoc;
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

    public int getStoc() {
        return stoc;
    }

    public void setStoc(int stoc) {
        this.stoc = stoc;
    }
}
