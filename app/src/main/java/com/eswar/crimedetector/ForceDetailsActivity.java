package com.eswar.crimedetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForceDetailsActivity extends AppCompatActivity {
    private ForceDetails forceDetails;
    private String forceID;
    private final String tag = "tag";
    private ForceEngagementsAdapter engagementsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_details);

        forceID = getIntent().getStringExtra("forceID");

        GetDataService service = RetrofitClient.getRetrofitInstance().create(GetDataService.class);

        Call<ForceDetails> callForce = service.getForceDetails("/" + forceID);
        callForce.enqueue(new Callback<ForceDetails>() {
            @Override
            public void onResponse(Call<ForceDetails> call, Response<ForceDetails> response) {
                try {
                    showDetailsForce(response.body());
                }
                catch (NullPointerException npe){
                    Log.d(tag, "Null Pointer Exception");
                    npe.printStackTrace();
                }
                catch (Exception e){
                    Log.d(tag, "Exception in showDetailsForce");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ForceDetails> call, Throwable t) {
                Log.d(tag, "Failure in Connecting Force Details");
                t.printStackTrace();
            }
        });
    }

    private void showDetailsForce(ForceDetails forceDetails){

        final TextView forceDetailsName = findViewById(R.id.force_name_details);
        final TextView forceDetailsUrl = findViewById(R.id.force_url_details);
        final TextView forceDetailsPhone = findViewById(R.id.force_phone_details);
        final TextView forceDetailsAbout = findViewById(R.id.force_about_details);
        final TextView forceDetailsAboutHeading = findViewById(R.id.force_about_details_heading);
        final ImageView faviconImage = findViewById(R.id.force_image_details);
        final RecyclerView forceEngagementsRecyclerView = findViewById(R.id.force_engagements_recycler_view);
        final CheckBox starCheckBox = findViewById(R.id.force_star);

        forceDetailsName.setText(forceDetails.getName());

        if(forceDetails.getUrl() != null && !forceDetails.getUrl().isEmpty()){
            forceDetailsUrl.setText(urlHtml(forceDetails.getUrl()));
            Linkify.addLinks(forceDetailsUrl, Linkify.WEB_URLS);
            forceDetailsUrl.setLinkTextColor(getResources().getColor(R.color.blue));
        }
        else{ forceDetailsUrl.setVisibility(View.GONE); }

        if(forceDetails.getTelephone() != null && !forceDetails.getTelephone().isEmpty()){ setTextColors(forceDetailsPhone, "Phone: ", forceDetails.getTelephone(), R.color.white, R.color.gray);}
        else{ forceDetailsPhone.setVisibility(View.GONE); }

        if(forceDetails.getDescription() != null && !forceDetails.getDescription().isEmpty()){ forceDetailsAbout.setText(parseHtml(forceDetails.getDescription())); }
        else{
            forceDetailsAbout.setVisibility(View.GONE);
            forceDetailsAboutHeading.setVisibility(View.GONE);

        }

        engagementsAdapter = new ForceEngagementsAdapter(ForceDetailsActivity.this, forceDetails.engagement_methods);

        forceEngagementsRecyclerView.setHasFixedSize(true);
        forceEngagementsRecyclerView.setLayoutManager(new LinearLayoutManager(ForceDetailsActivity.this));
        forceEngagementsRecyclerView.setNestedScrollingEnabled(false);
        forceEngagementsRecyclerView.setAdapter(engagementsAdapter);

        callBestIcon(faviconImage, forceDetails.getUrl());

        starCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(Build.VERSION.SDK_INT >= 21) {
                    if(isChecked){
                        Log.d(tag, "Favorites Button Pressed");
                        starCheckBox.setButtonTintList(getResources().getColorStateList(R.color.yellow));
                    }
                    else {
                        Log.d(tag, "Unstarred");
                        starCheckBox.setButtonTintList(getResources().getColorStateList(R.color.gray));
                    }
                }
            }
        });

    }

    private String urlHtml(String url){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return Html.fromHtml("<a href=\"" + url + "\">" + url + "</a>", Html.FROM_HTML_MODE_LEGACY).toString();
        }
        else{
            return Html.fromHtml("<a href=\"" + url + "\">" + url + "</a>").toString();
        }
    }

    private String parseHtml(String html){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        }
        else{
            return Html.fromHtml(html).toString();
        }
    }

    public String getGoogleUrlFavicon(String url){

        if(url != null && !url.isEmpty()){
            return ("https://www.google.com/s2/favicons?domain=" + url);
        }
        else{
            return "";
        }
    }

    public void callBestIcon(final ImageView imageView, String siteUrl){

        GetFavicon faviconService = RetrofitFaviconClient.getRetrofitInstance().create(GetFavicon.class);
        Call<Favicon> callFavicon = faviconService.getFavicon(siteUrl);
        callFavicon.enqueue(new Callback<Favicon>() {
            @Override
            public void onResponse(Call<Favicon> call, Response<Favicon> response) {
                try {
                    String iconUrl = getFaviconUrl(response.body());
                    setImage(imageView, iconUrl);
                    imageView.setVisibility(View.VISIBLE);
                }
                catch (NullPointerException npe){
                    Log.d(tag, "Null Pointer Exception");
                    npe.printStackTrace();
                }
                catch (Exception e){
                    Log.d(tag, "Exception in callBestIcon");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Favicon> call, Throwable t) {
                Log.d(tag, "Failure in Connecting Force Details");
                t.printStackTrace();
            }
        });
    }

    public String getFaviconUrl(Favicon favicon){
        ArrayList<Favicon.FaviconDetails> icons = favicon.getIcons();

        if(icons.size() == 0){ return ""; }
        else{ return icons.get(0).getUrl(); }
    }

    public void setImage(final ImageView imageView, final String iconUrl){

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(iconUrl, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
//                Log.d(tag, "Image loading started");
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.d(tag, "Image loading failed");
                failReason.getCause().printStackTrace();

            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                Log.d(tag, "Image loading complete");
                imageView.setImageBitmap(loadedImage);
                imageView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Log.d(tag, "Image loading canceled");
            }
        });
    }

    public void setTextColors(final TextView textView, final String title, final String content, final int colorTitle, final int colorContent){

        String colorCodeTitle = "#" + Integer.toHexString(getResources().getColor(colorTitle) & 0x00ffffff);
        String colorCodeContent = "#" + Integer.toHexString(getResources().getColor(colorContent) & 0x00ffffff);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml("<font color=" + colorCodeTitle +">" + title + "</font><font color=" + colorCodeContent + ">" + content + "</font>", Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            textView.setText(Html.fromHtml("<font color=" + colorCodeTitle +">" + title + "</font><font color=" + colorCodeContent + ">" + content + "</font>"));
        }
    }


//    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
//
//        private String url;
//        private ImageView imageView;
//
//        public ImageLoadTask(String url, ImageView imageView) {
//            this.url = url;
//            this.imageView = imageView;
//        }
//
//        @Override
//        protected Bitmap doInBackground(Void... params) {
//            try {
//                URL urlConnection = new URL(url);
//                HttpURLConnection connection = (HttpURLConnection) urlConnection
//                        .openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                return myBitmap;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            super.onPostExecute(result);
//            imageView.setImageBitmap(result);
//        }
//
//    }

    //    public Bitmap getBitmapFromURL(String src) {
//        try {
//            java.net.URL url = new java.net.URL(src);
//            HttpURLConnection connection = (HttpURLConnection) url
//                    .openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            return myBitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


}
