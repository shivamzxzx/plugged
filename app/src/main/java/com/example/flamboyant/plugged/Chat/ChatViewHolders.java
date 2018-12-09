package com.example.flamboyant.plugged.Chat;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.flamboyant.plugged.R;

/**
 * Created by shiva on 15-08-2018.
 */

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener  {
    public TextView mMessage;
    public LinearLayout mContainer;
    public NestedScrollView mScroller;
    public RecyclerView mRecycle;
    public Button mViewMedia;

    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener((View.OnClickListener) this);
        mMessage = itemView.findViewById(R.id.textmessage);
        mContainer = itemView.findViewById(R.id.container);

        mRecycle = itemView.findViewById(R.id.recyclerView);
        mViewMedia =itemView.findViewById(R.id.viewMedia);
    }

    @Override
    public void onClick(View v) {

    }
}
