package com.example.proxymeister.antonsskafferi.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Item {
    public int id;
    public String name;
    public String description;
    public double price; // ändra inte datatyp här
    public String type;
    public List<Note> notes;
    public List<Item> subItems;



    @Override
    public String toString() {
        return name + ", " + description;
    }


}
