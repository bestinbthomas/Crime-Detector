package com.eswar.crimedetector;

import com.google.gson.annotations.SerializedName;

public class Force {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public void setId(String id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }

}
