package com.pikopako.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class FilterModel implements Serializable {

    public String category_name;

    public boolean isChecked;
    public int id = -1;
    public ArrayList<FilterModel> arrayList = null;
    FilterModel filterModel;


    public void initializeModel(JSONObject responseObject){
        arrayList = new ArrayList<>();

        try {

            JSONArray data = responseObject.getJSONArray("data");

            for (int i=0;i<data.length();i++){
                JSONObject dataObject = data.getJSONObject(i);
                filterModel =new FilterModel();
                filterModel.category_name= dataObject.getString("restaurant_category_name");

                filterModel.id = dataObject.getInt("id");
                filterModel.isChecked=false;
                arrayList.add(filterModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
