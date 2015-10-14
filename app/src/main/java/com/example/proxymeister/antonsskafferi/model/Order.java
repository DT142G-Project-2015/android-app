package com.example.proxymeister.antonsskafferi.model;

import java.math.BigDecimal;
import java.util.List;


public class Order {
    public List<Group> groups;
    public int id;
    public int booth;
    public double totPrice = 0.00;

    public double getTotalPrice(){
        for(Group g : groups) {
            for(Item i : g.items)
                totPrice += i.price;
        }
        return totPrice;
    }

    public boolean allDone(){
        boolean done = true;
        for(Group g : groups){
            if(!g.status.equals("done"))
                done = false;
        }
        return done;
    }

    @Override
    public String toString() {
        String temp = new String();
        for(Group g : groups) {
            temp += g.toString() + ", ";
        }
        return  id + ", " + temp;
    }
    public final int getBooth() { return booth; }
    public final int getId() { return id; }
}
