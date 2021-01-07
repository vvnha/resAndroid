package com.example.dacn.ui.dashboard;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.R;
import com.example.dacn.SearchActivity;
import com.example.dacn.ui.notifications.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    String urlGetdata = "https://restaurantqn.herokuapp.com/api/foods/getFoods";
    String urlCreateCart = "https://restaurantqn.herokuapp.com/api/orders";
    String urlGetCart = "https://restaurantqn.herokuapp.com/api/users/getOrderUser";
    ViewPager viewPager;
    CardView searchView;
    List<Food> foods;
    FoodAdapter foodAdapter;
    Integer[] colors = null;
    JSONArray orders;
    ArrayList<Order> orderList;
    public int cartid = -1;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    SharedPreferences sharedPreferences;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //final TextView textView = root.findViewById(R.id.text_dashboard);
        sharedPreferences = getActivity().getSharedPreferences("dataLogin", getContext().MODE_PRIVATE);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
              //  textView.setText(s);
            }
        });

        foods = new ArrayList<>();
        orderList = new ArrayList<>();
        getData(urlGetdata);

        //Toast.makeText(getActivity(),orderList.size()+"",Toast.LENGTH_LONG).show();
        foodAdapter = new FoodAdapter(foods,getActivity(), sharedPreferences.getInt("cartid",-1));


        viewPager = root.findViewById(R.id.viewPager);
        searchView = root.findViewById(R.id.cardSearch);

        viewPager.setAdapter(foodAdapter);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < (foodAdapter.getCount() - 1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(
                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
    private void getData(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
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
                            foodAdapter.notifyDataSetChanged();
                            //Toast.makeText(getActivity(),String.valueOf(foods.size()), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "loi", Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                Toast.makeText(getActivity(),"loi",Toast.LENGTH_LONG).show();
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
            //String date = currentTime.get(Calendar.YEAR) +"-"+currentTime.get(Calendar.MONTH)+"-"+currentTime.get(Calendar.DATE)+" "+currentTime.get(Calendar.HOUR)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getActivity(), "Tạo giỏ hàng thành công", Toast.LENGTH_LONG).show();
                        getCart(sharedPreferences, urlGetdata);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
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