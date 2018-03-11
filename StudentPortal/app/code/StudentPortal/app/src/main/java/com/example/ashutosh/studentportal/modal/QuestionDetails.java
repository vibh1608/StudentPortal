package com.example.ashutosh.studentportal.modal;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.*;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ashutosh on 07-03-2018.
 */

public class QuestionDetails implements Parcelable,Serializable {

    @SerializedName("ParentKey")
    @Expose
    private String parentKey;
    @SerializedName("QuetionerName")
    @Expose
    private String quetionerName;
    @SerializedName("QuetionerEmail")
    @Expose
    private String quetionerEmail;
    @SerializedName("QuetionerProfileURL")
    @Expose
    private String quetionerProfileURL;
    @SerializedName("Question")
    @Expose
    private String question;
    @SerializedName("Likes")
    @Expose
    private String likes;
    @SerializedName("Time")
    @Expose
    private String time;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("isUpvoted")
    @Expose
    private String isUpvoted;
    @SerializedName("answerList")
    @Expose
    private List<AnswerDetails> answerDetailsList;

    protected QuestionDetails(Parcel in) {
        parentKey = in.readString();
        quetionerName = in.readString();
        quetionerEmail = in.readString();
        quetionerProfileURL = in.readString();
        question = in.readString();
        likes=in.readString();
        time=in.readString();
        date=in.readString();
        isUpvoted=in.readString();
        if(answerDetailsList != null)
            in.readList(answerDetailsList,null);
    }

    public QuestionDetails()
    {
        isUpvoted = "false";
        likes = "0";
        answerDetailsList = null;
    }

    public void addToAnswers(AnswerDetails answerDetails)
    {
        if(answerDetailsList==null)
            answerDetailsList = new ArrayList<AnswerDetails>();
        answerDetailsList.add(0,answerDetails);
    }

    public List<AnswerDetails> getAnswerDetailsList() {
        return answerDetailsList;
    }

    public void setAnswerDetailsList(List<AnswerDetails> answerDetailsList) {
        this.answerDetailsList = answerDetailsList;
    }

    public String getQuetionerName() {
        return quetionerName;
    }

    public void setQuetionerName(String quetionerName) {
        this.quetionerName = quetionerName;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getQuetionerEmail() {
        return quetionerEmail;
    }

    public void setQuetionerEmail(String quetionerEmail) {
        this.quetionerEmail = quetionerEmail;
    }

    public String getQuetionerProfileURL() {
        return quetionerProfileURL;
    }

    public void setQuetionerProfileURL(String quetionerProfileURL) {
        this.quetionerProfileURL = quetionerProfileURL;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsUpvoted() {
        return isUpvoted;
    }

    public void setIsUpvoted(String isUpvoted) {
        this.isUpvoted = isUpvoted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parentKey);
        dest.writeString(quetionerName);
        dest.writeString(quetionerEmail);
        dest.writeString(quetionerProfileURL);
        dest.writeString(question);
        dest.writeString(likes);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(isUpvoted);
        dest.writeList(answerDetailsList);
    }

    public static final Parcelable.Creator<QuestionDetails> CREATOR = new Parcelable.Creator<QuestionDetails>() {
        @Override
        public QuestionDetails createFromParcel(Parcel in) {
            return new QuestionDetails(in);
        }

        @Override
        public QuestionDetails[] newArray(int size) {
            return new QuestionDetails[size];
        }
    };

}
