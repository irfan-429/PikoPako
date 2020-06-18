package com.pikopako.Adapter;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.Model.Ingrediants_modal;
import com.pikopako.R;

import java.util.ArrayList;

public class FoodDetailAdapter extends RecyclerView.Adapter<FoodDetailAdapter.ViewHolder> {


    Context mContext;
    long click_time=0;
    long delay=500;
   public ArrayList<Ingrediants_modal> adapter_arraylist = new ArrayList<>();
    Ingrediants_modal ingrediants_modal;

    public FoodDetailAdapter(Context mContext, ArrayList<Ingrediants_modal> modalArrayList) {

        this.mContext=mContext;
        this.adapter_arraylist = new ArrayList<Ingrediants_modal>(modalArrayList);
    }




    @NonNull
    @Override
    public FoodDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_detail_topping_layout,parent,false);
        return new FoodDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodDetailAdapter.ViewHolder holder, final int position) {

        Log.e("Bug","FoodAdapter View holder");

        holder.checkBox.setChecked(adapter_arraylist.get(position).isIngredient_add);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (SystemClock.elapsedRealtime()-click_time < delay)
                    return;
                click_time=SystemClock.elapsedRealtime();
                adapter_arraylist.get(position).isIngredient_add = b;

             }
          });

            holder.txt_ingre_price.setText("€"+adapter_arraylist.get(position).ingredients_price);
            holder.checkBox.setText(adapter_arraylist.get(position).ingredients_name);
//                ingrediants_modal.setIngredientPrice(ingrediants_modalArrayList.get(position).ingredients_price);
    }

    @Override
    public int getItemCount() {
        return adapter_arraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
    CustomTextViewBold txt_ingre_price;
        public ViewHolder(View itemView) {
            super(itemView);
             checkBox=(CheckBox)itemView.findViewById(R.id.checkbox);
            txt_ingre_price=(CustomTextViewBold)itemView.findViewById(R.id.txt_ingre_price);
                this.setIsRecyclable(false);
        }
    }

    public interface ClickItemEvent{
        void onClickItem(String itemId);
    }


}
