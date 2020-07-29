package com.example.weatherapp;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface WeatherService {

    @GET("data/2.5/weather")
    Call<WeatherResponse> getCurrentWeatherData(
            @Query("q") String city,
            @Query("APPID") String app_id,
            @Query("units") String metric
    );
}
