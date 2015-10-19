package com.example.proxymeister.antonsskafferi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Menu {


    public boolean isMenuActive() {
        Calendar start = Calendar.getInstance();
        start.setTime(start_date);
        start.set(Calendar.HOUR_OF_DAY, 0);

        Calendar stop = Calendar.getInstance();
        stop.setTime(stop_date);
        stop.add(Calendar.DAY_OF_YEAR, 1);

        Calendar now = Calendar.getInstance();

        return start.before(now) && now.before(stop);
    }

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

    public String getMenuTypeString() {
        return type == 0 ? "Lunch"
                : type == 1 ? "A'la carte"
                : "Statisk";
    }

    public static Menu mergedMenuAtCurrentTime(List<Menu> menus) {
        Menu mergedMenu = new Menu();

        boolean lunch = isLunchActive();

        mergedMenu.type = lunch ? 0 : 1;
        mergedMenu.groups = new ArrayList<>();

        for (Menu m : menus) if (m.isMenuActive() && (m.type == mergedMenu.type || m.type == 2)) {
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