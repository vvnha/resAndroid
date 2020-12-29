package com.example.dacn.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.dacn.R;
import com.example.dacn.ui.dashboard.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private HomeViewModel homeViewModel;
    ImageSlider imageSlider;
    List<SlideModel> slideModels;
    List<Food> foods;
    String urlGetFoods = "https://restaurantqn.herokuapp.com/api/foods/getFoods";

    private GridView catView;
    private  List<SpecialModel> catList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //sharedPreferences = this.getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        getSharedPreference();

        foods = new ArrayList<>();
        getData(urlGetFoods);
        catView = root.findViewById(R.id.cat_Grid);

        loadSpecial();
        SpecialAdapter adapter = new SpecialAdapter(catList);
        catView.setAdapter(adapter);
        List<String> title = new ArrayList<>();
        title.add("Nhà hàng năm sao mang đẳng cấp quốc tế");
        title.add("Các đầu bếp có kinh nghiệm và tay nghề cao");
        title.add("Không gian thoáng đãng, phù hợp cho tất cả các sự kiện");
        title.add("Luôn luôn quan tâm và lắng nghe ý kiến của khách hàng");

        imageSlider = root.findViewById(R.id.slider);
        slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://www.tasteofhome.com/wp-content/uploads/2020/04/Chilis-3.jpg",title.get(0), ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://images.unsplash.com/photo-1552566626-52f8b828add9?ixid=MXwxMjA3fDB8MHxzZWFyY2h8NHx8cmVzdGF1cmFudHxlbnwwfHwwfA%3D%3D&ixlib=rb-1.2.1&w=1000&q=80",title.get(1), ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://st3.depositphotos.com/1000336/13275/i/600/depositphotos_132754102-stock-photo-restaurant-blurred-background.jpg",title.get(2), ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://images-na.ssl-images-amazon.com/images/I/71kO1Ec7kWL._AC_SL1200_.jpg",title.get(3), ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        return root;
    }

    private void loadSpecial() {
        catList.clear();
        String title = "Món ăn đặc biệt";
        catList.add(new SpecialModel("1", "Cua rang me", title, "http://2sao.vietnamnetjsc.vn/images/2018/01/09/13/14/cua-rang-me.jpg"));
        catList.add(new SpecialModel("2", "Bánh bạch tuột", title, "https://intertour.vn/uploads/media/news/37847d75-4b4e-4fda-8552-5edb30bd5cc5.jpg"));
        catList.add(new SpecialModel("3", "Udon", title, "https://intertour.vn/uploads/media/news/f0f46ca6-d654-432a-8f30-90c8b87592f1.jpg"));
        catList.add(new SpecialModel("4", "Sashimi cá nóc", title, "https://intertour.vn/uploads/media/news/e1eabe76-4912-4003-9899-1062c15e2eaf.jpg"));
        catList.add(new SpecialModel("5", "Pizza", title, "https://cdn.tgdd.vn/Files/2020/04/21/1250680/cach-lam-banh-pizza-chay-bang-noi-chien-khong-dau-10.jpg"));
        catList.add(new SpecialModel("6", "Lẩu Thái", title, "https://cdn.cet.edu.vn/wp-content/uploads/2018/03/hinh-anh-lau-thai-chua-cay.jpg"));
    }

    private void getSharedPreference(){
        sharedPreferences = this.getActivity().getSharedPreferences("dataLogin", Context.MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("checked", false);
        //Toast.makeText(getActivity(),String.valueOf(check),Toast.LENGTH_LONG).show();
    }
    private void getData(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray result = response.getJSONArray("data");
                            for(int i =0 ; i<result.length(); i++){
                                JSONObject obj = result.getJSONObject(i);
                                foods.add(new Food(obj.getInt("foodID"),
                                        obj.getString("foodName"),
                                        obj.getString("img"),
                                        obj.getString("price"),
                                        obj.getString("ingres"),
                                        (float)obj.getInt("rating"),
                                        obj.getInt("hits"),
                                        obj.getInt("parentID")));

                            }
                            //foodAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "loi", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

}