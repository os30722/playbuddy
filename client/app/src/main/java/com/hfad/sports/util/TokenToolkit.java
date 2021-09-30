package com.hfad.sports.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.hfad.sports.vo.Token;

public class TokenToolkit {

    private static final String AUTH_PREFS = "auth_token";
    private static SharedPreferences mPreference;
    public static String ACCESS_TOKEN_KEY = "accesstoken";
    public static String REFRESH_TOKEN_KEY = "refreshtoken";
    public static String USER_ID = "user_id";

    private TokenToolkit(){}

    public static void init(Context context){
        if (mPreference == null)
            mPreference = context.getSharedPreferences(AUTH_PREFS, Activity.MODE_PRIVATE);

    }

    public static void saveTokens(Token tokens){
        setUid(tokens.getUserId());
        setAccessToken(tokens.getAccessToken());
        setRefreshToken(tokens.getRefreshToken());
    }

    public static int getUid(){
        return mPreference.getInt(USER_ID, -1);
    }

    public static void setUid(int uid){
        mPreference.edit().putInt(USER_ID, uid).apply();
    }


    public static String getAccessToken(){
        return mPreference.getString(ACCESS_TOKEN_KEY, null);
    }

    public static String getRefreshToken(){
        return mPreference.getString(REFRESH_TOKEN_KEY, null);
    }

    public static void setAccessToken(String token){
        mPreference.edit().putString(ACCESS_TOKEN_KEY, token).apply();
    }

    public static void setRefreshToken(String token){
        mPreference.edit().putString(REFRESH_TOKEN_KEY, token).apply();
    }

    public static void clearToken(){
        mPreference.edit().remove(REFRESH_TOKEN_KEY).apply();
    }

    public static boolean containsToken(){
        return mPreference.contains(REFRESH_TOKEN_KEY);
    }


}
