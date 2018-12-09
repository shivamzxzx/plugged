package com.example.flamboyant.plugged.main;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.flamboyant.plugged.adapter.YoutubeVideoAdapter;
import com.example.flamboyant.plugged.model.YoutubeVideoModel;
import com.example.flamboyant.plugged.utils.RecyclerViewOnClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.common.api.Api;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Sleeper;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Lists;
import com.example.flamboyant.plugged.R;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;

import com.google.api.services.classroom.ClassroomScopes;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.TimeUnit;


public class SearchInterface extends AppCompatActivity implements PopularFeeds.AsyncResponse {
    private Button mSearchbutton;
    static Context context;
    private YoutubeVideoAdapter adapter;



    private EditText mSearchtext;
    private RecyclerView mPopularRecycler;
    private ProgressBar pbPopular;
    private  static final String TAG = "MyActivity";


    private static final String IMAGE_FILE_FORMAT = "image/png";

    private Toolbar mtoolbar;

    private static final List<String> SCOPES = Collections.singletonList(ClassroomScopes.CLASSROOM_COURSES_READONLY);

    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/java-youtube-api-tests");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY; // Directory to store user credentials.


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getBaseContext();
        setContentView(R.layout.activity_search_interface);
        Fresco.initialize(this);
        mSearchbutton = (Button) findViewById(R.id.searchbutton);
        mSearchtext = (EditText) findViewById(R.id.inputtext);
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mtoolbar);
        mPopularRecycler = (RecyclerView) findViewById(R.id.Popular_recycler);
        mPopularRecycler.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mPopularRecycler.setLayoutManager(linearLayoutManager);

        new PopularFeeds(this).execute();





        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun){

            //... Display the dialog message here ...
            AlertDialog alertDialog = new AlertDialog.Builder(
                    SearchInterface.this).create();

            // Setting Dialog Title
            alertDialog.setTitle("Quick guide");

            // Setting Dialog Message
            alertDialog.setMessage("Hey there! Search for your favourite youtube videos and see who else searched the same video." +
                    "Your matches will be shown below the video.Best of luck! Enjoy Pluggin;)");

            // Setting Icon to Dialog


            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Toast.makeText(getApplicationContext(), "Have fun ;)", Toast.LENGTH_SHORT).show();
                }
            });

            // Showing Alert Message
            alertDialog.show();
            // Save the state
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
        }

        mSearchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                class Auth {


                    /**
                     * Define a global instance of the HTTP transport.
                     */
                    public  final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

                    /**
                     * Define a global instance of the JSON factory.
                     */
                    public  final JsonFactory JSON_FACTORY = new JacksonFactory();

                    /**
                     * This is the directory that will be used under the user's home directory where OAuth tokens will be stored.
                     */
                    private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";


                    /**
                     * Authorizes the installed application to access user's protected data.
                     *  @param scopes              list of scopes needed to run youtube upload.
                     * @param credentialDatastore name of the credential datastore to cache OAuth tokens
                     */
                    public  com.google.api.client.auth.oauth2.Credential authorize(List<String> scopes, String credentialDatastore) throws IOException {

                        // Load client secrets.

                        Reader clientSecretReader = new InputStreamReader(Auth.class.getResourceAsStream("/client_secrets.json"));

                        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

                        // Checks that the defaults have been replaced (Default = "Enter X here").
                        if (clientSecrets.getDetails().getClientId().startsWith("Enter")) {

                            System.out.println(
                                    "Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/credential "
                                            + "into src/main/resources/client_secrets.json");
                            System.exit(1);
                        }


                        // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
                        String dataDir = SearchInterface.this.getApplicationInfo().dataDir;
                        String storePath = dataDir + File.separator + CREDENTIALS_DIRECTORY;


                        File file=new File(storePath);
                        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(file);


                        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentialDatastore);


                        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
                                .build();


                        // Build the local server and bind it to port 8080
                        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setHost("com.example.flamboyant.plugged.main").setPort(8080).build();


                            // Authorize.
                        //This line is the error line!!
                        Credential credential = new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");



                        return credential;


                    }
                }


                class Search extends AsyncTask<String, Void, Void> {

                    ProgressDialog pd;

                    List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
                    private static final long NUMBER_OF_VIDEOS_RETURNED = 20;

                    /**
                     * Define a global instance of a Youtube object, which will be used
                     * to make YouTube Data API requests.
                     */

                    private  YouTube youtube;
                    private  YouTube youtube1;
                    Auth auth2 = new Auth();

                    /**
                     * Initialize a YouTube object to search for videos on YouTube. Then
                     * display the name and thumbnail image of each video in the result set.
                     *
                     *
                     */
                    @Override
                    protected Void doInBackground(String... params) {
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


                            String apiKey = "YOUR API KEY";
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

                                prettyPrint(searchResultList.iterator(), queryTerm,vedolist);

                            }
                        }
                        catch (Throwable t) {
                            t.printStackTrace();
                        }
                        return null;
                    }




                    /*
                     * Prompt the user to enter a query term and return the user-specified term.
                     */
                    private  String getInputQuery() throws IOException {

                        String inputQuery = "";


                        inputQuery = mSearchtext.getText().toString();

                        if (inputQuery.length() < 1) {
                            // Use the string "YouTube Developers Live" as a default.
                            inputQuery = "YouTube Developers Live";
                        }
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
                    @TargetApi(Build.VERSION_CODES.O)
                    private  void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query, YouTube.Videos.List vedolist) throws IOException, GeneralSecurityException {
                        int i = 0;


                        System.out.println(
                                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");


                        //initializing array to store videoID
                        String[] videoIDs = new String[20];
                        String[] video_titles = new String[20];
                        String[] D_uration = new String[20];
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

                        Intent intent = new Intent(SearchInterface.this, MainActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) videoIDs);
                        args.putSerializable("titlelist", (Serializable) video_titles);
                        args.putSerializable("duration", (Serializable) D_uration);
                        intent.putExtra("BUNDLE", args);
                        startActivity(intent);


                    }



                }
                Search sr =new Search();
                sr.execute();
                Intent masuk = new Intent(SearchInterface.this,MainActivity.class);
                masuk.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(masuk);
                finish();
                return;


            }
        });

        //popluar Feeds code




    }


    private void setUpRecyclerView() {


    }
    private void populateRecyclerView(Object[] vidValues) {

        final ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList = generateDummyVideoList(vidValues);

        adapter = new YoutubeVideoAdapter(this, youtubeVideoModelArrayList);

            mPopularRecycler.setAdapter(adapter);




        //set click event
        mPopularRecycler.addOnItemTouchListener(new RecyclerViewOnClickListener(this, new RecyclerViewOnClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //start youtube player activity by passing selected video id via intent
                startActivity(new Intent(SearchInterface.this, YoutubePlayerActivity.class)
                        .putExtra("video_id", youtubeVideoModelArrayList.get(position).getVideoId()));


            }
        }));
    }



    private ArrayList<YoutubeVideoModel> generateDummyVideoList(Object[] vidvalues1) {

        ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList = new ArrayList<>();
        //get the video id array, title array and duration array from strings.xml
        Log.d(TAG,"done2");



            String[] videoIDArray = (String[]) vidvalues1[0];
            String[] videoTitleArray = (String[]) vidvalues1[1];
            String[] videoDurationArray = (String[]) vidvalues1[2];

            //loop through all items and add them to arraylist
            for (int i = 0; i < videoIDArray.length; i++) {

                YoutubeVideoModel youtubeVideoModel = new YoutubeVideoModel();
                youtubeVideoModel.setVideoId(videoIDArray[i]);
                youtubeVideoModel.setTitle(videoTitleArray[i]);
                youtubeVideoModel.setDuration(videoDurationArray[i]);


                youtubeVideoModelArrayList.add(youtubeVideoModel);

            }


        return youtubeVideoModelArrayList;

        }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();
        if( id==R.id.action_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SearchInterface.this, LoginRegistrationActivity.class);
                startActivity(intent);
                finish();

            return (true);
        }
        if(id==R.id.matchpage){
            Intent intent = new Intent(SearchInterface.this , MatchPageActivity.class);
            startActivity(intent);
            return (true);
        }

        if(id==R.id.profilesetting){

            Intent intent = new Intent(SearchInterface.this,Settings.class);

            startActivity(intent);
            finish();
        }


        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void processFinish(Object[] output) {
          populateRecyclerView(output);
    }
}
