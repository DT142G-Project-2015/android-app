package com.example.proxymeister.antonsskafferi.model;


import java.math.BigDecimal;
import java.security.acl.Group;
import java.util.List;

public class Order {
    private List<Group> groups;
   // public String status;
    public BigDecimal id;

    @Override
    public String toString() {
        String temp = new String();
        for(Group g : groups) {
            temp += groups.toString() + ", ";
        }
        return  id.toString() + ", " + temp;
    }
}
