package com.example.dacn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText edtGmail, edtPassword;
    CheckBox cbRemember;
    SharedPreferences sharedPreferences;
    String urlLogin = "https://restaurantqn.herokuapp.com/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AnhXa();
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);

        edtGmail.setText(sharedPreferences.getString("gmail",""));
        cbRemember.setChecked(sharedPreferences.getBoolean("checked", false));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gmail = edtGmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if(!gmail.isEmpty() && !password.isEmpty()){

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("email", gmail);
                        obj.put("password",password );
                        login(urlLogin, obj, gmail);
                        //Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Vui long nhap du thong tin", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void AnhXa() {
        btnLogin = findViewById(R.id.btnLogin);
        edtGmail = findViewById(R.id.edtGmail);
        edtPassword = findViewById(R.id.edtPassword);
        cbRemember = findViewById(R.id.checkboxRemember);
    }
    private void login(String url, JSONObject obj, String gmail){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject();

                            String token = response.getString("access_token").toString();
                            //Toast.makeText(getApplicationContext(), response.getString("access_token").toString(), Toast.LENGTH_LONG).show();
                            if (cbRemember.isChecked()){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("gmail", gmail);
                                editor.putString("token",token);
                                editor.putBoolean("checked", true);
                                editor.commit();

                                //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
                            }else {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("gmail");
                                editor.remove("token");
                                editor.remove("checked");
                                editor.commit();
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("checked", 1);
                            startActivity(intent);
                            //Toast.makeText(getApplicationContext(), response.getString("access_token").toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }
}