package com.example.proxymeister.antonsskafferi.model;

import java.util.List;


public class Order {
    public List<Group> groups;
    public int id;
    public int booth;
    public boolean payed;

    public double getTotalPrice(){
        double totPrice = 0.00;

        for(Group g : groups) {
            for(Item i : g.items) {
                totPrice += i.price;
                for (Item si : i.subItems)
                    totPrice += si.price;
            }
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
