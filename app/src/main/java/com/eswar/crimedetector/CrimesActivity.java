package com.eswar.crimedetector;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrimesActivity extends AppCompatActivity {

    private String latitude, longitude;
    private final String tag = "tag";
    private Intent intent;
    private final String NONE = "none";
    private CrimeAdapter adapter;
    private RecyclerView recyclerView;
    private List<Crime> crimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crimes);

        recyclerView = findViewById(R.id.crimes_recycler_view);
        recyclerView.setHasFixedSize(true);

        intent = getIntent();

        final boolean locationKnown = intent.getBooleanExtra("locationKnown", false);

        if(locationKnown){
            final String[] coordinates = intent.getStringArrayExtra("coordinates");
            try {
                final String latitude = coordinates[0], longitude = coordinates[1];

                if (checkNonEmpty(latitude) && checkNonEmpty(longitude)) {
                    Log.d(tag, "Searched for latitude: " + latitude + ", longitude: " + longitude);
                    final boolean timeKnown = intent.getBooleanExtra("timeKnown", false);

                    if (timeKnown) {
                        final String[] time = intent.getStringArrayExtra("time");
                        final String month = time[0], year = time[1];
                        Log.d(tag, "Month: " + month + ", Year: " + year);
                        crimesAtLocation(latitude, longitude, month, year);
                    }
                    else{
                        Log.d(tag, "No time provided");
                        try {
                            crimesAtLocation(latitude, longitude, NONE, NONE);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (NullPointerException npe){
                Log.d(tag, "Null Pointer Exception in getting coordinates");
                npe.printStackTrace();
            }
            catch (ArrayIndexOutOfBoundsException aiobe){
                Log.d(tag, "Array Out Of Bounds Exception in getting coordinates");
                aiobe.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            final String forceName = intent.getStringExtra("forceName");
            if(checkNonEmpty(forceName)){
                Log.d(tag, "Searched for Force: " + forceName);
                final boolean timeKnown = intent.getBooleanExtra("timeKnown", false);
                if(timeKnown){
                    try{
                        final String[] time = intent.getStringArrayExtra("time");
                        Log.d(tag, "Time: " + time[1] + "-" + time[0]);
                        crimesAtNoLocation(forceName, time[0], time[1]);
                    }
                    catch (Exception e){
                        Log.d(tag, "Exception in getting time");
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        crimesAtNoLocation(forceName, NONE, NONE);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean checkNonEmpty(String text){
        return ((text != null) && !text.isEmpty());
    }
    private void crimesAtLocation(String latitude, String longitude, String month, String year){
        GetDataService service = RetrofitClient.getRetrofitInstance().create(GetDataService.class);
        Call<List<Crime>> call;
        if(!month.equals(NONE) && !year.equals(NONE)) {
            call = service.getCrimesAtLocationOnDate(year + "-" + month, latitude, longitude);
        }
        else{
            call = service.getCrimesAtLocation(latitude, longitude);
        }
        call.enqueue(new Callback<List<Crime>>() {
            @Override
            public void onResponse(Call<List<Crime>> call, Response<List<Crime>> response) {
                Log.d(tag, "Crimes at location response not failure");
                try {
                    showCrimes(response.body());
                    crimes = response.body();
                    Log.d(tag, "Length of crimes = " + crimes.size());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Crime>> call, Throwable t) {
                Log.d(tag, "Failure connecting to crimes");
                t.printStackTrace();
            }
        });
    }

    private void crimesAtNoLocation(String forceID, String month, String year){
        GetDataService service = RetrofitClient.getRetrofitInstance().create(GetDataService.class);
        Call<List<Crime>> call;
        if(!month.equals(NONE) && !year.equals(NONE)) {
            call = service.getCrimesForForceOnDate("all", forceID, year + "-" + month);
        }
        else{
            call = service.getCrimesForForce("all", forceID);
        }
        call.enqueue(new Callback<List<Crime>>() {
            @Override
            public void onResponse(Call<List<Crime>> call, Response<List<Crime>> response) {
                Log.d(tag, "Crimes at no location response not failure");
                try {
                    showCrimes(response.body());
                    crimes = response.body();
                    Log.d(tag, "Length of crimes = " + crimes.size());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Crime>> call, Throwable t) {
                Log.d(tag, "Failure connecting to crimes");
                t.printStackTrace();
            }
        });
    }

    private void showCrimes(List<Crime> crimes){
        adapter = new CrimeAdapter(CrimesActivity.this, crimes);
        recyclerView.setLayoutManager(new LinearLayoutManager(CrimesActivity.this));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            Log.d(tag, "Swiped on " + crimes.get(viewHolder.getAdapterPosition()).getCategory());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive);
        }
    };
}
