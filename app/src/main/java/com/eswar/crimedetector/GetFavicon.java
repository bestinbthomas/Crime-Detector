package com.eswar.crimedetector;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetFavicon {

    @GET("/allicons.json")
    Call<Favicon> getFavicon(@Query(value = "url", encoded = true) String url);
}
