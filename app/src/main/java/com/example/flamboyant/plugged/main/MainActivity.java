package com.example.flamboyant.plugged.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.example.flamboyant.plugged.R;
import com.example.flamboyant.plugged.adapter.YoutubeVideoAdapter;
import com.example.flamboyant.plugged.model.YoutubeVideoModel;
import com.example.flamboyant.plugged.utils.RecyclerViewOnClickListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar spinner;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (ProgressBar) findViewById(R.id.pb);

        setUpRecyclerView();
        populateRecyclerView();

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        Intent masuk=new Intent(MainActivity.this,SearchInterface.class);
        masuk.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(masuk);
        finishAffinity();
        return;

    }




    /**
     * setup the recyclerview here
     */
    private void setUpRecyclerView() {

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * populate the recyclerview and implement the click event here
     */
    private void populateRecyclerView() {
        final ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList = generateDummyVideoList();

        YoutubeVideoAdapter adapter = new YoutubeVideoAdapter(this, youtubeVideoModelArrayList);
        recyclerView.setAdapter(adapter);

        //set click event
        recyclerView.addOnItemTouchListener(new RecyclerViewOnClickListener(this, new RecyclerViewOnClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //start youtube player activity by passing selected video id via intent
                startActivity(new Intent(MainActivity.this, YoutubePlayerActivity.class)
                        .putExtra("video_id", youtubeVideoModelArrayList.get(position).getVideoId()));
                spinner.setVisibility(View.GONE);

            }
        }));
    }


    /**
     * method to generate dummy array list of videos
     *
     * @return
     */
    private ArrayList<YoutubeVideoModel> generateDummyVideoList() {

        ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList = new ArrayList<>();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if(args!=null) {
            String[] videoID = (String[]) args.getSerializable("ARRAYLIST");
            String[] titles = (String[]) args.getSerializable("titlelist");
            String[] durations = (String[]) args.getSerializable("duration");





            //get the video id array, title array and duration array from strings.xml


            String[] videoIDArray = videoID;
            String[] videoTitleArray = titles;
            String[] videoDurationArray = durations;

            //loop through all items and add them to arraylist
            for (int i = 0; i < videoIDArray.length; i++) {

                YoutubeVideoModel youtubeVideoModel = new YoutubeVideoModel();
                youtubeVideoModel.setVideoId(videoIDArray[i]);
                youtubeVideoModel.setTitle(videoTitleArray[i]);
                youtubeVideoModel.setDuration(videoDurationArray[i]);


                youtubeVideoModelArrayList.add(youtubeVideoModel);

            }
        }
        return youtubeVideoModelArrayList;
    }

}
