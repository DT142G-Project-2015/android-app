package com.example.proxymeister.antonsskafferi.model;

import java.util.List;


public class Order {
    public List<Group> groups;
    public int id;

    @Override
    public String toString() {
        String temp = new String();
        for(Group g : groups) {
            temp += g.toString() + ", ";
        }
        return  id + ", " + temp;
    }


    public String toStringKitchenFormat() {
        String temp = new String();
        for(Group g : groups) {
            temp += g.toStringKitchenFormat() + " ";
        }
        return  id + ": " + "\n" + temp;
    }
}
