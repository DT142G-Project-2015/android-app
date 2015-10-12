package com.example.proxymeister.antonsskafferi.model;

import java.math.BigDecimal;
import java.util.List;

public class Item {
    public int id;
    public String name;
    public String description;
    public double price; // ändra inte datatyp här
    public int type;
    public List<Item> subitems;



    @Override
    public String toString() {
        return name + ", " + description;
    }


}
