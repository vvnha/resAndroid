package com.example.dacn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.dacn.ui.dashboard.Detail;
import com.example.dacn.ui.dashboard.Food;
import com.example.dacn.ui.home.SpecialModel;
import com.example.dacn.ui.notifications.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchAdapter extends BaseAdapter {

    private List<Food> cat_List;
    private Context context;
    SharedPreferences sharedPreferences;
    private String urlUpdateItem = "https://restaurantqn.herokuapp.com/api/orderDetails";
    private String urlImg = "https://restaurantqn.herokuapp.com/";
    private String urlGetCart = "https://restaurantqn.herokuapp.com/api/orders/getDetail/";
    private int cartid;
    private ArrayList<Detail> details;

    public SearchAdapter(List<Food> cat_List, Context context, int cartid) {
        this.cat_List = cat_List;
        this.context = context;
        this.cartid = cartid;
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

        sharedPreferences = context.getSharedPreferences("dataLogin",context.getApplicationContext().MODE_PRIVATE);
        details = new ArrayList<>();
        TextView catName = myView.findViewById(R.id.catName);
        TextView noOfTests = myView.findViewById(R.id.no_of_tests);
        ImageView imageView = myView.findViewById(R.id.imgSpecial);
        Button btnOrder = myView.findViewById(R.id.btnOrderFood);
        Glide.with(context).load(urlImg+cat_List.get(position).getImg()).into(imageView);
        catName.setText(cat_List.get(position).getFoodName());
        noOfTests.setText(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(cat_List.get(position).getPrice()))+ " VND");

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject item = new JSONObject();
                try {
                    item.put("orderID",cartid);
                    item.put("foodID",cat_List.get(position).getFoodID());
                    item.put("qty",1);
                    item.put("price", cat_List.get(position).getPrice());
                    //show(foods.get(position).getFoodID()+"");
                    //show(cartid+"");
                    getCart(urlGetCart+cartid, item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Glide.with(parent.getContext()).load(cat_List.get(position).getImg()).into(imageView);

        return myView;
    }

    private void show (String mess){
        Toast.makeText(context.getApplicationContext(),mess,Toast.LENGTH_LONG).show();
    }
    private void getCart(String url, JSONObject item){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        //Toast.makeText(context.getApplicationContext(),header,Toast.LENGTH_LONG).show();
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("data");
                            //show(result.toString());
                            //show(cartid+"");
                            details = getItems(result, item);

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

    public ArrayList<Detail> getItems(JSONArray cart,JSONObject item){
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
                details.add(detail);
                if (detail.getFoodID() == item.getInt("foodID")) {
                    dem++;
                    updateCartItem(urlUpdateItem + "/" + detail.getDetailID(), detail);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Toast.makeText(context.getApplicationContext(), dem+"", Toast.LENGTH_LONG).show();
        if (dem == 0) {
            createCartItem(urlUpdateItem, item);
        }
        return details;
    }

    private void createCartItem(String url, JSONObject item) {
        callApi(Request.Method.POST,url,item);
    }


    private void updateCartItem(String url, Detail detail) {
        JSONObject item = new JSONObject();
        try {
            item.put("foodID", detail.getFoodID());
            item.put("orderID",detail.getOrderID());
            item.put("price", detail.getPrice());
            item.put("qty", detail.getQty()+1);
            callApi(Request.Method.PATCH,url,item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                                show("Them gio hang thanh cong");
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
