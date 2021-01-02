package com.example.dacn.ui.ordered;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.R;
import com.example.dacn.ui.dashboard.Detail;
import com.example.dacn.ui.notifications.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FragmentB extends Fragment {

    SharedPreferences sharedPreferences;
    ListView lvOrder, lvOrdered;
    ArrayList<Order> arrayOrder;
    CancelAdapter adapter;
    ArrayList<Detail> arrayOrdered;
    OrderedItemAdapter orderedItemAdapter;
    private String urlGetOrdered = "https://restaurantqn.herokuapp.com/api/orders/getDetail/";
    String urlGetCart = "https://restaurantqn.herokuapp.com/api/users/getOrderUser";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b,container,false);

        sharedPreferences = getActivity().getSharedPreferences("dataLogin", getContext().MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");

        lvOrder = view.findViewById(R.id.orderedList);
        arrayOrder = new ArrayList<>();
        adapter = new CancelAdapter(getContext(),R.layout.order_item_cancel,arrayOrder);
        lvOrder.setAdapter(adapter);
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogDetail(position);
            }
        });

        getCart(sharedPreferences,urlGetCart);
        //Bundle bundle = getArguments();
        if (!token.equals("")){
            String data = sharedPreferences.getString("listOrderCancel","");
            try {
                JSONArray orderList = new JSONArray(data);

                //show(orderList+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            show("Trong");
        }

        //Toast.makeText(getActivity(), )
        return view;
    }
    private void show(String mess){
        Toast.makeText(getActivity(),mess,Toast.LENGTH_SHORT).show();
    }
    private void getOrderItem(JSONArray orders){
        int dem = 0;
        for (int i = 0; i<orders.length(); i++){
            try {
                JSONObject order = orders.getJSONObject(i);

                Order newOrder = new Order(order.getInt("orderID"),
                        order.getInt("userID"),
                        order.getString("total"),
                        order.getString("orderDate"),
                        order.getString("perNum"),
                        order.getString("service"),
                        order.getString("dateClick"));
                arrayOrder.add(newOrder);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
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
                    if(Integer.parseInt(newOrder.getService())== 3){
                        arrayOrder.add(newOrder);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.reverse(arrayOrder);
        adapter.notifyDataSetChanged();
    }
    private  void  dialogDetail(int position){
        Order order = arrayOrder.get(position);
        Dialog dialog = new Dialog((getActivity()));
        dialog.setContentView(R.layout.dialog_ordered);
        lvOrdered = dialog.findViewById(R.id.orderedItem);
        arrayOrdered = new ArrayList<>();
        orderedItemAdapter = new OrderedItemAdapter(getContext(), R.layout.ordered_item, arrayOrdered);
        lvOrdered.setAdapter(orderedItemAdapter);
        getDetail(urlGetOrdered +order.getOrderID());

        Button closeDialog = dialog.findViewById(R.id.btnCloseDialog);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void getDetail(String url){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        //Toast.makeText(context.getApplicationContext(),header,Toast.LENGTH_LONG).show();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
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
