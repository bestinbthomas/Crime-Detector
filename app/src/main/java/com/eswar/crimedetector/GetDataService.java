package com.eswar.crimedetector;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("/api/forces")
    Call<List<Force>> getAllForces();

    @GET("/api/forces/{name}")
    Call<ForceDetails> getForceDetails(@Path(value = "name", encoded = true) String forceName);

    @GET("/api/crimes-at-location")
    Call<List<Crime>> getCrimesAtLocation(@Query(value = "lat", encoded = true) String latitude, @Query(value = "lng", encoded = true) String longitude);

    @GET("/api/crimes-at-location")
    Call<List<Crime>> getCrimesAtLocationOnDate(@Query(value = "date", encoded = true) String date, @Query(value = "lat", encoded = true) String latitude, @Query(value = "lng", encoded = true) String longitude);

    @GET("/api/crimes-no-location")
    Call<List<Crime>> getCrimesForForce(@Query(value = "category", encoded = true) String category, @Query(value = "force", encoded = true) String force);

    @GET("/api/crimes-no-location")
    Call<List<Crime>> getCrimesForForceOnDate(@Query(value = "category", encoded = true) String category, @Query(value = "force", encoded = true) String force,  @Query(value = "date", encoded = true) String date);

    @GET("/api/crime-categories")
    Call<List<String>> getAllCategories();
}
