package com.example.proxymeister.antonsskafferi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Menu {


    private static boolean isLunchActive() {
        Calendar lunchStarts = Calendar.getInstance();
        lunchStarts.set(Calendar.HOUR_OF_DAY, 11);

        Calendar lunchEnds = Calendar.getInstance();
        lunchStarts.set(Calendar.HOUR_OF_DAY, 13);

        Calendar now = Calendar.getInstance();

        return lunchStarts.before(now) && now.before(lunchEnds);
    }

    private Group getGroupNamed(String name) {
        for (Group g : groups) if (name.equals(g.name))
            return g;
        return null;
    }

    public static Menu mergedMenuAtCurrentTime(List<Menu> menus) {
        Menu mergedMenu = new Menu();

        boolean lunch = isLunchActive();

        mergedMenu.type = lunch ? 0 : 1;
        mergedMenu.groups = new ArrayList<>();

        for (Menu m : menus) if (m.type == mergedMenu.type) {
            for (Group g : m.groups) {

                Group mergedGroup = mergedMenu.getGroupNamed(g.name);
                if (mergedGroup == null)
                    mergedMenu.groups.add(mergedGroup = new Group(g.name));

                mergedGroup.items.addAll(g.items);
            }
        }

        return mergedMenu;
    }

    public static class Item extends com.example.proxymeister.antonsskafferi.model.Item implements Serializable {
        //public List<SubItem> subItems;
    }

    public static class Group {
        public int id;
        public String name;
        public List<Item> items = new ArrayList<>();

        public Group(String name) {
            this.name = name;
        }
    }

    public int id;
    public int type;
    public Date start_date;
    public Date stop_date;
    public List<Group> groups;
}