package com.example.goparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class MainActivity extends AppCompatActivity {

    static int count=5;
    private TdxApi tdxApi;
    private String API_BASE_URL = "https://traffic.transportdata.tw/MOTC/v1/Parking/OffStreet/CarPark/City/";
    // 申請的APPID和APPKEY
    // （FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF 為 Guest
    // 帳號，以IP作為API呼叫限制，請替換為註冊的APPID & APPKey）
    private String APPID = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
    private String APPKey = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
    //private String APPID = "194a05b19bbc416ea7eb92059465791c";
    //private String APPKey = "ZN1CzFTHDA4ETAPoXfY5Akok4XI";

    // 取得當下的UTC時間，Java8有提供時間格式DateTimeFormatter.RFC_1123_DATE_TIME
    // 但是格式與C#有一點不同，所以只能自行定義
    private String xdate = getServerTime();
    private String SignDate = "x-date: " + xdate;
    private String respond = "";

    private String Signature = "";
    private String sAuth;

    static ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

    static Double Lat, Lon;

    TextView txt_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_show=findViewById(R.id.textView);

        /**從TDX上取得停車場基本資料**/
        Gson gson = new GsonBuilder().serializeNulls().create(); /* 讓NULL也被序列化 */
        // 查看Log
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        try {
            // 取得加密簽章
            Signature = HMAC_SHA1.Signature(SignDate, APPKey);
        } catch (SignatureException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Log.d("Signature :", Signature);
        sAuth = "hmac username=\"" + APPID + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\""
                + Signature + "\"";
        Log.d("SignAuth", sAuth);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", sAuth)
                                .header("x-date", xdate)
                                .build();

                        return chain.proceed(newRequest);
                    }
                })
                //.connectTimeout(10, TimeUnit.SECONDS)
                //.readTimeout(10,TimeUnit.SECONDS)
                //.writeTimeout(20,TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // gson
                .client(okHttpClient)

                .build();

        tdxApi = retrofit.create(TdxApi.class);

        getStations();
    }

    /**將停車場資料加到HashMap裡面
     * 把停車場資料放進info以回傳到其他java檔使用**/
    private void getStations(){

        Call<ParkingInfo> call = tdxApi.getStations(count,"JSON");

        call.enqueue(new Callback<ParkingInfo>() {
            @Override
            public void onResponse(Call<ParkingInfo> call, Response<ParkingInfo> response) {
                if (!response.isSuccessful()) {
                    txt_show.setText("Code: " + response.code());
                    return;
                }

                ArrayList<String> parkName = new ArrayList<String>();
                ArrayList<String> carNum = new ArrayList<String>();
                ArrayList<String> parkAddress = new ArrayList<String>();
                ArrayList<String> parkFare = new ArrayList<String>();
                ArrayList<String> lon = new ArrayList<String>();
                ArrayList<String> lat = new ArrayList<String>();

                for(CarPark park : response.body().getCarParks()){

                    String Num;
                    String description= park.getDescription();
                    int begin=description.indexOf("車");
                    int end=description.indexOf("格");
                    Num = description.substring(begin + 1, begin + 4);

                    String fare;
                    String fareDescription= park.getFareDescription();
                    int last=fareDescription.indexOf("元");
                    fare=fareDescription.substring(0,last);

                    //計算經緯度，符合的存起來要回傳
                    Lat=park.getCarParkPosition().getPositionLat();
                    Lon=park.getCarParkPosition().getPositionLon();
                    //Double distance=toDistance(myLon, myLat, Lon, Lat);

                    //存到ArrayList方便回傳
                    parkName.add(park.getCarParkName().getZh_tw());
                    carNum.add(Num);
                    parkAddress.add(String.valueOf(park.getAddress()));
                    parkFare.add(fare);
                    lon.add(Double.toString(Lon));
                    lat.add(Double.toString(Lat));
                }

                //建立意圖物件
                Intent info = new Intent(MainActivity.this,MapsActivity.class);

                //設定傳遞鍵值對
                info.putStringArrayListExtra("data_parkName", parkName);
                info.putStringArrayListExtra("data_carNum",carNum);
                info.putStringArrayListExtra("data_address",parkAddress);
                info.putStringArrayListExtra("data_fare",parkFare);
                info.putStringArrayListExtra("data_lon",lon);
                info.putStringArrayListExtra("data_lat",lat);

                //啟用意圖
                startActivity(info);
            }

            @Override
            public void onFailure(Call<ParkingInfo> call, Throwable t) {
                txt_show.setText(t.getMessage());
            }

        });
    }

    // 取得當下UTC時間
    public static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

}