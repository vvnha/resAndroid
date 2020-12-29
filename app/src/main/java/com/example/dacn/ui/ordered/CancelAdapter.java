package com.example.dacn.ui.ordered;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.dacn.R;
import com.example.dacn.ui.notifications.Order;

import java.util.List;

public class CancelAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Order> orderList;
    SharedPreferences sharedPreferences;

    public CancelAdapter (Context context, int layout, List<Order> orderList) {
        this.context = context;
        this.layout = layout;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
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
        TextView orderDate, orderPrice, orderStatus;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        sharedPreferences = context.getSharedPreferences("dataLogin", context.MODE_PRIVATE);
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.orderDate = convertView.findViewById(R.id.txtDate);
            holder.orderPrice = convertView.findViewById(R.id.orderPrice);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Order order = orderList.get(position);
        holder.orderDate.setText(order.getOrderDate());
        holder.orderPrice.setText(order.getTotal()+ " VND");

        return convertView;
    }


}
