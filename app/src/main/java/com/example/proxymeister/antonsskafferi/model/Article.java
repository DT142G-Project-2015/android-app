package com.example.proxymeister.antonsskafferi.model;

/**
 * Created by curious on 2015-10-08.
 */
public class Article {
    public int id;
    public String name;
    // public String category; unlikely used
    public int amount;
    public String unit;
    public String exp_date;     // public String days_to_exp ( depending on solution )

    @Override
    public String toString()
    {
        return name + " | " + Integer.toString(amount) + " " + unit + " | " +  exp_date;
    }
}

