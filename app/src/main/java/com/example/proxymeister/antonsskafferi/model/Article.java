package com.example.proxymeister.antonsskafferi.model;

public class Article {
    public int id;
    public String name;
    public String category;
    // public String category; unlikely used
    public double amount;
    public String unit;
    public String exp_date;     // public String days_to_exp ( depending on solution )

    //Constructor
    public Article(int startid, String startname, String startcategory, double startamount, String startunit, String startexp_date) {
        id = startid;
        name = startname;
        category = startcategory;
        amount = startamount;
        unit = startunit;
        exp_date = startexp_date;
    }

    @Override
    public String toString()
    {
        return name + " / " + Double.toString(amount) + " " + unit + " / " +  exp_date;
    }
}