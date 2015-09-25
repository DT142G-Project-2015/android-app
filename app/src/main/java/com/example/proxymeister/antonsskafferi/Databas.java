package com.example.proxymeister.antonsskafferi;


public class Databas {
    private static Databas instance;

    public static Databas getInstance() {
        if (instance == null)
            instance = new Databas();

        return instance;
    }

    public String text;
}
