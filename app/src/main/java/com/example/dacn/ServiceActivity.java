package com.example.dacn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.dacn.ui.user.User;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ServiceActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    private int type, totalMoney, orderID;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        type = bundle.getInt("type");
        user = (User) bundle.getSerializable("user");
        orderID = bundle.getInt("orderID");
        totalMoney = bundle.getInt("totalMoney");
        //Toast.makeText(this, type+"",Toast.LENGTH_SHORT);
        //Log.d("type", user.getName()+"");

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("tab 1");
        arrayList.add("tab 2");
        arrayList.add("tab 3");

        prepareViewPager(viewPager, arrayList);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        ServiceAdapter adapter = new ServiceAdapter(getSupportFragmentManager());
        ServiceFragment fragment = new ServiceFragment();
        for(int i = 0; i<arrayList.size(); i++){
            Bundle bundle = new Bundle();
            bundle.putString("title",arrayList.get(i));
            bundle.putInt("type",type);
            bundle.putInt("totalMoney",totalMoney);
            bundle.putInt("orderID", orderID);
            fragment.setArguments(bundle);
            adapter.addFragment(fragment,arrayList.get(i));
            fragment = new ServiceFragment();
        }
        viewPager.setAdapter(adapter);
    }

    private class ServiceAdapter extends FragmentPagerAdapter{

        ArrayList<String> arrayList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title){
            arrayList.add(title);
            fragmentList.add(fragment);
        }


        public ServiceAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrayList.get(position);
        }
    }
}