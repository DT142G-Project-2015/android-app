package com.example.proxymeister.antonsskafferi.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Group {
    public List<Item> items;

    public String getStatus() {
        return status;
    }

    public String getId() {
        return Integer.toString(id);
    }

    public String status;
    public int id;
    public int tablenum;

    public int orderId;

    class CustomComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.name.compareTo(o2.name);
        }
    }

    @Override
    public String toString() {
        String temp = new String();
        for(Item i : items) {
            temp += items.toString() + ", ";
        }
        return status + temp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Group))
            return false;
        Group other = (Group)o;

        return id == other.id;
    }
}
