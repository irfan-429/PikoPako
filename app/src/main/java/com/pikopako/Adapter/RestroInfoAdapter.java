package com.pikopako.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.Fragment.RestroInfoServices;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestroInfoAdapter extends RecyclerView.Adapter<RestroInfoAdapter.ViewHolder> {


    Context mContext;
    JSONArray jsonArray = new JSONArray();
    RestroInfoServices clickItemEvent;
    String img;

    public RestroInfoAdapter(Context mContext, JSONArray jsonArray, String image) {

        this.mContext = mContext;
        this.clickItemEvent = clickItemEvent;
        this.jsonArray = jsonArray;
        this.img=image;
    }


    @NonNull
    @Override
    public RestroInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restro_info_rating_row, parent, false);
        return new RestroInfoAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RestroInfoAdapter.ViewHolder holder, int position) {

        try {
            JSONObject categoriesobject = jsonArray.getJSONObject(position);


            Log.e("data object", "onBindViewHolder: " + categoriesobject.toString());
            holder.txt_ratingg.setText(categoriesobject.getString("rating"));
            holder.txt_comment.setText(categoriesobject.getString("review"));
//            Glide.with(mContext).
//                    load(categoriesobject.getString("profile_image"))
//                    .error(R.drawable.restro)
//                    .into(holder.imageProfile);
            Glide.with(mContext).load(img+categoriesobject.getString("profile_image")).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.IMMEDIATE).dontAnimate().placeholder(mContext.getResources().getDrawable(R.drawable.profileicon)).into(holder.imageProfile);

            holder.txt_nameuser.setText(categoriesobject.getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView imageProfile;
        CustomTextViewBold txt_nameuser;
        CustomTextViewNormal txt_ratingg, txt_comment;

        public ViewHolder(View itemView) {
            super(itemView);

            imageProfile = (CircularImageView) itemView.findViewById(R.id.imageProfil);
            txt_nameuser = (CustomTextViewBold) itemView.findViewById(R.id.txt_nameuser);
            txt_ratingg = (CustomTextViewNormal) itemView.findViewById(R.id.txt_ratingg);
            txt_comment = (CustomTextViewNormal) itemView.findViewById(R.id.txt_comment);

        }
    }

    public interface ClickItemEvent {
        void onClickItem(String itemId);
    }
}
