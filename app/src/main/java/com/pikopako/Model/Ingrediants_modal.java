package com.pikopako.Model;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

public class Ingrediants_modal implements Serializable {

    //incretions
    String product_quantity="1",productPrice="0",productName="",id="";

   public String ingredients_name="";
   public String ingredients_price="";
   public String ingredients_id="";
   public String discount="";
   public boolean isIngredient_add;

   public ArrayList<Ingrediants_modal> ingrediants_modalArrayList = new ArrayList<>();

    public ArrayList<Ingrediants_modal> parseIngrediants(JSONArray json_ingrediants_array){

        ingrediants_modalArrayList.clear();
        for (int i =0; i< json_ingrediants_array.length();i++){
           Ingrediants_modal ingrediants_modal = new Ingrediants_modal();
            try {

                if (json_ingrediants_array.getJSONObject(i).has("topping_name"))
                   ingrediants_modal.ingredients_name = json_ingrediants_array.getJSONObject(i).getString("topping_name");
                else
                   ingrediants_modal.ingredients_name = json_ingrediants_array.getJSONObject(i).getString("name");

                if (json_ingrediants_array.getJSONObject(i).has("topping_price"))
                   ingrediants_modal.ingredients_price = json_ingrediants_array.getJSONObject(i).getString("topping_price");
                else
                    ingrediants_modal.ingredients_price = json_ingrediants_array.getJSONObject(i).getString("price");

                if (json_ingrediants_array.getJSONObject(i).has("id"))
                    ingrediants_modal.ingredients_id = json_ingrediants_array.getJSONObject(i).getString("id");
                else
                    ingrediants_modal.ingredients_id = json_ingrediants_array.getJSONObject(i).getString("id");

                ingrediants_modal.isIngredient_add=false;

                ingrediants_modalArrayList.add(ingrediants_modal);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return ingrediants_modalArrayList;
    }

    public String getPRoductQuantity() {
        if (product_quantity==null||product_quantity=="")
            product_quantity="1";
        return product_quantity;
    }
    public void setProductQuantity(String productQuantity) {
        this.product_quantity = productQuantity;
    }
    public String getProductPrice() {
        if (productPrice==null||productPrice=="")
            productPrice="0";
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }


    public String getIngredientPrice() {
        if (ingredients_price==null || ingredients_price == "")
            ingredients_price="0";
        return ingredients_price;
    }

    public void setIngredientPrice(String ingredients_price) {
        this.ingredients_price = ingredients_price;
    }

    public void setDiscount(String discount){
       this.discount=discount;
    }

    public String getDiscount(){
       return discount;
    }

    public String getToppingname(){
       return ingredients_name;
    }

    public void setToppingName(String toppingName) {
        this.ingredients_name = toppingName;
    }
    public void setFoodName(String productName) {
        this.productName = productName;
    }
    public String getfoodName() {
        return productName;
    }

    public void setingreid(String id) {
        this.ingredients_id = id;
    }
    public String getingreid() {
        return ingredients_id;
    }



    public void setfoodid(String id) {
        this.id = id;
    }
    public String getFoodid() {
        return id;
    }


    ///ind arr
    public void setIngrediant(){
        ingrediants_modalArrayList = new Ingrediants_modal().parseIngrediants(null);
    }
//    public boolean isSelected(){
//        return isSelected();
//    }
//    public void setSelected(boolean isSelected) {
//        this.isIngredient_add = isSelected;
//    }
}
