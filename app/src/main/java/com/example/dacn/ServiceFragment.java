package com.example.dacn;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.res.TypedArrayUtils;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacn.ui.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceFragment extends Fragment {
    GridView gvTable;
    ArrayList<Table> arrayTable;
    TableAdapter adapter;
    String text = "";
    ArrayList<Integer> arrayTableChosen;
    int type = 0;
    int orderID = 0;
    String perNum = "";
    User user;
    int totalMoney = 0;
    String tableOrdered = "";
    SharedPreferences sharedPreferences;
    String urlLogin = "https://restaurantqn.herokuapp.com/api/orders/";

    Button btnOrder;

    private Socket mSocket;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceFragment newInstance(String param1, String param2) {
        ServiceFragment fragment = new ServiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            type = getArguments().getInt("type");
            user = (User) getArguments().getSerializable("user");
            totalMoney = getArguments().getInt("totalMoney");
            orderID = getArguments().getInt("orderID");
        }
        //Toast.makeText(getActivity(), "ok",Toast.LENGTH_SHORT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        sharedPreferences = getActivity().getSharedPreferences("dataLogin",getContext().MODE_PRIVATE);
        arrayTableChosen = new ArrayList<>();
        text = new String();


        //TextView textView = view.findViewById(R.id.text_view);
        //String title = getArguments().getString("title");
        //textView.setText(title);
        try {
            mSocket = IO.socket("http://192.168.1.4:9000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
        mSocket.on("thread",onRetrieveData);

        AnhXa(view);


        if(orderID>0){
            callApi(Request.Method.GET, urlLogin+orderID, null);
            //Log.d("length of array", orderID+"");
        }


        adapter = new TableAdapter(getContext(),R.layout.table_service_item, arrayTable);
        //Log.d("dog", totalMoney+"");

        gvTable.setAdapter(adapter);
        gvTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tableOrder = "5,6,7";
                if(type ==2){
                    if (orderID!=0){
                        checkTable(arrayTable.get(position).getNumberTable(), tableOrdered);
                    }else{
                        checkTable(arrayTable.get(position).getNumberTable());
                    }
                }
            }
        });


        return view;

    }

    private Emitter.Listener onRetrieveData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //JSONObject response = (JSONObject) args[0];
            JSONArray table = (JSONArray) args[0];
            arrayTableChosen.removeAll(arrayTableChosen);
            //Log.d("dog", table+"");
            for (int i = 0; i < table.length(); i++){
                try {
                    arrayTableChosen.add(Integer.parseInt(table.getString(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(arrayTableChosen.size()>0){
                for (int i = 0; i<arrayTable.size(); i++){
                    if(arrayTableChosen.contains(i+1)){
                        arrayTable.get(i).setStatus(true);
                    }else{
                        arrayTable.get(i).setStatus(false);
                    }
                }
            }else {
                for (int i = 0; i< arrayTable.size(); i++){
                    arrayTable.get(i).setStatus(false);
                }
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };


    private void checkTable(int numberTable) {

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
        mSocket.emit("message",arrayTableChosen);
    }

//    private void setTable(int numberTable, int type){
//        int dem = 0;
//        for(int i = 0; i<arrayTableChosen.size(); i++){
//            if(arrayTableChosen.get(i)==numberTable){
//                dem++;
//                arrayTableChosen.remove(i);
//            }
//        }
//        if(dem==0){
//            arrayTableChosen.add(numberTable);
//        }
//        if (type == 1) {
//                text = "";
//                for (int i = 0; i < arrayTableChosen.size(); i++) {
//                    if (i == 0) {
//                        text = text + arrayTableChosen.get(i);
//                    } else {
//                        text = text + "," + arrayTableChosen.get(i);
//                    }
//                }
//                if (arrayTableChosen.size() > 0) {
//                    for (int i = 0; i < arrayTable.size(); i++) {
//                        if (arrayTableChosen.contains(i + 1)) {
//                            arrayTable.get(i).setStatus(true);
//                        } else {
//                            arrayTable.get(i).setStatus(false);
//                        }
//                    }
//                } else {
//                    for (int i = 0; i < arrayTable.size(); i++) {
//                        arrayTable.get(i).setStatus(false);
//                    }
//                }
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//        }
//    }

    private void checkTable(int numberTable, String orderTable) {
        if(!orderTable.equals("")){
//            String[] table  = orderTable.split(",");
            if(orderTable.contains(numberTable+"")){
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
                mSocket.emit("message",arrayTableChosen);
            }else{
                DialogService(arrayTable.get(numberTable-1),"ban nay ban chua dat");
            }
            //Log.d("table", table[0]+"");

        }else {
            DialogService(arrayTable.get(numberTable),"ban trong");
        }
    }

    private void AnhXa(View view) {
        btnOrder = view.findViewById(R.id.btnOrderTable);
        gvTable = view.findViewById(R.id.gridViewTable);
        arrayTable = new ArrayList<>();
        for(int i = 0; i<16; i++ ){
            arrayTable.add(new Table(i+1,false));
        }
        if (type == 2){
            btnOrder.setVisibility(View.GONE);
        }

    }

    private  void DialogService(Table table, String mess){
        Dialog dialog = new Dialog(getActivity());
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
        String token = sharedPreferences.getString("token","");
        String header = "Bearer "+ token;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //Toast.makeText(context.getApplicationContext(),url+"",Toast.LENGTH_LONG).show();
        //show(url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, item,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("code").equals("200")||response.getString("code").equals("201")){
                                JSONObject data = new JSONObject(response.getString("data"));
                                //Log.d("data",data.getString("perNum"));
                                tableOrdered = data.getString("perNum");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();
                Log.d("error",error+"");
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