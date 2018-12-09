package com.example.flamboyant.plugged.Chat;



import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.example.flamboyant.plugged.R;
import com.example.flamboyant.plugged.main.MainActivity;
import com.example.flamboyant.plugged.main.MatchActivity;
import com.example.flamboyant.plugged.main.MatchPageActivity;
import com.example.flamboyant.plugged.main.YoutubePlayerActivity;
import com.example.flamboyant.plugged.utils.RecyclerViewOnClickListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView , mMedia;
    private RecyclerView.Adapter mChatAdapter,mMediaAdapter;;
    private NestedScrollView mScrollView;
    private android.support.v7.widget.Toolbar mToolbar;
    private TextView personName;
    private  String currentUserID,ChatID;
    private  static final String TAG = "MyActivity";

    private RecyclerView.LayoutManager mChatmanager;
    private EditText mMessage;
    private Button mSend;
    private String CurrentUserSex;
    private Uri resultUri;
    boolean isImageFitToScreen;
    private FirebaseAuth mAuth;

    public DatabaseReference mDatabaseUser,mDatabaseChat;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        int count=0;
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        final String ChatPerson=(String) args.getSerializable("Username");
        final String MatchPersonId = (String) args.getSerializable("customerid");
        final String MatchedPersonSex = (String) args.getSerializable("Sex");
        if(MatchedPersonSex == "Female"){
            CurrentUserSex = "Male";
        }
        else{
            CurrentUserSex = "Female";
        }




        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(MatchedPersonSex).child(MatchPersonId).child("Connections").child(currentUserID).child("ChatID");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatID();


        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        mToolbar.setTitle(null);
        setSupportActionBar(mToolbar);
        personName = (TextView) findViewById(R.id.person_name);
        Log.d(TAG,ChatPerson);
        personName.setText(ChatPerson);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button mMediaButton = (Button) findViewById(R.id.media);
        mMessage = (EditText) findViewById(R.id.message);
        mSend = (Button) findViewById(R.id.send);

        mMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                mMessage.requestFocus();

            }
        });
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mMedia= (RecyclerView) findViewById(R.id.medialist14);
        mMedia.setHasFixedSize(false);
        mRecyclerView.setHasFixedSize(false);
        mChatAdapter = new ChatAdapter(getDataSetChat(),ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);
        initializeMedia();
    }


    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    private void sendMessage() {
        String messageId = mDatabaseChat.push().getKey();
        String sendMessageText = mMessage.getText().toString();
        final DatabaseReference newChatdb = mDatabaseChat.push();
        final Map newMessage = new HashMap();
        if(!sendMessageText.isEmpty()){
             newMessage.put("CreatedByUser",currentUserID);
             newMessage.put("text",sendMessageText);

             newChatdb.setValue(newMessage);
             }
        if(!mediaUriList.isEmpty()){
            if(sendMessageText.isEmpty()){
                newMessage.put("text","Image");
            }
             for (String mediaUri : mediaUriList){
                String mediaId = newChatdb.child("media").push().getKey();
                mediaIdList.add(mediaId);
                final StorageReference filePath1 = FirebaseStorage.getInstance().getReference().child("SharedMedia").child(ChatID).child(messageId).child(mediaId);

                UploadTask uploadTask = filePath1.putFile(Uri.parse(mediaUri));

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {


                                newMessage.put("CreatedByUser",currentUserID);
                                newMessage.put("media/" + mediaIdList.get(totalMediaUploaded)+ "/" , uri.toString());
                                totalMediaUploaded++;
                                Log.d(TAG, String.valueOf(mediaIdList.size()));
                                if(totalMediaUploaded == mediaUriList.size())
                                    updateDatabaseWithNewMessage(newChatdb, newMessage);
                                    mMediaAdapter.notifyDataSetChanged();

                            }
                        });
                    }
                });
            }
        }
     mMessage.setText(null);




    }
    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDb, Map newMessageMap){
        newMessageDb.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        totalMediaUploaded=0;
        mMediaAdapter.notifyDataSetChanged();
    }

    private void getChatID() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatID = dataSnapshot.getValue().toString();
                mDatabaseChat = mDatabaseChat.child(ChatID);
                getChatMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();


    private void initializeMedia() {
        mediaUriList = new ArrayList<>();

        final LinearLayoutManager mMediaLayoutManager = new LinearLayoutManager(ChatActivity.this,LinearLayout.HORIZONTAL, false);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
        mMedia.addOnItemTouchListener(new RecyclerViewOnClickListener(this, new RecyclerViewOnClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final ImageView mPicShare = (ImageView) view.findViewById(R.id.mediaShare);
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    mPicShare.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    mPicShare.setAdjustViewBounds(true);
                }else{
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    isImageFitToScreen=true;
                    mPicShare.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    mPicShare.getLayoutParams().height=displayMetrics.heightPixels;
                    mPicShare.getLayoutParams().width=displayMetrics.widthPixels;
                }



            }
        }));
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            if(requestCode == PICK_IMAGE_INTENT){
                if(data.getClipData() == null){
                    mediaUriList.add(data.getData().toString());

                }else{
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }
    private void saveMedia() {

        final DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(ChatID);
        String userId = mAuth.getInstance().getCurrentUser().getUid();
        if(resultUri != null){
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("SharedMedia").child(userId);
            Bitmap bitmap=null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);

            } catch (IOException e) {
                e.printStackTrace();

            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            final UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> downloadUrl = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                DatabaseReference newChatdbb = mCustomerDatabase.push();
                                Map newMessages = new HashMap();
                                newMessages.put("CreatedByUser",currentUserID);
                                newMessages.put("media",downloadUri.toString());

                                newChatdbb.setValue(newMessages);
                            } else {
                                finish();
                            }
                        }
                    });

                    finish();
                    return;
                }
            });
        }else{
            finish();
        }
    }

    private void getChatMessages() {
        final LinearLayoutManager mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setNestedScrollingEnabled(false);
        mChatLayoutManager.setReverseLayout(false);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        final ArrayList<String> mediaUrlList = new ArrayList<>();
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String message = null;
                String CreatedByUser=null;


                if(dataSnapshot.child("text").getValue()!=null){
                    message = dataSnapshot.child("text").getValue().toString();
                    }
                if(dataSnapshot.child("CreatedByUser").getValue()!=null){
                    CreatedByUser = dataSnapshot.child("CreatedByUser").getValue().toString();
                }
                if(dataSnapshot.child("media").getChildrenCount() > 0){
                    for(DataSnapshot mediasnapshot : dataSnapshot.child("media").getChildren()){
                        mediaUrlList.add(mediasnapshot.getValue().toString());
                        Log.d(TAG, String.valueOf(mediaUrlList.size()));
                    }
                }
                if(message!=null && CreatedByUser!=null){
                    Boolean currentUserBoolean=false;
                    if(CreatedByUser.equals(currentUserID)){
                        currentUserBoolean=true;
                    }

                    ChatObject newMessage = new ChatObject(message,currentUserBoolean,mediaUrlList);
                    resultChat.add(newMessage);
                    mChatLayoutManager.scrollToPosition(resultChat.size()-1);
                    mChatAdapter.notifyDataSetChanged();
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


    private ArrayList<ChatObject> resultChat = new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetChat(){return  resultChat;}
}
