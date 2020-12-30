package com.example.dacn.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.dacn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodAdapter extends PagerAdapter {
    private List<Food> foods;
    private LayoutInflater layoutInflater;
    SharedPreferences sharedPreferences;
    private Context context;
    private int cartid;
    private String urlGetCart = "https://restaurantqn.herokuapp.com/api/orders/getDetail/";
    private String urlCreateItem = "https://restaurantqn.herokuapp.com/api/orders/getDetail/";
    private String urlUpdateItem = "https://restaurantqn.herokuapp.com/api/orderDetails";
    private String urlImg = "https://restaurantqn.herokuapp.com/";

    private ArrayList<Detail> details;

    public FoodAdapter(List<Food> foods, Context context, int cartid) {
        this.foods = foods;
        this.context = context;
        this.cartid = cartid;
    }


    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    private class ViewHolder{
        Button btnOrder;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewHolder holder = new ViewHolder();
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item, container, false);
        sharedPreferences = context.getSharedPreferences("dataLogin",context.getApplicationContext().MODE_PRIVATE);
        details = new ArrayList<>();

        ImageView imageView;
        TextView title, desc, price;
        Button btnOrder;

        imageView = view.findViewById(R.id.image);
        title = view.findViewById(R.id.title);
        desc = view.findViewById(R.id.desc);
        price = view.findViewById(R.id.price);
        btnOrder = view.findViewById(R.id.btnOrder);
        //holder.btnOrder = view.findViewById(R.id.btnOrder);


        //imageView.setImageResource(R.drawable.brochure);
        Glide.with(context).load(urlImg+foods.get(position).getImg()).into(imageView);
        title.setText(foods.get(position).getFoodName() );
        desc.setText(foods.get(position).getIngres());
        price.setText(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(foods.get(position).getPrice()))+ " VND");


        //getCart(urlGetCart+cartid);
        //show(details.size()+"");
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject item = new JSONObject();
                try {
                    item.put("orderID",cartid);
                    item.put("foodID",foods.get(position).getFoodID());
                    item.put("qty",1);
                    item.put("price", foods.get(position).getPrice());
                    //show(foods.get(position).getFoodID()+"");
                    //show(cartid+"");
                    getCart(urlGetCart+cartid, item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
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
            show(dem+"");
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
                    public void onResponse(JSONObject response) { show(response.toString());
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
