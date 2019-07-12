package com.eswar.crimedetector;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.text.WordUtils;

import java.util.Arrays;
import java.util.List;

public class CrimeAdapter extends RecyclerView.Adapter<CrimeAdapter.CustomViewHolder> {

    private Context context;
    private List<Crime> crimes;

    public CrimeAdapter(Context context, List<Crime> crimes){
        this.context = context;
        this.crimes = crimes;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public View mView;

        public CustomViewHolder(View view){
            super(view);
            mView = view;
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CustomViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.crime_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewHolder, int i) {
        View view = viewHolder.mView;
        final Crime crime = crimes.get(i);
        final TextView category = view.findViewById(R.id.crime_row_category);
        final TextView location = view.findViewById(R.id.crime_row_location);
        final TextView date = view.findViewById(R.id.crime_row_date);

        String categoryString = capitalizeCategory(crime.getCategory());

        category.setText(categoryString);
        location.setText(crime.getLocation().getStreet().getName());

        String dateString = formatDate(crime.getMonth());
        date.setText(dateString);

        Log.d("tag", "Set: " + crime.toString());
    }

    @Override
    public int getItemCount() {
        return crimes.size();
    }

    private String  urlHtml(String url){
        if(url != null && !url.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Html.fromHtml("<a href=\"" + url + "\">" + url + "</a>", Html.FROM_HTML_MODE_LEGACY).toString();
            } else {
                return Html.fromHtml("<a href=\"" + url + "\">" + url + "</a>").toString();
            }
        }
        else { return null; }
    }

    private String capitalizeCategory(String category){
        return WordUtils.capitalizeFully(category.replace("-", " "));
    }

    private String formatDate(String date){
        final List<String> months = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        final String month = date.split("-")[1], year = date.split("-")[0];
        int monthNumber;
        try {
            monthNumber = Integer.parseInt(month);
        }
        catch (Exception e){
            e.printStackTrace();
            return date;

        }
        return months.get(monthNumber) + " " + year;

    }
}
