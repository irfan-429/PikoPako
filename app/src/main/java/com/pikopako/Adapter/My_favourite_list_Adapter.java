package com.pikopako.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.pikopako.Activity.FoodDetailActivity;
import com.pikopako.Activity.OrderList;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.RestroInfoServices;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;

public class My_favourite_list_Adapter extends RecyclerView.Adapter<My_favourite_list_Adapter.ViewHolder> {


    Context mContext;
    JSONArray jsonArray;
    RestroInfoServices clickItemEvent;
    String id ="";
    String restaurant_id="";
    String language="";

    public My_favourite_list_Adapter(Context mContext, JSONArray jsonArray) {

        this.mContext=mContext;
        this.clickItemEvent=clickItemEvent;
        this.jsonArray=jsonArray;


        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";

    }




    @NonNull
    @Override
    public My_favourite_list_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_favourites_list,parent,false);


        return new My_favourite_list_Adapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final My_favourite_list_Adapter.ViewHolder holder, final int position) {

        try {
            JSONObject categoriesobject=jsonArray.getJSONObject(position);
            holder.txt_foodName.setText(categoriesobject.getString("food_name"));
            Glide.with(mContext).load(categoriesobject.getString("food_image")).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.IMMEDIATE).dontAnimate().placeholder(mContext.getResources().getDrawable(R.drawable.profileicon)).into(holder.img_food);
            holder.txt_food_price.setText("€ "+categoriesobject.getString("price"));
            holder.txt_food_category.setText(categoriesobject.getString("category_name"));
            id=categoriesobject.getString("food_id");
            restaurant_id=categoriesobject.getString("restaurant_id");
            holder.Iv_heart.setVisibility(View.GONE);
            holder.Iv_heart_red.setVisibility(View.VISIBLE);

            holder.Iv_heart_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder. Iv_heart_red.setVisibility(View.GONE);
                    holder.Iv_heart.setVisibility(View.VISIBLE);

                    add_to_delete_food("1",position);
                    Log.e("position", "onClick: "+position );
                }
            });


            holder.mSnackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, FoodDetailActivity.class);

                    try {
                        i.putExtra("food_id",jsonArray.getJSONObject(position).getString("food_id"));
                        i.putExtra("restaurant_id",jsonArray.getJSONObject(position).getString("restaurant_id"));
                        i.putExtra("statusFromFavourite",jsonArray.getJSONObject(position).getString("restaurant_status"));
                        BaseApplication.getInstance().getSession().setRestroId("");
                        BaseApplication.getInstance().getSession().setRestroId(jsonArray.getJSONObject(position).getString("restaurant_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("food_id in fvrite list", "onClick: "+id );
                    mContext.startActivity(i);

                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout mSnackView;
     ImageView img_food;
     CustomTextViewBold txt_foodName,txt_food_price;
        CustomTextViewNormal txt_food_category;
        ImageView Iv_heart_red,Iv_heart;

        public ViewHolder(View itemView) {
            super(itemView);
            mSnackView=(RelativeLayout)itemView.findViewById(R.id.topp);
            img_food=(ImageView)itemView.findViewById(R.id.img_food);
            txt_foodName=(CustomTextViewBold) itemView.findViewById(R.id.txt_foodName);
            txt_food_price=(CustomTextViewBold) itemView.findViewById(R.id.txt_food_price);
            txt_food_category=(CustomTextViewNormal)itemView.findViewById(R.id.txt_food_category);
            Iv_heart=(ImageView)itemView.findViewById(R.id.Iv_heart);
            Iv_heart_red=(ImageView)itemView.findViewById(R.id.Iv_heart_red);
        }
    }

    public interface ClickItemEvent{
        void onClickItem(String itemId);
    }

    private void add_to_delete_food(String status, final int position) {
        // final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        //   progressDialog.show();

        JsonObject jsonObject = new JsonObject();


        jsonObject.addProperty("food_item_id", id);
        jsonObject.addProperty("status", status);
        jsonObject.addProperty("language",language);
        Log.e("tag", "add delete id" + jsonObject.toString());


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().addFavourite(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(mContext, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                //    progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            //         BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));


                                favourite(position);

                            Log.e("tag","food added to delete");

                            UiHelper.showToast(mContext,jsonObject1.getString("message"));

                        } else {
                            UiHelper.showToast(mContext, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }

            @Override
            public void Error(String error) {
                //   if (progressDialog != null)
                //       progressDialog.dismiss();
                UiHelper.showToast(mContext, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    //     if (progressDialog != null)
                    //         progressDialog.dismiss();
                    UiHelper.showToast(mContext, "No Internet Connection");
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }


            @SuppressLint("NewApi")
            private void favourite(int pos){

                Log.e("favourt method position", "favourite: "+pos );

                JSONArray newjsonArray = new JSONArray();
                int len = jsonArray.length();
                if (jsonArray != null) {
                    for (int i=0;i<len;i++)
                    {

                        if (i != pos)
                        {
                            try {
                                newjsonArray.put(jsonArray.get(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                        Log.e("before", "array items: "+jsonArray.length() );
                        jsonArray=newjsonArray ;
                        notifyDataSetChanged();
                        Log.e("after", "array items: "+jsonArray.length() );

                }

            }

}
