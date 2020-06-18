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
import com.pikopako.Fragment.RestroInfoServices;
import com.pikopako.Model.Ingrediants_modal;
import com.pikopako.R;

import java.util.ArrayList;

public class Choose_topping_adapter extends RecyclerView.Adapter<Choose_topping_adapter.ViewHolder> {
    long click_time=0;
    long delay=500;

    Context mContext;
    public ArrayList<Ingrediants_modal> adapter_arraylist = new ArrayList<>();

   // RestroInfoServices clickItemEvent;


    public Choose_topping_adapter(Context mContext, ArrayList<Ingrediants_modal> dd) {

        this.mContext = mContext;
        this.adapter_arraylist = dd;
    }


    @NonNull
    @Override
    public Choose_topping_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_toppings_layout, parent, false);
        return new Choose_topping_adapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final Choose_topping_adapter.ViewHolder holder, final int position) {
        holder.checkBox.setChecked(adapter_arraylist.get(position).isIngredient_add);

      //  holder.checkBox.setOnCheckedChangeListener(null);


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (SystemClock.elapsedRealtime()-click_time < delay)
//                    return;
//                click_time=SystemClock.elapsedRealtime();
                Log.e("CHooseToppingAdap", "onCheckedChanged: "+adapter_arraylist.get(position).ingredients_name );
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

    public void SelectALl() {

        for (int i=0;i<adapter_arraylist.size();i++){
            adapter_arraylist.get(i).isIngredient_add=true;
        }

        notifyDataSetChanged();
       // notifyItemRangeChanged(0,adapter_arraylist.size());
    }

    public void DeSelectALl() {

        Log.e("TAG", "DeSelectALl: "+adapter_arraylist.size() );
        for (int i=0;i<adapter_arraylist.size();i++){
            adapter_arraylist.get(i).isIngredient_add=false;
            Log.e("TAG", "Is Added: "+adapter_arraylist.get(i).isIngredient_add );
        }

        notifyDataSetChanged();

    //    notifyItemRangeChanged(0,adapter_arraylist.size());
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

    public interface ClickItemEvent {
        void onClickItem(String itemId);
    }


}