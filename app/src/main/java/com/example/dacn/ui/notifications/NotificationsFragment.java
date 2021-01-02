package com.example.dacn.ui.notifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import com.example.dacn.EditInfoActivity;
import com.example.dacn.LoginActivity;
import com.example.dacn.R;
import com.example.dacn.ReserActivity;
import com.example.dacn.ReserTableActivity;
import com.example.dacn.ServiceActivity;
import com.example.dacn.ui.dashboard.Detail;
import com.example.dacn.ui.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    private String urlGetCart = "https://restaurantqn.herokuapp.com/api/orders/getDetail/";
    private NotificationsViewModel notificationsViewModel;
    ListView lvCart;
    ArrayList<Detail> arrayCart;
    CartAdapter adapter;
    Button btnReser;
    User user = null;
    String urlGetUser = "https://restaurantqn.herokuapp.com/api/user";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        //final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        btnReser = root.findViewById(R.id.btnReser);
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        lvCart = root.findViewById(R.id.listCart);
        arrayCart = new ArrayList<>();
        adapter = new CartAdapter(getActivity(),R.layout.cart_item,arrayCart);
        lvCart.setAdapter(adapter);

        sharedPreferences = getActivity().getSharedPreferences("dataLogin",getContext().MODE_PRIVATE);
        getUser(urlGetUser);

        btnReser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fragmentOrder fragmentReser = new fragmentOrder();
                //fragmentTransaction.add(R.id.layout_cart,fragmentReser,"fragReser");

               // NotificationsFragment fragmentCart = (NotificationsFragment) getFragmentManager().findFragmentByTag("fragReser");

                //fragmentTransaction.add(R.id.layout_cart,fragmentReser,"fragReser");
                //fragmentTransaction.replace(R.id.layout_cart, fragmentReser);
                //fragmentTransaction.remove(R.id.layout_cart);

                //fragmentTransaction.commit();

                int totalMoney = 0;
                for (int i = 0; i<arrayCart.size();i++){
                    Detail detail = arrayCart.get(i);
                    totalMoney = totalMoney + Integer.parseInt(detail.getPrice())*detail.getQty();
                }
                //show("OK");
                Intent intent = new Intent(getActivity(), ReserTableActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", (Serializable) user);
                bundle.putInt("type",1);
                bundle.putInt("totalMoney",totalMoney);
                intent.putExtra("data",bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        int cartid = sharedPreferences.getInt("cartid",-1);
//        String orderList = sharedPreferences.getString("listOrder","");
//        if(!orderList.equals("")){
//            Toast.makeText(getActivity(), orderList+"",Toast.LENGTH_SHORT).show();
//            JSONArray jsonArray = new JSONArray(orderList);
//        }else{
//            Toast.makeText(getActivity(), "qqqq",Toast.LENGTH_SHORT).show();
//        }
        if (cartid>0){
            getCart(urlGetCart+ cartid);
        }else {
            Toast.makeText(getActivity(), "Moi dang nhap",Toast.LENGTH_SHORT).show();
        }

        return root;
    }


    private void show(String mess){
        Toast.makeText(getActivity(), mess,Toast.LENGTH_SHORT).show();
    }

    private void getCart(String url){
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
                            //details = getItems(result, item);

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
                arrayCart.add(detail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void getUser(String url){
        JSONObject userCurrent = null;
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            User userCurrent = new User(
                                    response.getInt("id"),
                                    response.getString("name"),
                                    response.getString("email"),
                                    response.getString("phone"),
                                    response.getInt("positionID"));
                            user = userCurrent;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userInfo",response.toString());
                        editor.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"loi",Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(getContext(), LoginActivity.class);
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