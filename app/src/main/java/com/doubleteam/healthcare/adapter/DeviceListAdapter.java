package com.doubleteam.healthcare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import  com.doubleteam.healthcare.R;
import com.doubleteam.healthcare.model.DeviceListItems;

import java.util.Collections;
import java.util.List;

/**
 * Created by bsh on 12/14/2015.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.MyViewHolder> {

    List<DeviceListItems> data= Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public DeviceListAdapter(Context _context,List<DeviceListItems> _data){
        this.context=_context;
        inflater=LayoutInflater.from(this.context);
        this.data=_data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.devicelist_drawer_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DeviceListAdapter.MyViewHolder holder, int position) {
        DeviceListItems current=data.get(position);
        holder.DeviceName.setText(current.gethDeviceName());
        holder.DeviceAddress.setText(current.gethDeviceAddress());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView DeviceName;
        TextView DeviceAddress;
        public MyViewHolder(View itemView) {
            super(itemView);
            DeviceName= (TextView) itemView.findViewById(R.id.device_name);
            DeviceAddress= (TextView) itemView.findViewById(R.id.device_address);
        }
    }
}


