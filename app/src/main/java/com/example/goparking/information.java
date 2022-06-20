package com.example.goparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class information extends AppCompatActivity {

    static int count=5;

    RecyclerView mRecyclerView;
    MyListAdapter myListAdapter;
    static ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

    static Double Lat, Lon;
    //static Intent info = new Intent();

    ImageButton btn_back;
    TextView txt_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        btn_back=findViewById(R.id.backToMain);
        txt_show=findViewById(R.id.show);

        Intent info=getIntent();

        ArrayList<String> parkName = info.getStringArrayListExtra("data_parkName");
        ArrayList<String> carNum = info.getStringArrayListExtra("data_carNum");
        ArrayList<String> parkAddress = info.getStringArrayListExtra("data_address");
        ArrayList<String> parkFare = info.getStringArrayListExtra("data_fare");

        for(int i=0; i<5; i++) {
            HashMap<String, String> hashMap = new HashMap<>();

//        hashMap.put("Name","停車場名稱: ："+park.getCarParkName().getZh_tw());
//        hashMap.put("Address",String.valueOf(park.getAddress()));
//        hashMap.put("Num",String.valueOf(Num));
//        hashMap.put("Fare", fare);

            hashMap.put("Name", "停車場名稱: ：" + parkName.get(i));
            hashMap.put("Address", String.valueOf(parkAddress.get(i)));
            hashMap.put("Num", String.valueOf(carNum.get(i)));
            hashMap.put("Fare", parkFare.get(i));

            if (count != 0) {
                arrayList.add(hashMap);
                count--;
            }
        }

        mRecyclerView = findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        myListAdapter = new MyListAdapter();
        mRecyclerView.setAdapter(myListAdapter);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent info=getIntent();
                info.setClass(information.this, MapsActivity.class);
                startActivity(info);
            }
        });

    }

    /**把HashMap中的資料在RecyclerView中呈現**/
    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView tvId,txt_Address,txt_Num,txt_fare;
            private View mView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvId = itemView.findViewById(R.id.textView_Id);
                txt_Address = itemView.findViewById(R.id.txt_Address);
                txt_Num = itemView.findViewById(R.id.txt_Num);
                txt_fare  = itemView.findViewById(R.id.txt_fare);
                mView  = itemView;
            }
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvId.setText(arrayList.get(position).get("Name"));
            holder.txt_Address.setText(arrayList.get(position).get("Address"));
            holder.txt_Num.setText(arrayList.get(position).get("Num"));
            holder.txt_fare.setText(arrayList.get(position).get("Fare"));

            holder.mView.setOnClickListener((v)->{
                Toast.makeText(getBaseContext(),holder.txt_fare.getText(),Toast.LENGTH_SHORT).show();
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    //將經緯度轉換成實際距離
    private static Double toDistance(double LonA, double LatA, double LonB, double LatB){

        double MLonA = LonA;
        double MLatA = LatA;
        double MLonB = LonB;
        double MLatB = LatB;

        // 地球半徑(千米)
        double R = 6371.004;
        double C = Math.sin(rad(LatA)) * Math.sin(rad(LatB)) + Math.cos(rad(LatA)) * Math.cos(rad(LatB)) * Math.cos(rad(MLonA - MLonB));
        return (R * Math.acos(C));
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}