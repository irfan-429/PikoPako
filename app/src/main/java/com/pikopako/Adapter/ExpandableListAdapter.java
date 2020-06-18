package com.pikopako.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.Fragment.RestroDetailItemFragment;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;

    private HashMap<String, List<String>> expandableListDetail;
    JSONArray jsonArray = new JSONArray();
    RestroDetailItemFragment clickItemEvent;
//
//    public ExpandableListAdapter(Context context, List<String> expandableListTitle,
//                                       HashMap<String, List<String>> expandableListDetail) {
//        this.context = context;
//        this.expandableListTitle = expandableListTitle;
//        this.expandableListDetail = expandableListDetail;
//    }

    public ExpandableListAdapter(Context mContext, JSONArray categoriesArray) {
        this.mContext=mContext;
        this.clickItemEvent=clickItemEvent;
        this.jsonArray=categoriesArray;

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);

    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);

    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        try {
            return jsonArray.getJSONObject(listPosition).getJSONArray("food_items").getJSONObject(expandedListPosition);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
       // final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        JSONObject foodItems_JsonObj = (JSONObject) getChild(listPosition,expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.foodtitledetailsitem, null);
        }
        CustomTextViewBold Txt_food_name = (CustomTextViewBold) convertView.findViewById(R.id.Txt_food_name);
        CustomTextViewBold Txt_food_price=(CustomTextViewBold)convertView.findViewById(R.id.Txt_food_price);
        CustomTextViewNormal Txt_food_description=(CustomTextViewNormal)convertView.findViewById(R.id.Txt_food_description);

        try {
            Txt_food_name.setText(foodItems_JsonObj.getString("food_name"));
            Txt_food_price.setText("€"+foodItems_JsonObj.getString("price"));
            Txt_food_description.setText(foodItems_JsonObj.getString("ingredients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {


        try {
            return jsonArray.getJSONObject(listPosition).getJSONArray("food_items").length();
        } catch (JSONException e) {
            e.printStackTrace();
       return 0;
        }
//        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
//                .size();
    }

    @Override
    public Object getGroup(int listPosition) {


        try {
            return this.jsonArray.getJSONObject(listPosition);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getGroupCount() {

        return jsonArray.length();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        JSONObject foodCategory_JsonObj = (JSONObject) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.foodtitledetail, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.Txt_food);
        ImageView food_category_img=(ImageView)convertView.findViewById(R.id.food_category_img);
        ImageView imageView_arrow = (ImageView)convertView.findViewById(R.id.imgarrow);
        int imageResourceId = isExpanded ? R.drawable.up_arrow
                : R.drawable.arrowdown;
        imageView_arrow.setImageResource(imageResourceId);

        listTitleTextView.setTypeface(null, Typeface.BOLD);
        try {
            listTitleTextView.setText(foodCategory_JsonObj.getString("category_name"));

            Glide.with(mContext).
                    load(foodCategory_JsonObj.getString("category_image")).
                    error(R.drawable.profileicon)
                    .into(food_category_img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}