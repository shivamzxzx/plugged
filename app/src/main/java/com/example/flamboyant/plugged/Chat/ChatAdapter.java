package com.example.flamboyant.plugged.Chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.flamboyant.plugged.R;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.List;

/**
 * Created by shiva on 15-08-2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {
    private List<ChatObject> chatList;
    private Context context;
    private  static final String TAG = "MyActivity";


    public ChatAdapter(List<ChatObject> matchesList,Context context){
        this.chatList = matchesList;
        this.context = context;
    }
    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders(layoutview);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolders holder, int position) {
        holder.mMessage.setText(chatList.get(position).getMessage());
        if(chatList.get(position).getCurrentUser()){
            holder.mMessage.setGravity(Gravity.END);
            holder.mMessage.setTextColor(Color.parseColor("#404040"));
            holder.mMessage.setTextSize(15);
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.mContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.bubble_in) );
            }

        }
        else{
            holder.mMessage.setGravity(Gravity.START);
            holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            holder.mMessage.setTextSize(15);
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.mContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.bubble_out) );
            }

        }
        if (chatList.get(position).getMessage().equals("Image")){
            holder.mViewMedia.setVisibility(View.VISIBLE);
        }

        holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), chatList.get(holder.getAdapterPosition()).getMediaUrlList1())
                        .setStartPosition(holder.getAdapterPosition())
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }

}
