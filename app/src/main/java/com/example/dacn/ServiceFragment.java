package com.example.dacn;

import android.app.Dialog;
import android.os.Bundle;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
    String text;
    ArrayList<Integer> arrayTableChosen;

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
        }
        //Toast.makeText(getActivity(), "ok",Toast.LENGTH_SHORT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
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
        adapter = new TableAdapter(getContext(),R.layout.table_service_item, arrayTable);

        Log.d("dog", arrayTableChosen.size()+"");

        gvTable.setAdapter(adapter);
        gvTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkTable(arrayTable.get(position).getNumberTable());
                mSocket.emit("message",arrayTableChosen);
//                Log.d("dog", arrayTableChosen.size()+"");
                //DialogService(arrayTable.get(position), text+"");
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
            //Log.d("cat", arrayTableChosen.size()+"");
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
    }

    private void AnhXa(View view) {
        gvTable = view.findViewById(R.id.gridViewTable);
        arrayTable = new ArrayList<>();
        for(int i = 0; i<16; i++ ){
            arrayTable.add(new Table(i+1,false));
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
}