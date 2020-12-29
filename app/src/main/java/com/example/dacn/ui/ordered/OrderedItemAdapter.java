package com.example.dacn.ui.ordered;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dacn.R;
import com.example.dacn.ui.dashboard.Detail;
import com.example.dacn.ui.notifications.Order;

import java.util.ArrayList;

public class OrderedItemAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Detail> detaiList;

    public OrderedItemAdapter(Context context, int layout, ArrayList<Detail> detaiList) {
        this.context = context;
        this.layout = layout;
        this.detaiList = detaiList;
    }

    @Override
    public int getCount() {
        return detaiList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        TextView foodName, qty, total;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.foodName = convertView.findViewById(R.id.txtFoodnameDialog);
            holder.qty = convertView.findViewById(R.id.txtQtyDialog);
            holder.total = convertView.findViewById(R.id.txtTotalDialog);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Detail detail = detaiList.get(position);
        holder.foodName.setText(detail.getFoodName());
        holder.qty.setText("so luong: " + detail.getQty());
        holder.total.setText(Integer.parseInt(detail.getPrice())*detail.getQty()+" VND");
        return convertView;
    }
}
