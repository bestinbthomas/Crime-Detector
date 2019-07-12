package com.eswar.crimedetector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ForcesAdapter extends RecyclerView.Adapter<ForcesAdapter.CustomViewHolder> {

    private Context context;
    private List<Force> forces;
    private String query = "";

    public ForcesAdapter(Context context, List<Force> forces){
        this.context = context;
        this.forces = forces;
    }
    public ForcesAdapter(Context context, List<Force> forces, String query){
        this.context = context;
        this.forces = forces;
        this.query = query;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public View mView;
        public TextView forceName, forceID;
        public LinearLayout forceRow;
        public CustomViewHolder(View view){
            super(view);
            mView = view;
            forceName = mView.findViewById(R.id.force_name);
            forceID = mView.findViewById(R.id.force_id);
            forceRow = mView.findViewById(R.id.force_row);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        return new CustomViewHolder(layoutInflater.inflate(R.layout.forces_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {

        if (!query.isEmpty() && !query.equals("") && query != null) {
            Log.d("tag", "In adapter query is: " + query);
            highlightQuery(holder.forceName, forces.get(position).getName(), query, holder.mView);
        }
        else{
            holder.forceName.setText(forces.get(position).getName());
        }
        setTextColors(holder.forceID, "ID: ", forces.get(position).getId(), R.color.white, R.color.gray, holder.mView);

        holder.forceRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ForceDetailsActivity.class).putExtra("forceID", String.valueOf(forces.get(position).getId())));
            }
        });

    }

    @Override
    public int getItemCount() {
        return forces.size();
    }

    public void setTextColors(final TextView textView, final String title, final String content, final int colorTitle, final int colorContent, View v){

        String colorCodeTitle = "#" + Integer.toHexString(v.getResources().getColor(colorTitle) & 0x00ffffff);
        String colorCodeContent = "#" + Integer.toHexString(v.getResources().getColor(colorContent) & 0x00ffffff);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml("<font color=" + colorCodeTitle +">" + title + "</font><font color=" + colorCodeContent + ">" + content + "</font>", Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            textView.setText(Html.fromHtml("<font color=" + colorCodeTitle +">" + title + "</font><font color=" + colorCodeContent + ">" + content + "</font>"));
        }
    }

    public void highlightQuery(final TextView textView, final String title, final String query, View v){

        SpannableString queryString = new SpannableString(title);
        queryString.setSpan(new BackgroundColorSpan(Color.GRAY), title.toLowerCase().indexOf(query.toLowerCase()), title.toLowerCase().indexOf(query.toLowerCase()) + query.length(), 0);
        textView.setText(queryString);

//        String colorCodeTitle = "#" + Integer.toHexString(v.getResources().getColor(colorTitle) & 0x00ffffff);
//        String colorCodeQuery = "#" + Integer.toHexString(v.getResources().getColor(colorQuery) & 0x00ffffff);
//
//        String preTitle, postTitle;
//
//        if(title.contains(query)) {
//            preTitle = title.substring(0, title.indexOf(query));
//            postTitle = title.substring(title.indexOf(query) + query.length(), title.length());
//        }
//        else{
//            preTitle = title;
//            postTitle = "";
//        }
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            textView.setText(Html.fromHtml("<font color=" + colorCodeTitle +">" + preTitle + "</font><font color=" + colorCodeQuery+ ">" + query + "</font>" + "<font color=" + colorCodeTitle + ">" + postTitle + "</font>", Html.FROM_HTML_MODE_LEGACY));
//        }
//        else{
//            textView.setText(Html.fromHtml("<font color=" + colorCodeTitle +">" + preTitle + "</font><font color=" + colorCodeQuery + ">" + query + "</font>" + "<font color=" + colorCodeTitle + ">" + postTitle + "</font>"));
//        }
    }
}
