package com.pikopako.Model;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductListModel implements Serializable {
    public String cafe_id="";
    public String Pic_url = "";
    public String cafe_name = "";
    public String cafe_status = "";
    public String cafe_sublist = "";
    public String cafe_rating = "";
    public String cafe_deleiverytime = "";
    public String cafe_minorder = "";
    public String cafe_address="";
    public String coordinate_id="";
    public String total_reviews="";
    public ArrayList<ProductListModel> arraylist;

    ProductListModel productListModel;


    public void initialize(JSONObject jsonObject) {
        arraylist = new ArrayList<>();



        try {

            JSONArray dataArray = jsonObject.getJSONArray("data");
            JSONObject dataObject;

            for (int i = 0; i < dataArray.length(); i++) {


                dataObject = dataArray.getJSONObject(i);


             //  address = dataObject.getString("city") + " " + dataObject.getString("postal_code") + " " + dataObject.getString("street_address") + " " + dataObject.getString("house_number");
              //  name = dataObject.getString("first_name") + " " + dataObject.getString("last_name");

                productListModel = new ProductListModel();
                productListModel.cafe_id=dataObject.getString("restaurant_id");
                productListModel.Pic_url = dataObject.getString("restaurant_logo");
                productListModel.cafe_name = dataObject.getString("restaurant_name");
                productListModel.cafe_status=dataObject.getString("restaurant_status");
                productListModel.cafe_rating = dataObject.getString("avg_rating");
                productListModel.total_reviews = dataObject.getString("total_reviews");
                productListModel.cafe_minorder = dataObject.getString("minimum_order_amount");
                productListModel.cafe_sublist = dataObject.getString("restaurant_categories");
                productListModel.cafe_deleiverytime = dataObject.getString("maximum_time_delivery");
                productListModel.cafe_address=dataObject.getString("address");
                productListModel.coordinate_id=dataObject.getString("coordinate_id");
//                JSONArray genreArray = dataObject.getJSONArray("genre_name");
//                for (int k = 0; k < genreArray.length(); k++) {
//                    JSONObject genreObject = genreArray.getJSONObject(k);
//                    genre = genre + genreObject.getString("genres_name") + ",";
//                }




                arraylist.add(productListModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}