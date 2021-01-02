package com.example.dacn.ui.ordered;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.R;
import com.example.dacn.ServiceActivity;
import com.example.dacn.ui.dashboard.Detail;
import com.example.dacn.ui.notifications.Order;
import com.example.dacn.ui.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Order> orderList;
    SharedPreferences sharedPreferences;
    ArrayList<Order> arrayOrder;
    OrderAdapter adapter;
    ArrayList<Detail> arrayOrdered;
    String info;
    User user;
    ListView lvOrder, lvOrdered;
    OrderedItemAdapter orderedItemAdapter;
    private String urlGetOrdered = "https://restaurantqn.herokuapp.com/api/orders/getDetail/";

    String urlGetOrder = "https://restaurantqn.herokuapp.com/api/orders";

    public OrderAdapter(Context context, int layout, ArrayList<Order> orderList) {
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
        TextView orderDate, orderPrice, orderStatus, orderDetail;
        Button btnOrderCancel;
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
            holder.orderStatus = convertView.findViewById(R.id.orderStatus);
            holder.btnOrderCancel = convertView.findViewById(R.id.btnOrderCancel);
            holder.orderDetail = convertView.findViewById(R.id.detail);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        info = sharedPreferences.getString("userInfo","");
        JSONObject userInfo = null;
        try {
            userInfo = new JSONObject(info);
            user = new User(
                    userInfo.getInt("id"),
                    userInfo.getString("name"),
                    userInfo.getString("email"),
                    userInfo.getString("phone"),
                    userInfo.getInt("positionID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Order order = orderList.get(position);
        holder.orderDate.setText(order.getOrderDate());
        holder.orderPrice.setText("Total: "+NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(order.getTotal()))+" VND");
        holder.orderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetail(position, order.getOrderID());
            }
        });

        String txtStatus;
        switch (Integer.parseInt(order.getService())){
            case 0:
                txtStatus = "Chua xac nhan";
                break;
            case 1:
                txtStatus = "Da xac nhan";
                break;
            case 2:
                txtStatus="Da thanh toan";
                break;
            case 3:
                txtStatus="Da huy";
                break;
            default:
                txtStatus = "Da huy";
        }
        holder.orderStatus.setText(txtStatus);

        holder.btnOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(order);
            }
        });

        return convertView;
    }

    private void show (String mess){
        Toast.makeText(context.getApplicationContext(),mess,Toast.LENGTH_LONG).show();
    }

    private void submit(Order order){
        String totalMoney = order.getTotal();
        String perNum = order.getPerNum();
        String orderDate = order.getOrderDate();
        String service = "3";
        JSONObject item = new JSONObject();
        try {
            item.put("total", totalMoney);
            item.put("perNum", perNum);
            item.put("orderDate",orderDate);
            item.put("service", service);
            //show(item.toString());
            callApi(Request.Method.PATCH,urlGetOrder +"/"+ order.getOrderID(),item);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void callApi(int method, String url, JSONObject item){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, item,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("code").equals("200")||response.getString("code").equals("201")){
                                show("Sua don hang thanh cong");
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

    private  void  dialogDetail(int position, int orderID){
        //Order order = arrayOrder.get(position);

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_ordered);

        lvOrdered = dialog.findViewById(R.id.orderedItem);
        arrayOrdered = new ArrayList<>();
        orderedItemAdapter = new OrderedItemAdapter(context, R.layout.ordered_item, arrayOrdered);
        lvOrdered.setAdapter(orderedItemAdapter);
        getDetail(urlGetOrdered +orderList.get(position).getOrderID());

        Button closeDialog = dialog.findViewById(R.id.btnCloseDialog);
        Button viewOrder = dialog.findViewById(R.id.btnViewOrder);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context.getApplicationContext(), ServiceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", (Serializable) user);
                bundle.putInt("type",2);
                bundle.putInt("totalMoney",0);
                bundle.putInt("orderID", orderID);
                intent.putExtra("data",bundle);
                context.startActivity(intent);
            }
        });
        dialog.show();
    }
    private void getDetail(String url){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        //Toast.makeText(context.getApplicationContext(),header,Toast.LENGTH_LONG).show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("data");
                            getItems(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
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

    public void getItems(JSONArray cart){
        int dem =0;
        for (int i = 0; i < cart.length(); i++) {
            try {
                JSONObject cartItem = cart.getJSONObject(i);
                Detail detail = new Detail(cartItem.getInt("detailID"),
                        cartItem.getInt("orderID"),
                        cartItem.getInt("foodID"),
                        cartItem.getInt("qty"),
                        cartItem.getString("price"),
                        cartItem.getString("foodName"));
                arrayOrdered.add(detail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        orderedItemAdapter.notifyDataSetChanged();
    }

}
