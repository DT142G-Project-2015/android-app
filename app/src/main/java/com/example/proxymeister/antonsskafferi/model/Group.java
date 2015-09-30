package com.example.proxymeister.antonsskafferi.model;

import java.util.List;

/**
 * Created by Viktor on 2015-09-29.
 */
public class Group {
    private List<Item> items;
    private String status = "";

    @Override
    public String toString() {
        String temp = new String();
                for(Item i : items) {
                    temp += items.toString() + ", ";
                }
        return status + temp;
    }
}
