package com.eswar.crimedetector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForcesActivity extends AppCompatActivity {

    private final String tag = "tag";
    private RecyclerView recyclerView;
    private ForcesAdapter forcesAdapter;
    private Toolbar toolbar;
    private SearchView searchView;
    private List<Force> forces;
    private boolean submitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forces);

        recyclerView = findViewById(R.id.forces_recycler_view);
        recyclerView.setHasFixedSize(true);

        toolbar = findViewById(R.id.forces_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        GetDataService service = RetrofitClient.getRetrofitInstance().create(GetDataService.class);

        Call<List<Force>> call = service.getAllForces();
        call.enqueue(new Callback<List<Force>>() {
            @Override
            public void onResponse(Call<List<Force>> call, Response<List<Force>> response) {
                try {
                    if (response.body() != null){
                        forces = response.body();
                    }
                    showDataList(response.body());
                }
                catch (NullPointerException npe){
                    Log.d(tag, "Null Pointer Exception");
                    npe.printStackTrace();
                }
                catch (Exception e){
                    Log.d(tag, "Exception in showDataList");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Force>> call, Throwable t) {
                Log.d(tag, "Response failure");
                t.printStackTrace();
            }
        });

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_favicon_white) // resource or drawable
                .showImageForEmptyUri(R.drawable.default_favicon_white) // resource or drawable
                .showImageOnFail(R.drawable.default_favicon_white) // resource or drawable
                .resetViewBeforeLoading(false) // default
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options).build();

        ImageLoader.getInstance().init(config);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forces, menu);
        final MenuItem searchActionItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView)searchActionItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                submitted = true;
                Log.d(tag, "Submitted. Searched for " + query);
//                query = query.toLowerCase();
//                List<Force> queryForces = new ArrayList<>();
//                for (int i = 0; i < forces.size(); ++i){
//                    if(forces.get(i).getName().toLowerCase().contains(query)){
//                        queryForces.add(forces.get(i));
//                    }
//                }
//                showDataList(queryForces, query);
                if(!searchView.isIconified()){
                    searchView.setIconified(true);
                }
                searchActionItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(!submitted) {
                    Log.d(tag, "Changed. Searched for " + query);
                    query = query.toLowerCase();
                    List<Force> queryForces = new ArrayList<>();
                    for (int i = 0; i < forces.size(); ++i) {
                        if (forces.get(i).getName().toLowerCase().contains(query)) {
                            queryForces.add(forces.get(i));
                        }
                    }
                    showDataList(queryForces, query);
                }
                else{
                    submitted = false;
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.app_bar_search){
            Log.d(tag, "Search bar clicked");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDataList(List<Force> forces){
        forcesAdapter = new ForcesAdapter(ForcesActivity.this, forces);
        recyclerView.setLayoutManager(new LinearLayoutManager(ForcesActivity.this));
        recyclerView.setAdapter(forcesAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showDataList(List<Force> forces, String query){
        forcesAdapter = new ForcesAdapter(ForcesActivity.this, forces, query);
        recyclerView.setLayoutManager(new LinearLayoutManager(ForcesActivity.this));
        recyclerView.setAdapter(forcesAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback touchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            Log.d(tag, "Moved");
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d(tag, "Swiped on " + forces.get(viewHolder.getAdapterPosition()).getName());
            forcesAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            Log.d(tag, "dX = " + dX);
            super.onChildDraw(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive);
        }
    };


}
