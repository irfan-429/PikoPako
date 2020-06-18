package com.pikopako.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListData {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cricket = new ArrayList<String>();
        cricket.add("Vegetable Burger");
        cricket.add("Cheese Burger");
        cricket.add("Cheese Burger");
        cricket.add("Cheese Burger");
        cricket.add("Cheese Burger");

        List<String> football = new ArrayList<String>();
        football.add("Cheese Burger");
        football.add("Cheese Burger");
        football.add("Cheese Burger");
        football.add("Cheese Burger");
        football.add("Cheese Burger");

        List<String> basketball = new ArrayList<String>();
        basketball.add("Cheese Burger ");
        basketball.add("Cheese Burger");
        basketball.add("Cheese Burger");
        basketball.add("Cheese Burger");
        basketball.add("Cheese Burger");

        expandableListDetail.put("BURGER", cricket);
        expandableListDetail.put("SANDWICH ", football);
        expandableListDetail.put("PANNE PASTA", basketball);
        return expandableListDetail;
    }
}