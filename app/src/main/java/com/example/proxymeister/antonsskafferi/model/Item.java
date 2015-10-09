package com.example.proxymeister.antonsskafferi.model;

import java.math.BigDecimal;

public class Item {
    public int id;
    public String name;
    public String description;
    public double price; // ändra inte datatyp här
    public int type;



    @Override
    public String toString() {
        return name + ", " + description;
    }


    public String toStringKitchenFormat() {
        return name;
    }
}
