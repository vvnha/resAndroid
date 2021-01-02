package com.example.dacn;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ReserAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Table> tableList;

    public ReserAdapter(Context context, int layout, List<Table> tableList) {
        this.context = context;
        this.layout = layout;
        this.tableList = tableList;
    }

    @Override
    public int getCount() {
        return tableList.size();
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
        TextView txtTable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.txtTable = convertView.findViewById(R.id.numberTable);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Table table = tableList.get(position);
        holder.txtTable.setText(table.getNumberTable()+"");

        if (table.isStatus()==true){
            holder.txtTable.setBackgroundColor(Color.argb(244, 244,164, 96));
        }else{
            holder.txtTable.setBackgroundColor(Color.argb(255, 255, 255, 255));
        }
        return convertView;
    }
}
