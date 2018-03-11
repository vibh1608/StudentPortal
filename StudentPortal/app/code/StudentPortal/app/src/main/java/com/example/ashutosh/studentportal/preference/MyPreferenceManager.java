package com.example.ashutosh.studentportal.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.ashutosh.studentportal.modal.QuestionDetails;
import com.example.ashutosh.studentportal.modal.UserDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashutosh on 06-03-2018.
 */

public class MyPreferenceManager {

    public static void setUserDetail(Context context, UserDetails userDetails) {

        if (context != null) {
            Gson gson = new Gson();
            String objectString = gson.toJson(userDetails);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userDetails", objectString);
            editor.commit();
        }
    }

    public static UserDetails getUserDetail(Context context) {

        if (context != null) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();

            String userDetailString = preferences.getString("userDetails", "");
            Log.d("userDetails",userDetailString);
            if (!userDetailString.isEmpty()) {
                UserDetails userDetails = gson.fromJson(userDetailString, UserDetails.class);
                return userDetails;
            } else {
                //return  new UserDetail(null);
                return null;
            }
        } else
            return null;
    }

    public static  void setQuestionList(Context context, List<QuestionDetails> list)
    {
        if(context!=null)
        {
            Gson gson = new Gson();
            String objectString = gson.toJson(list);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("questionList",objectString);
            editor.commit();
            Log.d("questionList : ",objectString);
        }
    }

    public static ArrayList<QuestionDetails> getQuestionList(Context context)
    {
        if (context != null) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();

            String questionListString = preferences.getString("questionList", "");
            Log.d("questionDetails",questionListString);
            if (!questionListString.isEmpty()) {
                ArrayList<QuestionDetails> questionList = gson.fromJson(questionListString,new TypeToken<ArrayList<QuestionDetails>>(){}.getType());
                return questionList;
            } else {
                //return  new UserDetail(null);
                return null;
            }
        } else
            return null;
    }

    public static void deleteAllData(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}
