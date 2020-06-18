package com.pikopako.Model;

import java.util.ArrayList;

/**
 * Created by mukeshs on 11/5/18.
 */

public class ProductModel {
    String id,userName,productName,productPrice,userImage,product_quantity="1";

    ArrayList<Ingrediants_modal> ingrediants_modalArrayList= new ArrayList<>();



    public void setId(String id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductPrice() {
        if (productPrice==null||productPrice=="")
            productPrice="1";
        return productPrice;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserName(String name) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPRoductQuantity() {
        if (product_quantity==null||product_quantity=="")
            product_quantity="1";
        return product_quantity;
    }
    public void setProductQuantity(String productQuantity) {
        this.product_quantity = productQuantity;
    }

     ///ind arr
    public void setIngrediant(){
    ingrediants_modalArrayList = new Ingrediants_modal().parseIngrediants(null);
    }


}
