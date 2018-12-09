package com.example.flamboyant.plugged.Chat;

import java.util.ArrayList;

/**
 * Created by shiva on 15-08-2018.
 */

public class ChatObject {
    private String  message;
    private Boolean currentUser;
    ArrayList<String> mediaUrlList;

    public ChatObject(String message,Boolean currentUser,ArrayList<String> mediaUrlList ){
        this.message = message;
        this.currentUser = currentUser;
        this.mediaUrlList = mediaUrlList;


    }
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }


    public Boolean getCurrentUser(){
        return currentUser;
    }
    public void setCurrentUser(Boolean currentUser){
        this.currentUser = currentUser;
    }

    public ArrayList<String> getMediaUrlList1() {
        return mediaUrlList;
    }
}
