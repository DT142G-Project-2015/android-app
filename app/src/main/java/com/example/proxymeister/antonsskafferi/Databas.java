package com.example.proxymeister.antonsskafferi;


import java.util.ArrayList;
import java.util.List;

public class Databas {
    private static Databas instance;

    public static Databas getInstance() {
        if (instance == null)
            instance = new Databas();

        return instance;
    }
    public static class Order {
        String text;
    }

    public List<Order> orders = new ArrayList<Order>();

    public String text;
}
