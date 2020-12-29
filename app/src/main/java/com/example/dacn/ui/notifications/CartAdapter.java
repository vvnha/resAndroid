package com.example.dacn.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.R;
import com.example.dacn.ui.dashboard.Detail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends BaseAdapter {

    SharedPreferences sharedPreferences;
    private Context context;
    private int layout;
    private  List<Detail> cartList;
    private String urlUpdateItem = "https://restaurantqn.herokuapp.com/api/orderDetails";

    public CartAdapter(Context context, int layout, List<Detail> cartList) {
        this.context = context;
        this.layout = layout;
        this.cartList = cartList;
    }

    @Override
    public int getCount() {
        return cartList.size();
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
        TextView txtFoodName, txtPrice, txtAdd, txtRemove, txtTotal;
        EditText edtQty;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        sharedPreferences = context.getSharedPreferences("dataLogin",context.MODE_PRIVATE);
        int cartid = sharedPreferences.getInt("cartid",-1);
        if (convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.txtFoodName = convertView.findViewById(R.id.txtFoodName);
            holder.txtPrice = convertView.findViewById(R.id.txtPrice);
            holder.txtAdd = convertView.findViewById(R.id.txtAdd);
            holder.txtRemove = convertView.findViewById(R.id.txtRemove);
            holder.txtTotal = convertView.findViewById(R.id.txtTotal);
            holder.edtQty = convertView.findViewById(R.id.edtQty);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Detail detail = cartList.get(position);
        holder.txtFoodName.setText(detail.getFoodName());
        holder.txtPrice.setText(detail.getPrice()+" VND");
        holder.edtQty.setText(detail.getQty()+"");
        holder.txtTotal.setText(detail.getQty()*Integer.parseInt(detail.getPrice())+" VND");

        holder.txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty =Integer.parseInt(holder.edtQty.getText().toString().trim());
                qty++;
                //int price = Integer.parseInt(holder.txtPrice.getText().toString().trim());
                holder.edtQty.setText(qty+"");
                //show(qty+"");
                holder.txtTotal.setText(qty*Integer.parseInt(detail.getPrice())+" VND");
                updateCartItem(detail,qty,position);
            }
        });
        holder.txtRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty =Integer.parseInt(holder.edtQty.getText().toString().trim());
                //int price = Integer.parseInt(holder.txtPrice.getText().toString().trim());
                 if (qty<=0){
                    holder.edtQty.setText(0+"");
                 }else{
                     qty--;
                     holder.edtQty.setText(qty+"");
                 }
                holder.txtTotal.setText(qty*Integer.parseInt(detail.getPrice())+" VND");
                updateCartItem(detail,qty,position);

            }
        });

        return convertView;
    }
    private void show (String mess){
        Toast.makeText(context.getApplicationContext(),mess,Toast.LENGTH_LONG).show();
    }
    private void updateCartItem(Detail detail, int qty, int position){
        if(qty>0){
            JSONObject item = newItem(detail,qty);
            callApi(Request.Method.PATCH,urlUpdateItem + "/" + detail.getDetailID(),item);
        }else{
            callApi(Request.Method.DELETE, urlUpdateItem + "/" + detail.getDetailID(), null);
            cartList.remove(position);
        }
    }
    private JSONObject newItem(Detail detail, int qty){
        JSONObject item = new JSONObject();
        try {
            item.put("orderID",detail.getOrderID());
            item.put("foodID",detail.getFoodID());
            item.put("qty",qty);
            item.put("price", detail.getPrice());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }
    private void callApi(int method, String url, JSONObject item){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        //Toast.makeText(context.getApplicationContext(),url+"",Toast.LENGTH_LONG).show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, item,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("code").equals("200")||response.getString("code").equals("201")){
                                show("Sua gio hang thanh cong");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context.getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", header);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
