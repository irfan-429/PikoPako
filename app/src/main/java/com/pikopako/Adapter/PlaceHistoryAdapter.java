package com.pikopako.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.LocalStorage.DatabaseHandler;
import com.pikopako.Model.AddressModel;
import com.pikopako.R;

import java.util.ArrayList;



public class PlaceHistoryAdapter extends RecyclerView.Adapter<PlaceHistoryAdapter.ViewHolder> {
    ArrayList<AddressModel> addressList;
    OnSelectAddress onSelectAddress;
    DatabaseHandler databaseHandler;
    Context context;

    public PlaceHistoryAdapter(Context context, ArrayList<AddressModel> addressList, OnSelectAddress onSelectAddress) {
        this.addressList = addressList;
        this.onSelectAddress = onSelectAddress;
        this.context = context;
        databaseHandler = new DatabaseHandler(context);

    }

    @Override
    public PlaceHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_place_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceHistoryAdapter.ViewHolder holder, int position) {
        final AddressModel addressModel = addressList.get(position);
        holder.tvAddress.setText(addressModel.getAddress());
        holder.removeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressList.remove(addressModel);
                notifyDataSetChanged();
                databaseHandler.deleteAddress(addressModel);
            }
        });
        holder.tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectAddress.selectAddress(addressModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewBold tvAddress;
        ImageView removeIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAddress = (CustomTextViewBold) itemView.findViewById(R.id.location);
            removeIcon = (ImageView) itemView.findViewById(R.id.removeIcon);
        }
    }

    public interface OnSelectAddress {
        void selectAddress(AddressModel address);
    }
}
