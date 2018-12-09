package com.example.flamboyant.plugged.main;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Response;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;

class PopularFeeds extends AsyncTask<String, Void, pFeed> {
    private  static final String TAG = "MyActivity";
    Context context;

    List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
    private static final long NUMBER_OF_VIDEOS_RETURNED = 20;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */

    private YouTube youtube;
    private  YouTube youtube1;
    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     *
     */
    public interface AsyncResponse {
        void processFinish(Object[] output);
    }

    public AsyncResponse delegate = null;

    public PopularFeeds(AsyncResponse delegate){
        this.delegate = delegate;
    }
    @Override
    public pFeed doInBackground(String... params) {
        // Read the developer key from the properties file.


        try {



            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();


            String apiKey = "AIzaSyCkrqOrq6Hzqk_H-h0JtcuKeHXQ0R2P4FQ";
            // Prompt the user to enter a query term.
            String queryTerm = getInputQuery();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            YouTube.Videos.List vedolist = youtube.videos().list("snippet,contentDetails");
            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}


            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();

            List<SearchResult> searchResultList = searchResponse.getItems();

            if (searchResultList != null) {
                pFeed p = new pFeed();

                p.pfData = prettyPrint(searchResultList.iterator(), queryTerm,vedolist);
                return p;

            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }


    @Override
    public void onPostExecute(pFeed aVoid) {
        super.onPostExecute(aVoid);
        Object[] obj1 = aVoid.pfData;
        delegate.processFinish(obj1);

    }




    /*
     * Prompt the user to enter a query term and return the user-specified term.
     */
    private  String getInputQuery() throws IOException {

        String inputQuery = "";


        inputQuery = "trending songs";


        return inputQuery;
    }



    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private String[] videoIDs;
    private String[] video_titles;
    private String[] D_uration;
    public int x=1;




    @TargetApi(Build.VERSION_CODES.O)
    private Object[] prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query, YouTube.Videos.List vedolist) throws IOException, GeneralSecurityException {
        int i = 0;

        x=x+1;
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");


        //initializing array to store videoID
        videoIDs = new String[20];
        video_titles = new String[20];
        D_uration = new String[20];
        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            vedolist.setId(rId.getVideoId());
            vedolist.setKey("AIzaSyCkrqOrq6Hzqk_H-h0JtcuKeHXQ0R2P4FQ");
            List<Video> vedo = vedolist.execute().getItems();


            Iterator<Video> videos=vedo.iterator();


            Video single = videos.next();





            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                videoIDs[i] = rId.getVideoId();
                video_titles[i] = singleVideo.getSnippet().getTitle();
                String Apiduration = single.getContentDetails().getDuration();
                String Apiduration1=Apiduration.replace("PT","");


                if(Apiduration.indexOf("H")>=0){
                    String Apiduration2=Apiduration1.replace("H",":");
                    if(Apiduration.indexOf("M")>=0){
                        String Apiduration3=Apiduration2.replace("M",":");
                        if(Apiduration.indexOf("S")>=0){
                            D_uration[i]=Apiduration3.replace("S","");


                        }
                        else{
                            String Apiduration4=Apiduration2.replace("M",":00");
                            D_uration[i]=Apiduration4;
                        }
                    }
                    else{
                        String Apiduration4=Apiduration2.replace(":",":00:");
                        if(Apiduration.indexOf("S")>=0){
                            D_uration[i]=Apiduration4.replace("S","");


                        }
                        else{
                            String Apiduration3=Apiduration4.replace(":00:",":00:00");
                            D_uration[i]=Apiduration3;
                        }
                    }
                }
                else{

                    if(Apiduration.indexOf("M")>=0){
                        String Apiduration2=Apiduration1.replace("M",":");
                        if(Apiduration.indexOf("S")>=0){
                            D_uration[i]=Apiduration2.replace("S","");


                        }
                        else{
                            String Apiduration4=Apiduration2.replace(":",":00");
                            D_uration[i]=Apiduration4;
                        }

                    }
                    else{

                        D_uration[i]=Apiduration1.replace("S","");
                    }
                }

                i = i + 1;
                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
                //Calling Video:list to get the durations
            }


        }

        return new Object[]{videoIDs,video_titles,D_uration};



    }




}


