package com.example.dacn.ui.user;

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
import androidx.fragment.app.Fragment;
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
import com.example.dacn.ServiceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class UserFragment extends Fragment {
    private UserViewModel userViewModel;
    private int cartid;
    User user = null;
    String info;
    String urlGetUser = "https://restaurantqn.herokuapp.com/api/user";
    SharedPreferences sharedPreferences;
    TextView txtName, txtMail, txtPhone, txtPosi;
    Button btnService, btnEdit, btnLogOut;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);
        View root = null;
//        final TextView textView = root.findViewById(R.id.text_user);
//        userViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        sharedPreferences = getContext().getSharedPreferences("dataLogin",getActivity().MODE_PRIVATE);
        getUser(urlGetUser);
        info = sharedPreferences.getString("userInfo","");
        if (!info.equals("")){
            try {
                JSONObject userInfo = new JSONObject(info);
                User user = new User(
                        userInfo.getInt("id"),
                        userInfo.getString("name"),
                        userInfo.getString("email"),
                        userInfo.getString("phone"),
                        userInfo.getInt("positionID"));
                if (user.getPositionID() == 3 ){
                    root = inflater.inflate(R.layout.fragment_staff, container, false);
                    btnService = root.findViewById(R.id.btnService);
                    btnService.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ServiceActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", (Serializable) user);
                            bundle.putInt("type",2);
                            bundle.putInt("totalMoney",0);
                            bundle.putInt("orderID", 0);
                            intent.putExtra("data",bundle);
                            startActivity(intent);
                        }
                    });
                }else{
                    root = inflater.inflate(R.layout.fragment_user, container, false);
                }
                AnhXa(root);
                txtName.setText(user.getName());
                txtMail.setText(user.getMail());
                txtPhone.setText(user.getPhone());
                String posi = "";
                switch (user.getPositionID()){
                    case 1: posi = "admin";
                    break;
                    case 2: posi = "manager";
                    break;
                    case 3: posi = "staff";
                    break;
                    case 4: posi = "user";
                    break;
                    default: posi = "user";
                }
                txtPosi.setText(posi);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            root = inflater.inflate(R.layout.fragment_user, container, false);
        }
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditInfoActivity.class);
                startActivity(intent);
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.commit();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
    private void show(String mess){
        Toast.makeText(getActivity(), mess,Toast.LENGTH_SHORT).show();
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
    private void AnhXa(View view){
        txtName = view.findViewById(R.id.txtUserName);
        txtMail = view.findViewById(R.id.txtUserMail);
        txtPhone = view.findViewById(R.id.txtUserPhone);
        txtPosi = view.findViewById(R.id.txtUserPosi);
        btnEdit = view.findViewById(R.id.btnEditInfo);
        btnLogOut = view.findViewById(R.id.btnLogOut);
    }
}
