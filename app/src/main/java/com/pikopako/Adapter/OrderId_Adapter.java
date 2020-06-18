package com.pikopako.Adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pikopako.Activity.OrderIdActivity;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderId_Adapter extends RecyclerView.Adapter<OrderId_Adapter.ViewHolder> {


    Context mContext;


    JSONArray jsonArray;
    OrderIdActivity clickItemEvent;
    private RadioButton lastCheckedRB = null;

    int order_id=-1;

    public OrderId_Adapter(Context mContext, JSONArray jsonArray,int id) {
        this.mContext = mContext;
        this.clickItemEvent = clickItemEvent;
        this.jsonArray = jsonArray;
        this.order_id=id;
    }


    @NonNull
    @Override
    public OrderId_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_orderid_layout, parent, false);
        return new OrderId_Adapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final OrderId_Adapter.ViewHolder holder, final int position) {

        try {
            final JSONObject jsonObject=jsonArray.getJSONObject(position);
            Log.e("jsonobject", "onBindViewHolder: "+jsonObject.toString() );

            holder.radioButton.setText("PIKOPAKO#"+jsonObject.getString("id"));

            if (order_id==jsonObject.getInt("id"))
                holder.radioButton.setChecked(true);
            else
                holder.radioButton.setChecked(false);

                holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        try {
                            order_id=jsonArray.getJSONObject(position).getInt("id");
                            notifyDataSetChanged();
                            ((OrderIdActivity)mContext).setRadio(order_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        RadioButton radioButton;
       // RadioGroup radioGroup;
        public ViewHolder(View itemView) {
            super(itemView);

            radioButton=(RadioButton) itemView.findViewById(R.id.radiobutton);
      //      radioGroup=(RadioGroup)itemView.findViewById(R.id.radioGroup);
            this.setIsRecyclable(false);

        }
    }

    public interface ClickItemEvent {
        void onClickItem(String itemId);
    }


}