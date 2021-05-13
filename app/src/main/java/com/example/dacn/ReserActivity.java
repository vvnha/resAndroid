package com.example.dacn;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.ui.notifications.NotificationsFragment;
import com.example.dacn.ui.notifications.Order;
import com.example.dacn.ui.user.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReserActivity extends AppCompatActivity {

    Button btnAgree, btnCancel;
    SharedPreferences sharedPreferences;
    String info;
    ArrayList<Order> orderList;
    Spinner spinnerNumPeople;
    //DatePickerDialog datepicker;
    JSONArray listOrder;
    String urlCreateCart = "https://restaurantqn.herokuapp.com/api/orders";
    String urlGetdata = "https://restaurantqn.herokuapp.com/api/users/getOrderUser";
    int cartid = -1;
    String table = "", date = "", time ="";
    int totalMoney = 0;
    EditText edtDate, edtTime, edtName, edtMail, edtPhone ;
    //DatePicker datePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reser);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        AnhXa();
        //edtName.setText("abc");

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("dataTotal");
        totalMoney = bundle.getInt("totalMoney");
        table = bundle.getString("table");
        date = bundle.getString("date");
        time = bundle.getString("time");

        ///Log.d("table", table);


       sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);
       cartid = sharedPreferences.getInt("cartid",-1);
        orderList = new ArrayList<>();
        listOrder = new JSONArray();

        ArrayList<String> arrayNumpeople = new ArrayList<String>();
        int n = 5;
        for (int i = 1; i< n; i ++){
            arrayNumpeople.add(i+" person");
        }
        arrayNumpeople.add(n+"+ person");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrayNumpeople);
        spinnerNumPeople.setAdapter(arrayAdapter);

        spinnerNumPeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //show((position+1)+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //show(1+"");
            }
        });

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
                edtName.setText(user.getName());
                edtMail.setText(user.getMail());
                edtPhone.setText(user.getPhone());
                edtDate.setText(date);
                edtTime.setText(time);
//                edtName.setEnabled(false);
//                edtMail.setEnabled(false);
//                edtPhone.setEnabled(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDate();
            }
        });
        //edtTime.setEnabled(false);
        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTime();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!info.equals("")){
                    //show(info);
                    try {
                        JSONObject userInfo = new JSONObject(info);
                        User user = new User(
                                userInfo.getInt("id"),
                                userInfo.getString("name"),
                                userInfo.getString("email"),
                                userInfo.getString("phone"),
                                userInfo.getInt("positionID"));
                        submit(totalMoney, cartid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    private void AnhXa(){
        btnAgree = findViewById(R.id.btnAgree);
        btnCancel = findViewById(R.id.btnCancel);
        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);
        spinnerNumPeople = findViewById(R.id.spinner);
        edtName = findViewById(R.id.edtName);
        edtMail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        //datePicker = findViewById(R.id.datePicker);
    }
    private void show(String mess){
        Toast.makeText(getApplicationContext(),mess,Toast.LENGTH_LONG).show();
    }
    private void datePickerChange(DatePicker datePicker, int year, int month, int dayOfMonth) {
        //Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
        edtDate.setText(year +"-" + (month + 1) + "-" + dayOfMonth);
    }

    private void timePickerChange(TimePicker timePicker, int hour, int minute) {
        //Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
        edtTime.setText((hour +1)+":" + minute + ":00" );
    }

    private void dialogDate(){
        Calendar currentTime = Calendar.getInstance();
        int day = currentTime.get(Calendar.DAY_OF_MONTH);
        int month = currentTime.get(Calendar.MONTH);
        int year = currentTime.get(Calendar.YEAR);
        String datetime = currentTime.get(Calendar.YEAR) +":"+currentTime.get(Calendar.MONTH)+":"+currentTime.get(Calendar.DATE)+" "+currentTime.get(Calendar.HOUR)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_date);
        DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        Button btnCancelDialog = dialog.findViewById(R.id.btnCancelDialog);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datePickerChange(  datePicker,   year,   month,   dayOfMonth);
            }
        });

        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dialogTime(){
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR);
        int minute = currentTime.get(Calendar.MINUTE);
        String datetime = currentTime.get(Calendar.YEAR) +":"+currentTime.get(Calendar.MONTH)+":"+currentTime.get(Calendar.DATE)+" "+currentTime.get(Calendar.HOUR)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_time);
        TimePicker timePicker = dialog.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        Button btnCancelDialog = dialog.findViewById(R.id.btnCancelDialog);
//        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
//            @Override
//            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                datePickerChange(  datePicker,   year,   month,   dayOfMonth);
//            }
//        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timePickerChange(  timePicker,  hourOfDay, minute);
                //show(hourOfDay+"");
            }
        });



        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
//        show(orders.toString());
//        Toast.makeText(getApplicationContext(), cartid+"",Toast.LENGTH_SHORT).show();
        if(dem==0){
            Calendar currentTime = Calendar.getInstance();
            //String date = currentTime.get(Calendar.YEAR) +"-"+currentTime.get(Calendar.MONTH)+"-"+currentTime.get(Calendar.DATE)+" "+currentTime.get(Calendar.HOUR)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = new Date();
            String date =dateFormat.format(date1);
            //String date = currentTime.get(Calendar.YEAR) +"-"+currentTime.get(Calendar.MONTH)+1+"-"+day+" "+currentTime.get(Calendar.HOUR)+":"+currentTime.get(Calendar.MINUTE)+":"+currentTime.get(Calendar.SECOND);

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
            Toast.makeText(getApplicationContext(), sharedPreferences.getInt("cartid",-1)+"",Toast.LENGTH_SHORT).show();
        }
    }
    private void createCart(String url, JSONObject obj, JSONArray orders){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
    private void submit(int totalMoney, int cartid){
        String perNum = table;
        String orderDate = edtDate.getText().toString().trim()+" "+edtTime.getText().toString().trim();
        String service = "0";
        JSONObject item = new JSONObject();
        try {
            item.put("total", totalMoney+"");
            item.put("perNum", perNum);
            item.put("orderDate",orderDate);
            item.put("service", service);
            //show(item.toString());
            callApi(Request.Method.PATCH,urlCreateCart +"/"+ cartid,item);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void callApi(int method, String url, JSONObject item){
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //Toast.makeText(context.getApplicationContext(),url+"",Toast.LENGTH_LONG).show();
        //show(url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, item,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) { show(response.toString());
                        try {
                            if(response.getString("code").equals("200")||response.getString("code").equals("201")){
                                show("Dat hang thanh cong");
                                getCart(sharedPreferences,urlGetdata);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
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