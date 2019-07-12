package com.eswar.crimedetector;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrimeSearchActivity extends AppCompatActivity {

    private String month, year, latitude, longitude;
    private final String tag = "tag";
    private List<Force> forces;
    private LinearLayout locationLayout, noLocationLayout;
    private List<String> forceNames = new ArrayList<>(), forceIDs = new ArrayList<>();
    private AutoCompleteTextView forceText;
    private boolean locationChosen = true, foundForces = false;
    private EditText latitudeText, longitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_search);

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    findForces();
                }
            }, 100);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        final List<String> months = Arrays.asList("--Latest--", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        final List<String> years = Arrays.asList("--Latest--", "2019", "2018", "2017", "2016", "2015", "2014", "2013");

        final Spinner monthSpinner = findViewById(R.id.month_spinner);
        final Spinner yearSpinner = findViewById(R.id.year_spinner);

        forceText = findViewById(R.id.search_force_main);

        final CheckBox locationCheckBox = findViewById(R.id.crime_search_location_check);

        locationLayout = findViewById(R.id.crime_search_location_known);
        noLocationLayout = findViewById(R.id.crime_search_location_unknown);

        final MonthAdapter monthAdapter = new MonthAdapter(CrimeSearchActivity.this, months);
        final YearAdapter yearAdapter = new YearAdapter(CrimeSearchActivity.this, years);

        latitudeText = findViewById(R.id.search_latitude_main);
        longitudeText = findViewById(R.id.search_longitude_main);

        final Button submit = findViewById(R.id.crime_search_submit);

        locationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    locationLayout.setVisibility(View.VISIBLE);
                    noLocationLayout.setVisibility(View.GONE);
                    locationChosen = true;
                }
                else{
                    locationLayout.setVisibility(View.GONE);
                    noLocationLayout.setVisibility(View.VISIBLE);
                    locationChosen = false;
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationChosen){
                    if(checkNonEmpty(latitudeText) && checkNonEmpty(longitudeText)){
                        latitude = String.valueOf(latitudeText.getText());
                        longitude = String.valueOf(longitudeText.getText());
                        Intent intent = new Intent(CrimeSearchActivity.this, CrimesActivity.class);
                        intent.putExtra("coordinates", new String[]{latitude, longitude});
                        boolean timeKnown = false;
                        if (!month.equals(months.get(0))){
                            String actualYear = (year.equals(years.get(0))) ? "2019" : year;
                            intent.putExtra("time", new String[]{monthNumber(month, months), actualYear});
                            timeKnown = true;
                        }
                        else if(year.equals(years.get(0))){
                            Log.d(tag, "Month chosen, Year not chosen");
                            //TODO Alert User With Year
                        }
                        intent.putExtra("timeKnown", timeKnown);
                        intent.putExtra("locationKnown", true);
                        startActivity(intent);
                    }
                }
                else{
                    if(checkNonEmpty(forceText)){
                        if(foundForces){
                            int choice = findStringInText(forceText, forces);
                            if(choice != -1){
                                Intent intent = new Intent(CrimeSearchActivity.this, CrimesActivity.class);
                                intent.putExtra("forceName", forces.get(choice).getId());
                                boolean timeKnown = false;
                                if(!month.equals(months.get(0))){
                                    timeKnown = true;
                                    String actualYear = (year.equals(years.get(0))) ? "2019" : year;
                                    intent.putExtra("time", new String[]{monthNumber(month, months), actualYear});
                                }
                                intent.putExtra("timeKnown", timeKnown);
                                intent.putExtra("locationKnown", false);
                                startActivity(intent);
                            }
                            else {
                                String forceName = forceText.getEditableText().toString();
                                Toast.makeText(CrimeSearchActivity.this, "Force " + forceName + " not found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

        monthSpinner.setAdapter(monthAdapter);
        yearSpinner.setAdapter(yearAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month = months.get(position);
                Log.d(tag, "Month selected: " + month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                month = months.get(0);
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = years.get(position);
                Log.d(tag, "Year Selected: " + year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                year = years.get(0);
            }
        });

    }

    private void findForces(){
        GetDataService service = RetrofitClient.getRetrofitInstance().create(GetDataService.class);
        Call<List<Force>> call = service.getAllForces();

        call.enqueue(new Callback<List<Force>>() {
            @Override
            public void onResponse(Call<List<Force>> call, Response<List<Force>> response) {
                if(response.body() != null && response.body().size() != 0){
                    forces = response.body();
                    try {
                        Log.d(tag, "Length of forces: " + forces.size());
                        for (int i = 0; i < forces.size(); ++i) {
                            forceNames.add(forces.get(i).getName());
                            forceIDs.add(forces.get(i).getId());
                        }
                        forceText.setAdapter(new ArrayAdapter<String>(CrimeSearchActivity.this, android.R.layout.simple_list_item_1, forceNames));
                        foundForces = true;
                        Log.d(tag, "Forces found");
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Force>> call, Throwable t) {
                Log.d(tag, "Failure in getting forces in Crime Search Activity");
            }
        });
    }

    public class MonthAdapter extends ArrayAdapter<String>{

        private Activity context;
        private List<String> months;
        private View view;

        public MonthAdapter(Activity context, List<String> months){
            super(context, R.layout.month_spinner_layout, R.id.month_spinner_text, months);

            this.context = context;
            this.months = months;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.month_spinner_layout, null, true);

            final TextView monthText = view.findViewById(R.id.month_spinner_text);
            monthText.setText(months.get(position));

            return view;
        }
    }

    public class YearAdapter extends ArrayAdapter<String>{

        private Activity context;
        private List<String> years;
        private View view;

        public YearAdapter(Activity context, List<String> years){
            super(context, R.layout.year_spinner_layout, R.id.year_spinner_text, years);

            this.context = context;
            this.years = years;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.year_spinner_layout, null, true);

            final TextView yearText = view.findViewById(R.id.year_spinner_text);
            yearText.setText(years.get(position));

            return view;
        }
    }

    private boolean checkNonEmpty(String text){
        return ((text != null) && !text.isEmpty());
    }
    private boolean checkNonEmpty(EditText editText){
        String text = String.valueOf(editText.getText());
        return ((text != null) && !text.isEmpty());
    }
    private boolean checkNonEmpty(AutoCompleteTextView autoCompleteTextView){
        String text = String.valueOf(autoCompleteTextView.getEditableText());
        return ((text != null) && !text.isEmpty());
    }
    private int findStringInText(AutoCompleteTextView autoCompleteTextView, List<Force> forces){
        String text = autoCompleteTextView.getEditableText().toString();
        int i;
        for (i = 0; i < forces.size(); ++i){
            String choice = forces.get(i).getName();
            if(text.equalsIgnoreCase(choice)){
                return i;
            }
        }
        return -1;
    }
    private String monthNumber(String month, List<String> months){
        int monthNumber = months.indexOf(month);
        if(monthNumber < 10){
            return "0" + String.valueOf(monthNumber);
        }
        else {
            return String.valueOf(monthNumber);
        }
    }
}
