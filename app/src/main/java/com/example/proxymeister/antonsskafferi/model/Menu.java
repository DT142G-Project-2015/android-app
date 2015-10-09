package com.example.proxymeister.antonsskafferi.model;

import java.util.Date;
import java.util.List;

public class Menu {

    public static class Item extends com.example.proxymeister.antonsskafferi.model.Item {
        //public List<SubItem> subItems;
    }

    public static class Group {
        public int id;
        public String name;
        public List<Item> items;
    }

    public int id;
    public String name;
    public Date start_date;
    public Date stop_date;
    public List<Group> groups;
}