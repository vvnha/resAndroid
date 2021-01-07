package com.example.dacn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.ui.notifications.NotificationsFragment;
import com.example.dacn.ui.notifications.Order;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String urlGetdata = "https://restaurantqn.herokuapp.com/api/users/getOrderUser";
    String urlCreateCart = "https://restaurantqn.herokuapp.com/api/orders";
    String urlGetUser = "https://restaurantqn.herokuapp.com/api/user";
    JSONArray orders;
    ArrayList<Order> orderList;
    JSONArray listOrder;
    int cartid = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orders = new JSONArray();
        orderList = new ArrayList<>();
        listOrder = new JSONArray();
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        Intent intent = getIntent();
        int checkLoginAfterLogin = intent.getIntExtra("checked",-1);
        //Toast.makeText(getApplicationContext(),checkLoginAfterLogin+"te",Toast.LENGTH_LONG).show();
        if(checkLoginRemember()<0 && checkLoginAfterLogin <0 || sharedPreferences.getString("token","").trim().isEmpty()){
            Intent intent1 = new Intent(this, LoginActivity.class);
            startActivity(intent1);
        }else {
            sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
            //Toast.makeText(getApplicationContext(),"te",Toast.LENGTH_LONG).show();
            getUser(urlGetUser);
            getCart(sharedPreferences, urlGetdata);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_ordered ,R.id.navigation_user)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
    public  int checkLoginRemember(){
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("checked", false);
        if (check) return 1;
        else return -1;
    }

    private void getCart(SharedPreferences sharedPreferences, String url) {
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        //Toast.makeText(getApplicationContext(),header,Toast.LENGTH_LONG).show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("data");
                            getItemCart(result);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"loi",Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent1);
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

//        Calendar currentTime = Calendar.getInstance();
//        String date = currentTime.get(Calendar.YEAR) +":"+currentTime.get(Calendar.MONTH)+":"+currentTime.get(Calendar.DATE)+" "+currentTime.get(Calendar.HOUR)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND);
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
                    listOrder.put(order);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        Toast.makeText(getApplicationContext(), cartid+"",Toast.LENGTH_SHORT).show();
        if(dem==0){
            Calendar currentTime = Calendar.getInstance();
            //String date = currentTime.get(Calendar.YEAR) +"-"+currentTime.get(Calendar.MONTH)+1+"-"+currentTime.get(Calendar.DATE)+" "+currentTime.get(Calendar.HOUR)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND);
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
            editor.putString("listOrder",listOrder.toString());
            editor.commit();
//            Toast.makeText(getApplicationContext(), listOrder.toString()+"",Toast.LENGTH_SHORT).show();
//            NotificationsFragment fragmentOrder = new NotificationsFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt("orderLength",orderList.size());
//            fragmentOrder.setArguments(bundle);
//            editor.putString("ordered", orderList.toString());
//            Toast.makeText(getActivity(),orderList.toString(),Toast.LENGTH_LONG).show();
        }
    }
    private void createCart(String url, JSONObject obj, JSONArray orders){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.d("cart", obj.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Tạo giỏ hàng thành công", Toast.LENGTH_LONG).show();
                        //getItemCart(orders);
                        getCart(sharedPreferences, urlGetdata);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
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

    private void getUser(String url){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userInfo",response.toString());
                        editor.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"loi",Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent1);
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