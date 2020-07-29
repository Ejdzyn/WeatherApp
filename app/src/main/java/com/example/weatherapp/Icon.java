package com.example.weatherapp;

import com.google.gson.annotations.SerializedName;

public class Icon {

    @SerializedName("id")
    public int id;
    @SerializedName("main")
    public String main;
    @SerializedName("description")
    public String description;
    @SerializedName("icon")
    public String icon;

}
