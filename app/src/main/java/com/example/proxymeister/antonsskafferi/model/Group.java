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

    public String toStringKitchenFormat() {
        String temp = new String();

        int counter = 1;
        //Sorts the items in terms of their names with the help of a function object
        Collections.sort(items, new CustomComparator());
        //Iterates the orders and prints the item with it's frequency
        for(int i = 0; i < items.size(); i++)
        {
            if(i != items.size()-1){
                if(items.get(i).id == items.get(i + 1).id){
                    counter++;
                }
                else{
                    if(counter == 1) {
                        temp += "   " + items.get(i).toStringKitchenFormat() + "\n";
                    }
                    else{
                        temp += counter + " " + items.get(i).toStringKitchenFormat() + "\n";
                        counter = 1;
                    }
                }
            }
            else{
                if(counter == 1) {
                    temp += "   " + items.get(i).toStringKitchenFormat() + "\n";
                }
                else{
                    temp += counter + " " + items.get(i).toStringKitchenFormat() + "\n";
                }
            }
            //temp += items.get(i).toStringKitchenFormat() + "\n";
        }
        /*

        for(Item i : items) {
            temp += i.toStringKitchenFormat() + "\n";
        }
*/
        return "Group No." + id + "\n" + temp;
    }
}
