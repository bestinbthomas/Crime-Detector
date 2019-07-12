package com.eswar.crimedetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import org.apache.commons.text.WordUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForceEngagementsAdapter extends RecyclerView.Adapter<ForceEngagementsAdapter.CustomViewHolder> {
    private Context context;
    private List<ForceDetails.EngagementMethod> engagements;
    private final String tag = "tag";

    public ForceEngagementsAdapter(Context context, List<ForceDetails.EngagementMethod> engagements){
        this.context = context;
        this.engagements = engagements;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public View mView;
        public TextView engagementTitle, engagementDescription, engagementUrl;
        public ImageView engagementImage;
        public CustomViewHolder(View view){
            super(view);
            mView = view;

            engagementTitle = mView.findViewById(R.id.force_engagement_title);
            engagementDescription = mView.findViewById(R.id.force_engagement_description);
            engagementUrl = mView.findViewById(R.id.force_engagement_url);
            engagementImage = mView.findViewById(R.id.force_engagement_image);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        return new CustomViewHolder(layoutInflater.inflate(R.layout.engagement_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ForceDetails.EngagementMethod engagementMethod = engagements.get(position);
        String title = engagementMethod.getTitle();

        setText(holder.engagementTitle, WordUtils.capitalizeFully(title), "Title", holder.mView);
        setText(holder.engagementUrl, urlHtml(engagementMethod.getUrl()), "URL", holder.mView);
        setText(holder.engagementDescription, parseHtml(engagementMethod.getDescription()), "Description", holder.mView);

        try {
            String titleLower = title.toLowerCase();
            if(titleLower.contains("twitter")){
                holder.engagementImage.setImageResource(R.drawable.twitter);
            }
            else if(titleLower.contains("facebook")){
                holder.engagementImage.setImageResource(R.drawable.facebook);
            }
            else if(titleLower.contains("youtube")){
                holder.engagementImage.setImageResource(R.drawable.youtube);
            }
            else if(titleLower.contains("rss")){
                holder.engagementImage.setImageResource(R.drawable.rss);
            }
            else{
                try{
                    callBestIcon(holder.engagementImage, engagementMethod.getUrl());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            Log.d(tag, "In EngagementsAdapter: Exception in Setting Image");
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return engagements.size();
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

    private void setText(TextView textView, String text, String description, View v){
        if(text != null && !text.isEmpty()){
            try {
                textView.setText(text);

                if(description.equals("URL")){
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    textView.setLinkTextColor(v.getResources().getColor(R.color.blue));
                    Linkify.addLinks(textView, Linkify.WEB_URLS);
                }
                else if(description.equals("Description")){
                    textView.setLineSpacing(0f, 1.2f);
                }
            }
            catch (NullPointerException npe){
                Log.d(tag, "In EngagementsAdapter: Null Pointer Exception in Setting text for " + description);
            }
            catch (Exception e){
                Log.d(tag, "In EngagementsAdapter: Exception in Setting text for " + description);
                e.printStackTrace();
            }
        }
        else{
            textView.setVisibility(View.GONE);
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

    public void setImage(final ImageView imageView, String url){
        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(url, imageView, new ImageLoadingListener() {
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
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {

                Log.d(tag, "Image loading canceled");
            }
        });
    }

    private String parseHtml(String html){
        if(html != null && !html.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
            } else {
                return Html.fromHtml(html).toString();
            }
        }
        else { return null; }
    }

    public String getGoogleUrlFavicon(String url){
        if(url != null && !url.isEmpty()){
            return "https://www.google.com/s2/favicons?domain=" + url;
        }
        else{
            return "";
        }
    }

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
