package com.example.flamboyant.plugged.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flamboyant.plugged.Chat.ChatActivity;
import com.example.flamboyant.plugged.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    private MatchIDs Match_data[];
    private ListView mList;
    private String userSex;
    private String oppositeSex;

    private NewAdapter arrayAdapter;
    private static final String TAG = "MyActivity";
    private DatabaseReference connectionDb;
    private String currentUid;

    private FirebaseAuth mAuth;
    ListView lv;
    List<MatchIDs> rowItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        mList=(ListView) findViewById(R.id.list_view);

        Log.d(TAG,"on match activity");
        currentUid=mAuth.getInstance().getCurrentUser().getUid();
        checkuserSex();
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  Intent Chat = new Intent(MatchActivity.this,ChatActivity.class);
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
        final String videoId=(String) args.getSerializable("videoId");
        final DatabaseReference df2 = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeSex);
        connectionDb=FirebaseDatabase.getInstance().getReference().child("Users");
        rowItems=new ArrayList<MatchIDs>();
        connectionDb.child(userSex).child(currentUid).child("videoId").child(videoId).setValue(true);
        connectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userSex).child(currentUid).child("profileImageUrl").exists()){
                    Toast.makeText(getApplicationContext(),"Finding matches!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Complete your profile else other users can't find you!",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        arrayAdapter = new NewAdapter (MatchActivity.this,R.layout.rows,rowItems);

        TextView mEmpty = (TextView) findViewById(R.id.emptylistview);
        mList.setAdapter(arrayAdapter);

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
        mList.setEmptyView(mEmpty);


    }



}
