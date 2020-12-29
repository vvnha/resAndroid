package com.example.dacn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.ui.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditInfoActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String info;
    EditText edtName, edtMail, edtPhone;
    Button btnAgree, btnCancel;
    String urlGetUser = "https://restaurantqn.herokuapp.com/api/user";
    String urlUpdateUser = "https://restaurantqn.herokuapp.com/api/user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);
        getUser(urlGetUser);
        AnhXa();
        User user = null;
        info = sharedPreferences.getString("userInfo","");
        if (!info.equals("")){
            JSONObject userInfo = null;
            try {
                userInfo = new JSONObject(info);
                user = new User(
                        userInfo.getInt("id"),
                        userInfo.getString("name"),
                        userInfo.getString("email"),
                        userInfo.getString("phone"),
                        userInfo.getInt("positionID"));
                edtName.setText(user.getName());
                edtPhone.setText(user.getPhone());
                edtMail.setText(user.getMail());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        User finalUser = user;
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String mail = edtMail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                if (!name.equals("") && mail!=null && phone!=null){
                    finalUser.setName(name);
                    finalUser.setMail(mail);
                    finalUser.setPhone(phone);
                    submit(finalUser);
                    edtName.setText(finalUser.getName());
                    edtPhone.setText(finalUser.getPhone());
                    edtMail.setText(finalUser.getMail());
                    finish();
                }else {
                    show("vui long nhap day du");
                }
               //submit(finalUser);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void show(String mess){
        Toast.makeText(this, mess,Toast.LENGTH_SHORT).show();
    }

    private void submit(User user){
        JSONObject item = new JSONObject();
        try {
            item.put("name",user.getName());
            item.put("email",user.getMail());
            item.put("phone",user.getPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //show(user.getId()+"");
        //show(item.toString());
        callApi(Request.Method.PATCH, urlUpdateUser+user.getId(),item);
    }

    private void getUser(String url){
        JSONObject userCurrent = null;
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //show(response+"");
                            User userCurrent = new User(
                                    response.getInt("id"),
                                    response.getString("name"),
                                    response.getString("email"),
                                    response.getString("phone"),
                                    response.getInt("positionID"));
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
                Toast.makeText(EditInfoActivity.this,"loi",Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(EditInfoActivity.this, LoginActivity.class);
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
    private void AnhXa(){
        edtName = findViewById(R.id.edtName);
        edtMail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnAgree = findViewById(R.id.btnAgree);
        btnCancel = findViewById(R.id.btnCancel);
    }
    private void callApi(int method, String url, JSONObject item){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(EditInfoActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, item,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //show(response.toString());
                        try {
                            if(response.getString("code").equals("200")||response.getString("code").equals("201")){
                                show("Sua nguoi dung thanh cong");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditInfoActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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