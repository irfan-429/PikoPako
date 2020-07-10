package com.pikopako.Fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.pikopako.aaa.FoodDetailActivity;
import com.pikopako.Adapter.ExpandableListAdapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

@SuppressLint("ValidFragment")
public class RestroDetailItemFragment extends Fragment {
    ProgressDialog progressDialog;

    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;

    @BindView(R.id.toplayout)
    LinearLayout mSnackView;

    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    String restro_id,food_id,coordinate_i;
    JSONArray categoriesArray;
    String restaurant_status="Closed";

    String language="";
    @SuppressLint("ValidFragment")
    public RestroDetailItemFragment(String cafe_id,String coordinate_id) {
        restro_id=cafe_id;
        coordinate_i=coordinate_id;
        Log.e("id", "RestroDetailItemFragment: "+restro_id );
    }

    public RestroDetailItemFragment(){}
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_restro_detail_item_activity, container, false);
        ButterKnife.bind(this, view);
        DisplayMetrics metrics = new DisplayMetrics();
        getData();
        int width = metrics.widthPixels;

   //     expandableListDetail = ExpandableListData.getData();
      //  expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
     //   expandableListAdapter = new ExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail);
     //   expandableListView.setAdapter(expandableListAdapter);


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {


//                Toast.makeText(getActivity(),
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
              //  Toast.makeText(getActivity(),expandableListTitle.get(groupPosition) + " -> " + expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();

//               FoodDetailActivity fragment=new FoodDetailActivity();
//                FragmentTransaction transaction=getFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container,fragment);
//                transaction.commit();
                Log.e("tagOnclickItem"," Group Position :- "+groupPosition+" Child Position :-  "+childPosition+"ID:- "+id);

                try {
                  food_id=   categoriesArray.getJSONObject(groupPosition).getJSONArray("food_items").getJSONObject(childPosition).getString("food_id");
                        Log.e("tagfoodid",""+food_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Bundle restro_bundle=getArguments();


                Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
                intent.putExtra("food_id",food_id);
                intent.putExtra("restaurant_id",restro_id);
                intent.putExtra("coordinate_id",coordinate_i);
             //   intent.putExtra("restaurant_status",restaurant_status);
                intent.putExtra("restro_bundle",restro_bundle);
                Log.e("restro id detail", "onChildClick: "+restro_id+"status"+restaurant_status +"bundle data"+restro_bundle.getString("restro_id"));
                startActivity(intent);
                return false;



//                Intent i = new Intent(getActivity(),RestroInfoActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putSerializable("data",productListModelArrayList);
//                bundle.putString("position",position);
//                i.putExtras(bundle);
//                startActivity(i);
//                getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


            }
        });

        //   listeners();
        return view;
    }




    private void getData(){
        progressDialog= UiHelper.generateProgressDialog(getActivity(),false);
        progressDialog.show();
        TimeZone tz = TimeZone.getDefault();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("restaurant_id", restro_id);
        jsonObject.addProperty("coordinate_id", coordinate_i);

        jsonObject.addProperty("timezone",tz.getID());
        jsonObject.addProperty("time", String.valueOf(Calendar.getInstance().getTime()));
        jsonObject.addProperty("language",language);
        Log.e("tag", "json object restro id detailitemfrag:- " + jsonObject.toString());

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getRestaurantServices(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().get(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if(progressDialog!=null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        Log.e("restrodetailitemjson",jsonObject1.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            if (isAdded()) {
                                 categoriesArray = jsonObject1.getJSONObject("data").getJSONArray("food_categories");
                                 restaurant_status=jsonObject1.getJSONObject("data").getString("restaurant_status");
                                Log.e("categoriesArray",categoriesArray.toString());
                        expandableListAdapter=new ExpandableListAdapter(getActivity(),categoriesArray);
                        expandableListView.setAdapter(expandableListAdapter);




                            }
                        }
                        else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void Error(String error) {
                if(progressDialog!=null)
                    progressDialog.dismiss();
                UiHelper.showToast(getActivity(),error);
            }
            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(getActivity(),mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}




