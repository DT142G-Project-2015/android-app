package com.example.proxymeister.antonsskafferi.model;

public class Article {
    public int article_id;
    public String article_name;
    public double amount;
    public String unit;
    public String exp_date;     // public String days_to_exp ( depending on solution )
    public int category_id;


    public Article()
    {
        article_id = 0;
        article_name = "";
        category_id = 0;
        amount = 0;
        unit = "";
        exp_date = "";
    }

    //Constructor
    public Article(String startname, double startamount, String startunit, String startexp_date, int startcategory) {
        article_name = startname;
        amount = startamount;
        unit = startunit;
        exp_date = startexp_date;
        category_id = startcategory;
    }

    @Override
    public String toString()
    {
        return ( article_name + " | " + Double.toString(amount) + " " + unit + " | " +  exp_date);
    }

    public boolean isVoid()
    {
        return ( article_id == 0 && article_name.equals("") && category_id == 0
                && amount == 0 && unit.equals("") && exp_date.equals("") );
    }
}