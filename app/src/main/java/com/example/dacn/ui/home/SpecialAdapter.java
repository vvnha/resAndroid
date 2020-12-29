package com.example.dacn.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dacn.R;

import java.util.List;

public class SpecialAdapter extends BaseAdapter {

    private List<SpecialModel> cat_List;

    public SpecialAdapter(List<SpecialModel> cat_List) {
        this.cat_List = cat_List;
    }

    @Override
    public int getCount() {
        return cat_List.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View myView;
        if (convertView == null){
            myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layput, parent, false);
        }else {
            myView = convertView;
        }

        TextView catName = myView.findViewById(R.id.catName);
        TextView noOfTests = myView.findViewById(R.id.no_of_tests);
        ImageView imageView = myView.findViewById(R.id.imgSpecial);
        catName.setText(cat_List.get(position).getName());
        noOfTests.setText(cat_List.get(position).getNoOfTests()+"");
        Glide.with(parent.getContext()).load(cat_List.get(position).getImg()).into(imageView);

        return myView;
    }
}
