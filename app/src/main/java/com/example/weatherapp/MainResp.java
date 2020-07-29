package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainResp extends AppCompatActivity {



    private TextView tempDisp;
    private TextView cityName;
    private TextView clock;
    private TextView pressDisp;
    private TextView humidityDisp;
    private TextView minDisp;
    private TextView maxDisp;
    public static SwipeRefreshLayout refreshLayout;
    private ImageView iconWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

            tempDisp = findViewById(R.id.tempDisplay);
            cityName = findViewById(R.id.cityName);
            clock = findViewById(R.id.Clock);
            pressDisp = findViewById(R.id.pressureDisplay);
            humidityDisp  = findViewById(R.id.humidityDisplay);
            minDisp = findViewById(R.id.minDisplay);
            maxDisp = findViewById(R.id.maxDisplay);
            refreshLayout = findViewById(R.id.refresh);
            iconWeather = findViewById(R.id.weatherIcon);

            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    getData();
                    Toast.makeText(MainResp.this, "Refreshed", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                        }
                    },0);
                }
            });

        Intent intent = getIntent();

            String miasto = intent.getStringExtra("CITY");
            Double temp = intent.getDoubleExtra("TEMP",0);
            int press = intent.getIntExtra("PRESS",0);
            Double humidity = intent.getDoubleExtra("HUM",0);
            Double min = intent.getDoubleExtra("MIN",0);
            Double max = intent.getDoubleExtra("MAX",0);
            String icon = intent.getStringExtra("ICON");
            cityName.setText(miasto);
            getTime();

            tempDisp.setText(temp+"\u2103");
            pressDisp.setText(press+" hPa");
            humidityDisp.setText(humidity+"%");
            minDisp.setText(min+"\u2103");
            maxDisp.setText(max+"\u2103");

        String link =  "https://openweathermap.org/img/wn/" +  icon +".png";
        Picasso.get().load(link).into(iconWeather);

    }

    public void getData(){
        final Intent intent  = new Intent (String.valueOf(MainResp.class));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        String myText = MainActivity.cityTmp;;

        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(myText, "749561a315b14523a8f5f1ef95e45864", "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainResp.this, "Zła nazwa miasta", Toast.LENGTH_SHORT).show();
                    return;
                }
                WeatherResponse weatherResponse = response.body();
                double temp = weatherResponse.getMain().getTemp();
                int press = weatherResponse.getMain().getPressure();
                double humidity = weatherResponse.getMain().getHumidity();
                double min = weatherResponse.getMain().getTemp_min();
                double max = weatherResponse.getMain().getTemp_max();
                String icon = weatherResponse.weather.get(0).icon;
                intent.putExtra("ICON",icon);
                intent.putExtra("CITY",weatherResponse.getName());
                intent.putExtra("TEMP",temp);
                intent.putExtra("PRESS",press);
                intent.putExtra("HUM",humidity);
                intent.putExtra("MIN",min);
                intent.putExtra("MAX",max);

                //Toast.makeText(MainResp.this, icon, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainResp.this, "Connection Error !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTime(){
        Date today = new Date();

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm");

        df.setTimeZone(java.util.TimeZone.getDefault());
        String localT = df.format(today);
        clock.setText(localT);

        Calendar calendar = Calendar.getInstance();
        int min = calendar.get(Calendar.MINUTE);

        if(min%5==0){
            getData();
        }

        refresh(1000);
    }

    private void refresh(int milliseconds){

        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getTime();
            }
        };
        handler.postDelayed(runnable,milliseconds);
    }


}

