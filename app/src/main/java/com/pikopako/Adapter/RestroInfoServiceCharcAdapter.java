package com.pikopako.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.IconTextView;
import com.pikopako.Fragment.RestroInfoServices;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestroInfoServiceCharcAdapter extends RecyclerView.Adapter<RestroInfoServiceCharcAdapter.ViewHolder> {


        Context mContext;
    JSONArray jsonArray=new JSONArray();
        RestroInfoServices clickItemEvent;

public RestroInfoServiceCharcAdapter(Context mContext, JSONArray jsonArray) {

        this.mContext=mContext;
        this.clickItemEvent=clickItemEvent;
        this.jsonArray=jsonArray;
        }


@NonNull
@Override
public RestroInfoServiceCharcAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.restro_info_characteristics_row,parent,false);
        return new ViewHolder(view);
        }

        @Override
public void onBindViewHolder(@NonNull RestroInfoServiceCharcAdapter.ViewHolder holder, int position) {

    try {
        JSONObject categoriesobject=jsonArray.getJSONObject(position);
        holder.txt_detail.setText(categoriesobject.getString("sub_name"));
        if (categoriesobject.getString("isChecked").equalsIgnoreCase("1")){
             holder.img_close.setText(R.string.icon_check_circle);
             holder.img_close.setTextColor(mContext.getResources().getColor(R.color.holo_green_dark));
        }
//        else
//            holder.img_close.setText(R.string.icon_close_circle);
//            holder.img_close.setTextColor(mContext.getResources().getColor(R.color.holo_red_dark));

    } catch (JSONException e) {
        e.printStackTrace();
    }

}

@Override
public int getItemCount() {
        return jsonArray.length();
        }

public class ViewHolder extends RecyclerView.ViewHolder {

    IconTextView img_close;
    CustomTextViewBold txt_detail;

    public ViewHolder(View itemView) {
        super(itemView);
    // img_detail=(ImageView)itemView.findViewById(R.id.image_detail);
     txt_detail=(CustomTextViewBold) itemView.findViewById(R.id.txt_detail);
     img_close=(IconTextView)itemView.findViewById(R.id.Img_serves_alcohol);
    }
}

public interface ClickItemEvent{
    void onClickItem(String itemId);
}
}
