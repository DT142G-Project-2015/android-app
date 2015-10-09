package com.example.proxymeister.antonsskafferi.model;

import java.math.BigDecimal;

public class Item {
    public int id;
    public String name;
    public String description;
    public BigDecimal price;
    public int type;


    @Override
    public String toString() {
        return name + ", " + description + ", " + price.toString() + " kr";
    }


    public String toStringKitchenFormat() {
        return name;
    }
}
