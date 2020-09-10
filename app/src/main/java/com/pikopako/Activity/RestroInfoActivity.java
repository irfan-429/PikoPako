package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.Fragment.RestroDetailItemFragment;
import com.pikopako.Fragment.RestroInfoServices;
import com.pikopako.Model.ProductListModel;
import com.pikopako.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RestroInfoActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.pager)
    ViewPager viewPager;

    @BindView(R.id.cafe_image)
    ImageView cafe_image;

    @BindView(R.id.Txt_cafename)
    CustomTextViewBold Txt_cafename;

    @BindView(R.id.txt_address)
    CustomTextViewNormal txt_address;

    @BindView(R.id.txt_status)
    CustomTextViewBold txt_status;

    @BindView(R.id.txt_rating)
    CustomTextViewBold txt_rating;

    @BindView(R.id.txt_minordertime)
    CustomTextViewBold txt_minordertime;

    @BindView(R.id.txt_minorderprice)
    CustomTextViewBold txt_minorderprice;

    @BindView(R.id.txt_totalrating)
    CustomTextViewNormal txt_totalrating;

    @BindView(R.id.tvTitle)
    CustomTextViewBold tvTitle;

    ProductListModel productListModel=new ProductListModel();
    ProgressDialog progressDialog;


    public String restro_image="";
    public String restro_name="";
    public String restro_location="";
    public String restro_status="";
    public String restro_id="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restro_info);
        ButterKnife.bind(this);
        listners();
        initialize();
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);



    }

    private void initialize() {


        Intent intent=this.getIntent();
        Bundle bundle=intent.getExtras();
        ArrayList<ProductListModel> arrayList= (ArrayList<ProductListModel>) bundle.getSerializable("data");
        String position= bundle.getString("position");
        Log.e("tag"," Position data:- "+position);

         productListModel = arrayList.get(Integer.parseInt(position));

        if (productListModel.Pic_url!=null)
            Glide.with(this).load(productListModel.Pic_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.IMMEDIATE).dontAnimate().placeholder(this.getResources().getDrawable(R.drawable.profileicon)).into(cafe_image);

            tvTitle.setText(productListModel.cafe_name);
            Txt_cafename.setText(productListModel.cafe_name);
            txt_address.setText(productListModel.cafe_address);
            txt_status.setText(productListModel.cafe_status);
        if (productListModel.cafe_status.equalsIgnoreCase(getResources().getString(R.string.closed))){
           txt_status.setTextColor(getResources().getColor(R.color.red));
        }

        txt_rating.setText(productListModel.cafe_rating+" ");


            //To store minimum order amount
            BaseApplication.getInstance().getSession().setminimum_order_amount(productListModel.cafe_minorder);

            txt_minorderprice.setText("€"+productListModel.cafe_minorder);
            txt_minordertime.setText(productListModel.cafe_deleiverytime+" "+getString(R.string.minutes));
        Log.e("tag", "initialize: "+productListModel.cafe_deleiverytime+getString(R.string.minutes) );

        Log.e("TAG", "Total Reviews: "+productListModel.total_reviews );
             txt_totalrating.setText(productListModel.total_reviews+" ");

             restro_image=productListModel.Pic_url;
             restro_name=productListModel.cafe_name;
             restro_location=productListModel.cafe_address;
             restro_status=productListModel.cafe_status;
             restro_id=productListModel.cafe_id;

        BaseApplication.getInstance().getSession().setCoordinateId(productListModel.coordinate_id);
        BaseApplication.getInstance().getSession().setRestroId(productListModel.cafe_id);
            Log.e("data to set"," "+productListModel.Pic_url +" name"+productListModel.cafe_name);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        RestroDetailItemFragment restroDetailItemFragment=new RestroDetailItemFragment(productListModel.cafe_id,productListModel.coordinate_id);

        Bundle bundle=new Bundle();
        bundle.putString("restro_image",restro_image);
        bundle.putString("restro_name",restro_name);
        bundle.putString("restro_location",restro_location);
        bundle.putString("restro_status",restro_status);
        bundle.putString("restro_id",restro_id);
        restroDetailItemFragment.setArguments(bundle);

        adapter.addFragment(restroDetailItemFragment,getString(R.string.restaurant_menu));
        adapter.addFragment(new RestroInfoServices(productListModel.cafe_id,productListModel.coordinate_id),getString(R.string.restaurant_detail));
        viewPager.setAdapter(adapter);
    }


    private void listners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    //    mTitle.setText("  ");
     //   btn_menu.setOnClickListener(this);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btnmenu:
//                Intent intent=new Intent(RestroInfoActivity.this,RestroDetailItemFragment.class);
//                startActivity(intent);
//                break;
        }
    }

}
