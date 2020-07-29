package com.example.weatherapp;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherResponse {

    public Pogoda getMain() {
        return main;
    }

    public Pogoda main;
    @SerializedName("weather")
    public ArrayList<Icon> weather = new ArrayList<Icon>();

    public String getName() {
        return name;
    }

    private String name;
}

