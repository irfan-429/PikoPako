package com.pikopako.Adapter;

import android.content.Context;
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
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.Model.ProductListModel;
import com.pikopako.R;

import java.util.ArrayList;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    Context mContext;
   public ArrayList<ProductListModel>arrayList;
    ClickItemEvent clickItemEvent;
    ProductListModel productModel;


    public ProductListAdapter(Context mContext, ArrayList<ProductListModel> arrayList, ClickItemEvent clickItemEvent) {
        this.arrayList = arrayList;
        this.mContext=mContext;
        this.clickItemEvent=clickItemEvent;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        productModel=arrayList.get(position);
        if (productModel.Pic_url!=null)
            Glide.with(mContext).load(productModel.Pic_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.IMMEDIATE).dontAnimate().placeholder(mContext.getResources().getDrawable(R.drawable.profileicon)).into(holder.imageView);

        holder.txt_cafename.setText(productModel.cafe_name);
        holder.txt_cafestatus.setText(productModel.cafe_status);
        if (productModel.cafe_status.equalsIgnoreCase(mContext.getString(R.string.closed))){
            holder.txt_cafestatus.setTextColor(mContext.getResources().getColor(R.color.red));
        }

        holder.txt_cafesublist.setText(productModel.cafe_address);
        holder.txt_caferating.setText(productModel.cafe_rating);
        holder.txt_cafedeleiverytime.setText(productModel.cafe_deleiverytime+" "+mContext.getString(R.string.minutes));
        holder.txt_cafeminorder.setText("€"+productModel.cafe_minorder+" "+mContext.getString(R.string.minorder));
        //   Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.restro);
      //  Bitmap circularBitmap = UiHelper.getRoundedCornerBitmap(bitmap, 100);
     //   holder.imageView.setImageBitmap(circularBitmap);





        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("tag","ff"+position +" data" +arrayList.toString());
                BaseApplication.getInstance().getSession().setDeliveryTime(arrayList.get(position).cafe_deleiverytime);//set rest max delivery time
                clickItemEvent.onClickItem(arrayList.get(position).cafe_id, String.valueOf(position));

                Log.e("adaptertag","" +position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CustomTextViewBold txt_cafename;
        CustomTextViewNormal txt_cafestatus;
        CustomTextViewNormal txt_cafesublist,txt_caferating,txt_cafedeleiverytime,txt_cafeminorder;
        RelativeLayout mainView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView =(ImageView)itemView.findViewById(R.id.imgProduct) ;
            txt_cafename=(CustomTextViewBold)itemView.findViewById(R.id.txt_cafename);
            txt_cafestatus=(CustomTextViewNormal) itemView.findViewById(R.id.txt_cafestatus);
            txt_cafesublist =(CustomTextViewNormal)itemView.findViewById(R.id.txt_cafesublist);
            txt_caferating =(CustomTextViewNormal)itemView.findViewById(R.id.txt_caferating);
            txt_cafedeleiverytime =(CustomTextViewNormal)itemView.findViewById(R.id.txt_cafedeleiverytime);
            txt_cafeminorder =(CustomTextViewNormal)itemView.findViewById(R.id.txt_minorder);
            mainView=(RelativeLayout)itemView.findViewById(R.id.mainView);
        }
    }

   public interface ClickItemEvent{
       void onClickItem(String itemId,String position);

    }
}


