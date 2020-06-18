package com.pikopako.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pikopako.Activity.RestroRatingActivity;
import com.pikopako.Adapter.RestroInfoServiceCharcAdapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Model.ProductListModel;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class RestroInfoServices extends Fragment {
    @BindView(R.id.toprelativelyt)
    RelativeLayout mSnackView;

    @BindView(R.id.recycleview_characteristics)
    RecyclerView recyclerViewcharac;

    @BindView(R.id.recycleview_seating)
    RecyclerView recyclerViewSeating;

    @BindView(R.id.recycleview_services)
    RecyclerView recyclerViewServices;

    @BindView(R.id.imageProfile1)
    CircularImageView imageProfile1;

    @BindView(R.id.imageProfile2)
    CircularImageView imageProfile2;

    @BindView(R.id.imageProfile3)
    CircularImageView imageProfile3;

    @BindView(R.id.txt_name1)
    CustomTextViewBold txt_name1;

    @BindView(R.id.txt_name2)
    CustomTextViewBold txt_name2;

    @BindView(R.id.txt_name3)
    CustomTextViewBold txt_name3;


    @BindView(R.id.txt_rating1)
    CustomTextViewNormal txt_rating1;

    @BindView(R.id.txt_rating2)
    CustomTextViewNormal txt_rating2;

    @BindView(R.id.txt_rating3)
    CustomTextViewNormal txt_rating3;

    @BindView(R.id.txt_comments1)
    CustomTextViewNormal txt_comments1;

    @BindView(R.id.txt_comments2)
    CustomTextViewNormal txt_comments2;

    @BindView(R.id.txt_comments3)
    CustomTextViewNormal txt_comments3;

    @BindView(R.id.rating1)
    LinearLayout rating1;

    @BindView(R.id.rating2)
    LinearLayout rating2;

    @BindView(R.id.rating3)
    LinearLayout rating3;
//    @BindView(R.id.recycleview_payment)
//    RecyclerView recyclerViewPayment;

    @BindView(R.id.txt_show_reviews)
    CustomTextViewBold txt_show_reviews;
    ProgressDialog progressDialog;
    ProductListModel productModel;
    RestroInfoServiceCharcAdapter restroInfoServiceCharcAdapter, restroInfoServiceSeatingAdapter, restroInfoServiceAdapter, restroInfoServicePaymentAdapter;
    String id, coordinate_i;
    String language="";

    @SuppressLint("ValidFragment")
    public RestroInfoServices(String cafe_id, String coordinate_id) {
        id = cafe_id;
        coordinate_i = coordinate_id;
    }

    public RestroInfoServices() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restro_info_services, container, false);
        ButterKnife.bind(this, view);
        getData();
        txt_show_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RestroRatingActivity.class);

                intent.putExtra("id", id);
                intent.putExtra("coordinate_id", coordinate_i);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void getData() {
        progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();
        TimeZone tz = TimeZone.getDefault();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("restaurant_id", id);
        jsonObject.addProperty("coordinate_id", coordinate_i);

        jsonObject.addProperty("timezone", tz.getID());
        jsonObject.addProperty("time", String.valueOf(Calendar.getInstance().getTime()));
        jsonObject.addProperty("language",language);
        Log.e("tag", "json object edit profile" + jsonObject.toString());

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getRestaurantServices(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().get(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        Log.e("hhg", jsonObject1.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            if (isAdded()) {
                                // setData(jsonObject1);
                                JSONObject alcohol = new JSONObject();
                                JSONObject service = new JSONObject();
                                JSONObject seating = new JSONObject();


                                JSONArray categoriesArray = jsonObject1.getJSONObject("data").getJSONArray("categories");
                                for (int i = 0; i < categoriesArray.length(); i++) {


                                    JSONObject jsonObject2 = categoriesArray.getJSONObject(i);
                                    if (jsonObject2.getString("title").equalsIgnoreCase("Alcohol")) {

                                        alcohol = jsonObject2;
                                    } else if (jsonObject2.getString("title").equalsIgnoreCase("Service")) {

                                        service = jsonObject2;
                                    } else
                                        seating = jsonObject2;
                                }
                                //reviewsArray
                                JSONArray reviewsArray = jsonObject1.getJSONObject("data").getJSONArray("review");
                                String image=   jsonObject1.getJSONObject("data").getString("profile_image_base_url");
                                if (reviewsArray.length() > 2) {
                                    rating1.setVisibility(View.VISIBLE);
                                    rating2.setVisibility(View.VISIBLE);
                                    rating3.setVisibility(View.VISIBLE);


                                    JSONObject timingdata_0 = reviewsArray.getJSONObject(0);
                                    JSONObject timingdata_1 = reviewsArray.getJSONObject(1);
                                    JSONObject timingdata_2 = reviewsArray.getJSONObject(2);

                                    txt_rating1.setText(timingdata_0.getString("rating"));
                                    txt_rating2.setText(timingdata_1.getString("rating"));
                                    txt_rating3.setText(timingdata_2.getString("rating"));

                                    txt_name1.setText(timingdata_0.getString("name"));
                                    txt_name2.setText(timingdata_1.getString("name"));
                                    txt_name3.setText(timingdata_2.getString("name"));

                                    txt_comments1.setText(timingdata_0.getString("review"));
                                    txt_comments2.setText(timingdata_1.getString("review"));
                                    txt_comments3.setText(timingdata_2.getString("review"));


                                    Glide.with(getActivity()).
                                            load(image+timingdata_0.getString("profile_image"))
                                            .error(R.drawable.profileicon)
                                            .into(imageProfile1);

                                    Glide.with(getActivity()).
                                            load(image+timingdata_1.getString("profile_image"))
                                            .error(R.drawable.profileicon)
                                            .into(imageProfile2);

                                    Glide.with(getActivity()).
                                            load(image+timingdata_2.getString("profile_image"))
                                            .error(R.drawable.profileicon)
                                            .into(imageProfile3);

                                } else if (reviewsArray.length() == 2) {
                                    txt_show_reviews.setVisibility(View.GONE);
                                    rating1.setVisibility(View.VISIBLE);
                                    rating2.setVisibility(View.VISIBLE);
                                    rating3.setVisibility(View.GONE);

                                    JSONObject timingdata_0 = reviewsArray.getJSONObject(0);
                                    JSONObject timingdata_1 = reviewsArray.getJSONObject(1);

                                    txt_rating1.setText(timingdata_0.getString("rating"));
                                    txt_rating2.setText(timingdata_1.getString("rating"));

                                    txt_name1.setText(timingdata_0.getString("name"));
                                    txt_name2.setText(timingdata_1.getString("name"));

                                    txt_comments1.setText(timingdata_0.getString("review"));
                                    txt_comments2.setText(timingdata_1.getString("review"));

                                    Glide.with(getActivity()).
                                            load(image+timingdata_0.getString("profile_image"))
                                            .error(R.drawable.profileicon)
                                            .into(imageProfile1);

                                    Glide.with(getActivity()).
                                            load(image+timingdata_1.getString("profile_image"))
                                            .error(R.drawable.profileicon)
                                            .into(imageProfile2);
                                } else if (reviewsArray.length() == 1) {
                                    txt_show_reviews.setVisibility(View.GONE);
                                    rating1.setVisibility(View.VISIBLE);
                                    rating2.setVisibility(View.GONE);
                                    rating3.setVisibility(View.GONE);

                                    JSONObject timingdata_0 = reviewsArray.getJSONObject(0);
                                    txt_rating1.setText(timingdata_0.getString("rating"));
                                    txt_name1.setText(timingdata_0.getString("name"));
                                    txt_comments1.setText(timingdata_0.getString("review"));
                                    Glide.with(getActivity()).
                                            load(image+timingdata_0.getString("profile_image"))
                                            .error(R.drawable.profileicon)
                                            .into(imageProfile1);


                                }


                                //Charac
                                recyclerViewcharac.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                restroInfoServiceCharcAdapter = new RestroInfoServiceCharcAdapter(getActivity(), alcohol.getJSONArray("subcategories"));
                                recyclerViewcharac.setAdapter(restroInfoServiceCharcAdapter);
                                restroInfoServiceCharcAdapter.notifyDataSetChanged();
                                //seating
                                recyclerViewSeating.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                restroInfoServiceSeatingAdapter = new RestroInfoServiceCharcAdapter(getActivity(), seating.getJSONArray("subcategories"));
                                recyclerViewSeating.setAdapter(restroInfoServiceSeatingAdapter);
                                restroInfoServiceSeatingAdapter.notifyDataSetChanged();
                                // service
                                recyclerViewServices.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                restroInfoServiceAdapter = new RestroInfoServiceCharcAdapter(getActivity(), service.getJSONArray("subcategories"));
                                recyclerViewServices.setAdapter(restroInfoServiceAdapter);
                                restroInfoServiceAdapter.notifyDataSetChanged();
                                //payment
                                //      recyclerViewPayment.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                //     restroInfoServicePaymentAdapter = new RestroInfoServiceCharcAdapter(getActivity(), jsonArray);
                                //     recyclerViewPayment.setAdapter(restroInfoServicePaymentAdapter);
                                //     restroInfoServicePaymentAdapter.notifyDataSetChanged();


                            }
                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void Error(String error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                UiHelper.showErrorMessage(mSnackView, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(getActivity(), mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

}