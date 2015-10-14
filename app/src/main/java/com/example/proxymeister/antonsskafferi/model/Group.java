package com.example.proxymeister.antonsskafferi.model;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;


public class Group {

    public enum Status {
        @SerializedName("initial") Initial,
        @SerializedName("readyForKitchen") ReadyForKitchen,
        @SerializedName("readyToServe") ReadyToServe,
        @SerializedName("done") Done;

        private String getText() {
            try {
                return getClass().getField(name()).getAnnotation(SerializedName.class).value();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);  // IMPOSSIBLE
            }
        }

        public static String getText(Status status) {
            return (status == null ? Initial : status).getText();
        }

        public static Status fromText(String text) {
            for (Status st : values()) {
                if (st.getText().equals(text))
                    return st;
            }
            return Initial;
        }

        public static String sanitize(String s) {
            return fromText(s).getText();
        }
    }

    public List<Item> items;

    public String getId() {
        return Integer.toString(id);
    }

    @SerializedName("status")
    public Status status;
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
