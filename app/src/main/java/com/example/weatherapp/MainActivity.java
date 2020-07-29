package com.example.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    public static String cityTmp;

    public void saveData(String output){
        SharedPreferences sharedPreferences = getSharedPreferences("TEST",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DATA_KEY",output);
        editor.apply();
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("TEST",MODE_PRIVATE);
        String date = sharedPreferences.getString("DATA_KEY","");
        editText.setText(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.myText);


        loadData();
        refresh();
    }

    public void sendText(View view){
        editText = findViewById(R.id.myText);
        final Intent intent  = new Intent (this, MainResp.class);
        String myText = editText.getText().toString();
        final String tmp = myText;
        myText.replaceAll("ą","a");
        myText.replaceAll("ń","n");
        myText.replaceAll("ć","c");
        myText.replaceAll("ę","e");
        myText.replaceAll("ś","s");
        myText.replaceAll("ó","o");

        //intent.putExtra("CITY",myText);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myText+=",pl";

        cityTmp = myText;

        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(myText, "749561a315b14523a8f5f1ef95e45864", "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Zła nazwa miasta", Toast.LENGTH_SHORT).show();
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

                saveData(tmp);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshRun(int milliseconds){

        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        };
        handler.postDelayed(runnable,milliseconds);
    }


    public void refresh(){
        TextView tv = findViewById(R.id.connectView);
        Button button = findViewById(R.id.button2);

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            tv.setVisibility(View.INVISIBLE);
            button.setEnabled(true);
        }else{
            tv.setVisibility(View.VISIBLE);
            button.setEnabled(false);
        }
        refreshRun(1000);
    }
}
