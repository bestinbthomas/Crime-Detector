package com.eswar.crimedetector;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SearchLocation extends Fragment {

    private EditText latitudeText, longitudeText;
    private Button search;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_location_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        latitudeText = view.findViewById(R.id.search_latitude);
        longitudeText = view.findViewById(R.id.search_longitude);
        search = view.findViewById(R.id.search_location_submit);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String latitude = String.valueOf(latitudeText.getText());
                final String longitude = String.valueOf(longitudeText.getText());

                startActivity(new Intent(getActivity(), CrimesActivity.class).putExtra("coordinates", new String[]{latitude, longitude}));
            }
        });
    }
}
