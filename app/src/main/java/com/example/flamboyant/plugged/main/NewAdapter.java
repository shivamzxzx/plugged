package com.example.flamboyant.plugged.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**import com.bumptech.glide.Glide; */

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.flamboyant.plugged.Chat.ChatActivity;
import com.example.flamboyant.plugged.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.youtube.player.internal.v;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
/**
 * Created by shivam on 8/7/2018.
 */

public class NewAdapter extends ArrayAdapter<MatchIDs>{
    Context context;
    private static final String TAG = "MyActivity";
    public  String Username;

    public NewAdapter(Context context, int resourceId, List<MatchIDs> items){
        super(context, resourceId, items);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        MatchIDs card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rows, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.Itemname);
        ImageView image = (ImageView) convertView.findViewById(R.id.ItemImage);
        TextView Age = (TextView) convertView.findViewById(R.id.ItemAge);
        TextView userid= (TextView) convertView.findViewById(R.id.personid);
        TextView userSex = (TextView) convertView.findViewById(R.id.Sex);


        userSex.setText(card_item.getSex());
        userid.setText(card_item.getUserId());
        name.setText(card_item.getName());
        Age.setText(card_item.getAge());
        Glide.with(getContext()).load(card_item.getprofileImageUrl()).into(image);


        return convertView;

    }



}

