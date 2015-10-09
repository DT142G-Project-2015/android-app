package com.example.proxymeister.antonsskafferi.model;

public class Article {
    public int id;
    public String name;
    // public String category; unlikely used
    public double amount;
    public String unit;
    public String exp_date;     // public String days_to_exp ( depending on solution )

    @Override
    public String toString()
    {
        return name + " | " + Double.toString(amount) + " " + unit + " | " +  exp_date;
    }
}