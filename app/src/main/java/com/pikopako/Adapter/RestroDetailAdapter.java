package com.pikopako.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pikopako.Fragment.RestroDetailItemFragment;
import com.pikopako.Model.ProductModel;
import com.pikopako.R;

import java.util.ArrayList;

public class RestroDetailAdapter extends RecyclerView.Adapter<RestroDetailAdapter.ViewHolder> {
    Context mContext;
    ArrayList<ProductModel> arrayList;
    RestroDetailItemFragment clickItemEvent;

    public RestroDetailAdapter(Context mContext, ArrayList<ProductModel> arrayList, RestroDetailItemFragment clickItemEvent) {
        this.arrayList = arrayList;
        this.mContext=mContext;
        this.clickItemEvent=clickItemEvent;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_restro_detail_item_activity,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    clickItemEvent.onClickItem(arrayList.get(getAdapterPosition()).getId());
//                }
//            });
        }
    }

    public interface ClickItemEvent{
        void onClickItem(String itemId);
    }
}

