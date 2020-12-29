package com.example.dacn.ui.ordered;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.R;
import com.example.dacn.ui.notifications.Order;
import com.example.dacn.ui.user.UserViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OrderedFragment extends Fragment {
    private OrderedViewModel orderedViewModel;
    Button btnAddOrder, btnAddCancel;
    JSONArray orderList;
    JSONArray orderCancelList;

    String urlGetCart = "https://restaurantqn.herokuapp.com/api/users/getOrderUser";
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderedViewModel =
                new ViewModelProvider(this).get(OrderedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ordered, container, false);
//        final TextView textView = root.findViewById(R.id.text_user);
//        orderedViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        sharedPreferences = getActivity().getSharedPreferences("dataLogin", getContext().MODE_PRIVATE);
        orderList = new JSONArray();
        orderCancelList = new JSONArray();

        btnAddOrder = root.findViewById(R.id.btnAddA);
        btnAddCancel = root.findViewById(R.id.btnAddB);
        btnAddOrder.setOnClickListener(this::AddFragment);
        btnAddOrder.performClick();
        btnAddCancel.setOnClickListener(this::AddFragment);

        //getCart(sharedPreferences, urlGetCart);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        //FragmentA fragmentA = new FragmentA();
//
//        //fragmentTransaction.add(R.id.frameContent,fragmentA);
//       // fragmentTransaction.commit();
//
//        btnAddOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentA fragmentA = new FragmentA();
//                fragmentTransaction.add(R.id.frameContent,fragmentA);
//
//                fragmentTransaction.commit();
//            }
//        });
//        btnAddCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentB fragmentB = new FragmentB();
//                fragmentTransaction.add(R.id.frameContent,fragmentB);
//                fragmentTransaction.commit();
//            }
//        });


        return root;
    }

    private void show(String mess){
        Toast.makeText(getActivity(),mess,Toast.LENGTH_LONG).show();
    }

    public void AddFragment(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        String param = "";
        switch (view.getId()){
            case R.id.btnAddA:
                fragment = new FragmentA();
                param = orderList.toString();
//                show(orderList.length()+"");
                break;
            case R.id.btnAddB:
                fragment = new FragmentB();
                param = orderCancelList.toString();
//                show(orderCancelList.length()+"");
                break;
        }
//        Bundle bundle = new Bundle();
//        bundle.putString("orderList",param);
//        fragment.setArguments(bundle);



        fragmentTransaction.replace(R.id.frameContent,fragment);
        fragmentTransaction.commit();
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
                            //Toast.makeText(getActivity(), response+"", Toast.LENGTH_LONG).show();
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
                    dem++;
                }else{
                    Order newOrder = new Order(order.getInt("orderID"),
                            order.getInt("userID"),
                            order.getString("total"),
                            order.getString("orderDate"),
                            order.getString("perNum"),
                            order.getString("service"),
                            order.getString("dateClick"));
                    if(Integer.parseInt(newOrder.getService())!= 3){
                        orderList.put(order);
                    }else{
                        orderCancelList.put(order);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(dem==0){
           show("chua co gio hang nao");
        }else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("listOrder",orderList.toString());
            editor.putString("listOrderCancel",orderCancelList.toString());
            editor.commit();
        }
    }


}
