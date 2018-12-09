package com.example.flamboyant.plugged.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flamboyant.plugged.Chat.ChatActivity;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.example.flamboyant.plugged.R;
import com.example.flamboyant.plugged.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonu on 10/11/17.
 * <p>
 * ### Here you need to extend the activity with YouTubeBaseActivity otherwise it will crash the app  ###
 */

public class YoutubePlayerActivity extends YouTubeBaseActivity {
    private static final String TAG = YoutubePlayerActivity.class.getSimpleName();
    private String videoID;
    private YouTubePlayerView youTubePlayerView;
    private Button mButton;

    private String name;
    private String userId;
    private String currentUid;
    private ListView mList;
    private FirebaseAuth mAuth;
    private String userSex;
    private String oppositeSex;
    ListView lv;
    List<MatchIDs> rowItems;
    private DatabaseReference connectionDb;
    private NewAdapter arrayAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_player_activity);
        //get the video id
        videoID = getIntent().getStringExtra("video_id");
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        initializeYoutubePlayer();






        mList=(ListView) findViewById(R.id.list_view);

        Log.d(TAG,"on match activity");
        currentUid=mAuth.getInstance().getCurrentUser().getUid();
        checkuserSex();
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent Chat = new Intent(YoutubePlayerActivity.this,ChatActivity.class);
                String Username = ((TextView) view.findViewById(R.id.Itemname)).getText().toString();
                String personId = ((TextView) view.findViewById(R.id.personid)).getText().toString();
                String Sex = ((TextView) view.findViewById(R.id.Sex)).getText().toString();
                Bundle args = new Bundle();
                args.putSerializable("Username",Username);
                args.putSerializable("customerid",personId);
                args.putSerializable("Sex",Sex);
                Chat.putExtra("BUNDLE", args);
                startActivity(Chat);
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();

    }
    public void checkuserSex(){
        final FirebaseUser person= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference maleDb=FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(person.getUid())){
                    userSex="Male";
                    oppositeSex="Female";
                    OppositeSexUsers();

                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference FemaleDb=FirebaseDatabase.getInstance().getReference().child("Users").child("Female");
        FemaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(person.getUid())){

                    userSex="Female";
                    oppositeSex="Male";
                    OppositeSexUsers();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void OppositeSexUsers(){

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        final String videoId=(String) getIntent().getStringExtra("video_id");
        final DatabaseReference df2 = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeSex);
        connectionDb=FirebaseDatabase.getInstance().getReference().child("Users");
        rowItems=new ArrayList<MatchIDs>();
        connectionDb.child(userSex).child(currentUid).child("videoId").child(videoId).setValue(true);
        connectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userSex).child(currentUid).child("profileImageUrl").exists()){
                    Toast.makeText(getApplicationContext(),"Finding matches!",Toast.LENGTH_SHORT);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Complete your profile else other users can't find you!",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        arrayAdapter = new NewAdapter (YoutubePlayerActivity.this,R.layout.rows,rowItems);

        TextView mEmpty = (TextView) findViewById(R.id.ifempty);
        mList.setAdapter(arrayAdapter);
        mList.setEmptyView(mEmpty);

        df2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot user : dataSnapshot.getChildren()){
                    if(user.child("videoId").exists() && user.child("profileImageUrl").exists() && user.child("Age").exists()){
                        for(DataSnapshot connected : user.child("videoId").getChildren()){
                            if(connected.getKey().equals(videoId)){
                                String Key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                                connectionDb.child(userSex).child(currentUid).child("Connections").child(user.getKey()).child("ChatID").setValue(Key);
                                df2.child(user.getKey()).child("Connections").child(currentUid).child("ChatID").setValue(Key);

                                MatchIDs item= new MatchIDs(user.getKey(),user.child("name").getValue().toString(),user.child("profileImageUrl").getValue().toString(),user.child("Age").getValue().toString(),user.child("Sex").getValue().toString());
                                rowItems.add(item);
                                arrayAdapter.notifyDataSetChanged();
                                break;
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    /**
     * initialize the youtube player
     */
    private void initializeYoutubePlayer() {
        youTubePlayerView.initialize(Constants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer,
                                                boolean wasRestored) {

                //if initialization success then load the video id to youtube player
                if (!wasRestored) {
                    //set the player style here: like CHROMELESS, MINIMAL, DEFAULT
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                    //load the video
                    youTubePlayer.loadVideo(videoID);

                    //OR

                    //cue the video
                    //youTubePlayer.cueVideo(videoID);

                    //if you want when activity start it should be in full screen uncomment below comment
                    //  youTubePlayer.setFullscreen(true);

                    //If you want the video should play automatically then uncomment below comment
                    //  youTubePlayer.play();

                    //If you want to control the full screen event you can uncomment the below code
                    //Tell the player you want to control the fullscreen change
                   /*player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                    //Tell the player how to control the change
                    player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean arg0) {
                            // do full screen stuff here, or don't.
                            Log.e(TAG,"Full screen mode");
                        }
                    });*/

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                //print or show error if initialization failed
                Log.e(TAG, "Youtube Player View initialization failed");
            }
        });
    }

}
