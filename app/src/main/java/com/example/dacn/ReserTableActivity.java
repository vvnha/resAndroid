package com.example.dacn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.dacn.ui.user.User;

import java.util.ArrayList;

public class ReserTableActivity extends AppCompatActivity {
    GridView gvTable;
    ArrayList<Table> arrayTable;
    ReserAdapter adapter;
    String text = "";
    ArrayList<Integer> arrayTableChosen;
    int type = 0;
    User user;
    int totalMoney = 0;

    Button btnOrder;

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
                setTable(arrayTable.get(position).getNumberTable(),1);
                //DialogService(arrayTable.get(1),arrayTable.get(position).getNumberTable()+"");
                //Log.d("dog",totalMoney+"");
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
        //btnOrder.setVisibility(View.GONE);
    }

    private void setTable(int numberTable, int type){
        int dem = 0;
        for(int i = 0; i<arrayTableChosen.size(); i++){
            if(arrayTableChosen.get(i)==numberTable){
                dem++;
                arrayTableChosen.remove(i);
            }
        }
        if(dem==0){
            arrayTableChosen.add(numberTable);
        }
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
                    if (arrayTableChosen.contains(i + 1)) {
                        arrayTable.get(i).setStatus(true);
                    } else {
                        arrayTable.get(i).setStatus(false);
                    }
                }
            } else {
                for (int i = 0; i < arrayTable.size(); i++) {
                    arrayTable.get(i).setStatus(false);
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
}