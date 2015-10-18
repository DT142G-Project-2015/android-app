package com.example.proxymeister.antonsskafferi.model;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {
    public int id;
    public String name;
    public String description;
    public double price; // ändra inte datatyp här
    public int type;
    public List<Note> notes;
    public List<Item> subItems;



    @Override
    public String toString() {
        return name + ", " + description;
    }


}
