package com.pikopako.Adapter;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pikopako.Model.FilterModel;
import com.pikopako.R;

import org.json.JSONArray;

import java.util.ArrayList;

public class FilterAdapter  extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {


    Context mContext;
    JSONArray jsonArray=new JSONArray();
    long click_time=0;
    long delay=500;
    public ArrayList<FilterModel> filterModelArrayList = new ArrayList<>();


    public FilterAdapter(ArrayList<FilterModel> filterModels, Context context) {
        if (!filterModels.isEmpty())
            filterModelArrayList = filterModels;
        this.mContext = context;
    }


    @NonNull
    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_layout_row,parent,false);
        return new FilterAdapter.ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull final FilterAdapter.ViewHolder holder, final int position) {
        holder.checkbox.setChecked(filterModelArrayList.get(position).isChecked);
       holder.checkbox.setText(filterModelArrayList.get(position).category_name);

       holder.checkbox.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (SystemClock.elapsedRealtime()-click_time < delay)
                   return;
               click_time=SystemClock.elapsedRealtime();
               Log.e("onclick", "onClick: " +position);
             //  holder.checkbox.setChecked(!holder.checkbox.isChecked());
               filterModelArrayList.get(position).isChecked=holder.checkbox.isChecked();
               notifyItemRangeChanged(0,filterModelArrayList.size());

           }
       });

    }

    public void SelectALl(){

        for (int i=0;i<filterModelArrayList.size();i++){
        filterModelArrayList.get(i).isChecked=true;}

        notifyItemRangeChanged(0,filterModelArrayList.size());

    }

    public void DeSelectALl(){

        for (int i=0;i<filterModelArrayList.size();i++){
            filterModelArrayList.get(i).isChecked=false;
        }

        notifyItemRangeChanged(0,filterModelArrayList.size());

    }


    @Override
    public int getItemCount() {
        return filterModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
      CheckBox checkbox;



        public ViewHolder(View itemView) {
            super(itemView);

            checkbox=(CheckBox)itemView.findViewById(R.id.checkbox);



        }
    }

    public interface ClickItemEvent{
        void onClickItem(String itemId);
    }




}
