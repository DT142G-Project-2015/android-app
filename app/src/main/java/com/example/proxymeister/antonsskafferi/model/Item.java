package com.example.proxymeister.antonsskafferi.model;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable, Cloneable {
    public int id;
    public String name;
    public String description;
    public double price; // ändra inte datatyp här
    public int type;
    public List<Note> notes;
    public List<Item> subItems;

    @Override
    public Object clone() {
        Item clone = null;
        try {
            clone = (Item)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    @Override
    public String toString() {
        return name + ", " + description;
    }


}
