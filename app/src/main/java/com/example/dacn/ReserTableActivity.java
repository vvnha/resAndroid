package com.example.dacn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.ui.dashboard.Food;
import com.example.dacn.ui.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ReserTableActivity extends AppCompatActivity {
    GridView gvTable;
    ArrayList<Table> arrayTable;
    ReserAdapter adapter;
    String text = "";
    ArrayList<Integer> arrayTableChosen;
    int type = 0;
    User user;
    int totalMoney = 0;
    String numNotChoose = null;
    EditText edtDate, edtTime;
    Button btnOrder, btnFind;
    ArrayList<Integer> tableNotChoose;
    String urlGetdata = "https://restaurantqn.herokuapp.com/api/orders/search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reser_table);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        type = bundle.getInt("type");
        user = (User) bundle.getSerializable("user");
        totalMoney = bundle.getInt("totalMoney");

        arrayTableChosen = new ArrayList<>();
        text = new String();
        AnhXa();
        //Log.d("dog",arrayTable.size()+"");
        adapter = new ReserAdapter(ReserTableActivity.this,R.layout.table_service_item, arrayTable);
        gvTable.setAdapter(adapter);
        gvTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String date = edtDate.getText().toString().trim();
                String time = edtTime.getText().toString().trim();
                if (!date.equals("")&&!time.equals("")&&numNotChoose!=null){
                    if(!tableNotChoose.contains(position+1)||tableNotChoose.size()==0){
                        setTable(arrayTable.get(position).getNumberTable(),1);
                    }else {
                        DialogService(arrayTable.get(1),"Ban khong the chon vi ban nay da duoc dat");
                    }
                }else{
                    DialogService(arrayTable.get(1),"Ban chua chon ngay gio");
                }
                //Log.d("dog",totalMoney+"");
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetStatus();
                tableNotChoose = new ArrayList<>();
                String date = edtDate.getText().toString().trim();
                String time = edtTime.getText().toString().trim();
                if(!date.equals("")&&!time.equals("")){
                    JSONObject item = new JSONObject();
                    try {
                        item.put("date",date);
                        item.put("time",time);
                        callApi(Request.Method.POST,urlGetdata,item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    DialogService(arrayTable.get(1),"Ban chua chon ngay gio");
                }
            }
        });

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDate();
            }
        });

        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTime();
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!text.equals("")){
                    //Intent intent = new
                    Intent intent = new Intent(ReserTableActivity.this, ReserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("totalMoney",totalMoney);
                    bundle.putString("table", text);
                    intent.putExtra("dataTotal",bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }else{
                    DialogService(arrayTable.get(1),"ban chua chon ban");
                }
            }
        });
    }
    private void AnhXa() {
        btnOrder = findViewById(R.id.btnOrderTable);
        gvTable = findViewById(R.id.gridOrderTable);
        arrayTable = new ArrayList<>();
        for(int i = 0; i<16; i++ ){
            arrayTable.add(new Table(i+1,false));
        }
        edtDate = findViewById(R.id.edtDateTable);
        edtTime = findViewById(R.id.edtTimeTable);
        btnFind = findViewById(R.id.btnFind);
        //btnOrder.setVisibility(View.GONE);
    }

    private void setTable(int numberTable, int type){
        int dem = 0;
        // neu chua chon thi them vao ds chon, neu chon roi thi khi click bo khoi mang
        for(int i = 0; i<arrayTableChosen.size(); i++){
            if(arrayTableChosen.get(i)==numberTable){
                dem++;
                arrayTableChosen.remove(i);
            }
        }
        if(dem==0){
            arrayTableChosen.add(numberTable);
        }
        //het
        //them vao cai text so ban vd "1,2,4"
        if (type == 1) {
            text = "";
            for (int i = 0; i < arrayTableChosen.size(); i++) {
                if (i == 0) {
                    text = text + arrayTableChosen.get(i);
                } else {
                    text = text + "," + arrayTableChosen.get(i);
                }
            }
            if (arrayTableChosen.size() > 0) {
                for (int i = 0; i < arrayTable.size(); i++) {
                    if (arrayTableChosen.contains(i + 1)||tableNotChoose.contains(i+1)) { // if it has tb has chosen by other, set show the table
                        arrayTable.get(i).setStatus(true);
                    } else {
                        arrayTable.get(i).setStatus(false);
                    }
                }
            } else {
                for (int i = 0; i < arrayTable.size(); i++) {
                    if (!tableNotChoose.contains(i+1)){
                        arrayTable.get(i).setStatus(false);
                    }
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void resetStatus(){
        for (int i = 0; i<arrayTable.size(); i++){
                arrayTable.get(i).setStatus(false);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private  void DialogService(Table table, String mess){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_service);
        TextView txtNumber = dialog.findViewById(R.id.viewOffer);
        Button btnClose = dialog.findViewById(R.id.btnCloseDialog);
        txtNumber.setText(mess+"");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void callApi(int method, String url, JSONObject item){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, item,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            numNotChoose = response.getString("data");
                            setTableNotChoose(numNotChoose);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReserTableActivity.this, "loi", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
    private void datePickerChange(DatePicker datePicker, int year, int month, int dayOfMonth) {
        //Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
        if(month/10<1){
            if (dayOfMonth/10<1){
                edtDate.setText(year +"-0" + (month+1) + "-0" + dayOfMonth);
            }else{
                edtDate.setText(year +"-0" + (month+1) + "-" + dayOfMonth);
            }
        }else{
            if (dayOfMonth/10<1){
                edtDate.setText(year +"-" + (month+1) + "-0" + dayOfMonth);
            }else{
                edtDate.setText(year +"-" + (month+1) + "-" + dayOfMonth);
            }
        }
    }

    private void timePickerChange(TimePicker timePicker, int hour, int minute) {
        //Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
        if (hour/10<1){
            if (minute/10<1){
                edtTime.setText("0"+(hour)+":" + minute + ":00" );
            }else{
                edtTime.setText("0"+(hour)+":0" + minute + ":00" );
            }
        }else{
            if (minute/10<1){
                edtTime.setText(+(hour)+":0" + minute + ":00" );
            }else{
                edtTime.setText(+(hour)+":" + minute + ":00" );
            }
        }
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
                datePickerChange(  datePicker,   year,   monthOfYear,   dayOfMonth);
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

    private void setTableNotChoose(String numNotChoose) {
        if (!numNotChoose.equals("")) {
            if (numNotChoose.indexOf(",") > -1) {
                String[] tableNotChooseString = numNotChoose.split(",");
                if (tableNotChooseString.length > 0) {
                    for (int y = 0; y < tableNotChooseString.length; y++) {
                        arrayTable.get(Integer.parseInt(tableNotChooseString[y]) - 1).setStatus(true);
                        tableNotChoose.add(Integer.parseInt(tableNotChooseString[y]));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }else{
            tableNotChoose.add(Integer.parseInt(numNotChoose));
            arrayTable.get(Integer.parseInt(numNotChoose)-1).setStatus(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}