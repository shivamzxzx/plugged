package com.example.flamboyant.plugged.main;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by shivam on 8/7/2018.
 */

public class MatchIDs {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String Age,Sex;

    public MatchIDs(String userId, String name, String profileImageUrl,String Age,String Sex){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.Age = Age;
        this.Sex = Sex;
    }



    public String getUserId(){
        return userId;
    }
    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getprofileImageUrl(){

        return profileImageUrl;
    }
    public void setprofileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
    public String getAge(){
        return Age;
    }
    public void setAge(String Age){
        this.Age = Age;
    }

    public String getSex(){
        return Sex;
    }
    public void setSex(String Sex){
        this.Sex = Sex;
    }

}
