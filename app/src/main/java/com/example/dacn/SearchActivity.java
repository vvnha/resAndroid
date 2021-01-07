package com.example.dacn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.ui.dashboard.Food;
import com.example.dacn.ui.notifications.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    GridView gridSearch;
    EditText edtName;
    Button btnSearch;
    public int cartid = -1;
    List<Food> foods;
    ArrayList<Order> orderList;
    SearchAdapter adapter;
    SharedPreferences sharedPreferences;
    String urlGetdata = "https://restaurantqn.herokuapp.com/api/foods/search";
    String urlCreateCart = "https://restaurantqn.herokuapp.com/api/orders";
    String urlGetCart = "https://restaurantqn.herokuapp.com/api/users/getOrderUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        gridSearch = findViewById(R.id.cat_Grid_Search);
        edtName = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        getCart(sharedPreferences,urlGetCart);
        orderList = new ArrayList<>();
        foods = new ArrayList<>();

        show(cartid+"");
        adapter = new SearchAdapter(foods,this,sharedPreferences.getInt("cartid",-1));
        gridSearch.setAdapter(adapter);



        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //foods = new ArrayList<>();
                String foodName =  edtName.getText().toString().trim();
                if (!foodName.equals("")){
                    JSONObject item = new JSONObject();
                    try {
                        item.put("name", foodName);
                        getData(urlGetdata,item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    private void show (String mess){
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }
    private void getData(String url, JSONObject item){
        RequestQueue requestQueue = Volley.newRequestQueue(SearchActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, item,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("data");
                            for(int i =0 ; i<result.length(); i++){
                                JSONObject obj = result.getJSONObject(i);

                                foods.add(new Food(obj.getInt("foodID"),
                                        obj.getString("foodName"),
                                        obj.getString("img"),
                                        obj.getString("price"),
                                        obj.getString("ingres"),
                                        (float)obj.getInt("rating"),
                                        obj.getInt("hits"),
                                        obj.getInt("parentID")));

                            }
                            //adapter.notifyDataSetChanged();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchActivity.this, "loi", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void getCart(SharedPreferences sharedPreferences, String url) {
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        //Toast.makeText(getActivity(),url,Toast.LENGTH_LONG).show();
        //System.out.println(token);
        RequestQueue requestQueue = Volley.newRequestQueue(SearchActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("data");
                            //Toast.makeText(getActivity(), response.length()+"", Toast.LENGTH_LONG).show();
                            getItemCart(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchActivity.this,"loi",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();

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

    public void getItemCart(JSONArray orders){
        int dem = 0;
        for (int i = 0; i<orders.length(); i++){
            try {
                JSONObject order = orders.getJSONObject(i);
                if (order.getString("perNum").equals("1000")&&order.getString("service").trim().equals("-1")){
                    cartid = order.getInt("orderID");
                    dem++;
                }else{
                    Order newOrder = new Order(order.getInt("orderID"),
                            order.getInt("userID"),
                            order.getString("total"),
                            order.getString("orderDate"),
                            order.getString("perNum"),
                            order.getString("service"),
                            order.getString("dateClick"));
                    orderList.add(newOrder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(dem==0 && cartid>0){
            Calendar currentTime = Calendar.getInstance();
            // date = currentTime.get(Calendar.YEAR) +"-"+currentTime.get(Calendar.MONTH)+"-"+currentTime.get(Calendar.DATE)+" "+currentTime.get(Calendar.HOUR)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND);
            String day = "";
            if(Integer.parseInt(String.valueOf(currentTime.get(Calendar.DATE)))/10<1){
                day = "0"+currentTime.get(Calendar.DATE);
            }else {
                day = currentTime.get(Calendar.DATE)+"";
            }
            String date = currentTime.get(Calendar.YEAR) +"-"+currentTime.get(Calendar.MONTH)+1+"-"+day+" "+currentTime.get(Calendar.HOUR)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND);

            JSONObject obj = new JSONObject();
            try {
                obj.put("total",0);
                obj.put("orderDate",date);
                obj.put("perNum","1000");
                obj.put("service","-1");
                obj.put("dateClick", date);
                createCart(urlCreateCart, obj, orders);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("cartid",cartid);
            editor.commit();
//            editor.putString("ordered", orderList.toString());
//            Toast.makeText(getActivity(),orderList.toString(),Toast.LENGTH_LONG).show();
        }
    }
    private void createCart(String url, JSONObject obj, JSONArray orders){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(SearchActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SearchActivity.this, "Tạo giỏ hàng thành công", Toast.LENGTH_LONG).show();
                        getCart(sharedPreferences, urlGetdata);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
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