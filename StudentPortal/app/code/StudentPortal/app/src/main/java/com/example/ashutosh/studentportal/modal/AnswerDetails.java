package com.example.ashutosh.studentportal.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ashutosh on 09-03-2018.
 */

public class AnswerDetails implements Parcelable,Serializable {

    @SerializedName("ParentKey")
    @Expose
    private String parentKey;
    @SerializedName("AnswererName")
    @Expose
    private String answererName;
    @SerializedName("AnswererEmail")
    @Expose
    private String answererEmail;
    @SerializedName("Answer")
    @Expose
    private String answer;
    @SerializedName("Upvote")
    @Expose
    private String upvote;
    @SerializedName("Time")
    @Expose
    private String time;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("isUpvoted")
    @Expose
    private String isUpvoted;

    protected AnswerDetails(Parcel in) {
        parentKey = in.readString();
        answererName = in.readString();
        answererEmail = in.readString();
        answer = in.readString();
        upvote=in.readString();
        time=in.readString();
        date=in.readString();
        isUpvoted=in.readString();
    }

    public AnswerDetails()
    {
        isUpvoted = "false";
        upvote = "0";
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getAnswererName() {
        return answererName;
    }

    public void setAnswererName(String answererName) {
        this.answererName = answererName;
    }

    public String getAnswererEmail() {
        return answererEmail;
    }

    public void setAnswererEmail(String answererEmail) {
        this.answererEmail = answererEmail;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUpvote() {
        return upvote;
    }

    public void setUpvote(String upvote) {
        this.upvote = upvote;
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
        dest.writeString(answererName);
        dest.writeString(answererEmail);
        dest.writeString(answer);
        dest.writeString(upvote);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(isUpvoted);
    }

    public static final Parcelable.Creator<AnswerDetails> CREATOR = new Parcelable.Creator<AnswerDetails>() {
        @Override
        public AnswerDetails createFromParcel(Parcel in) {
            return new AnswerDetails(in);
        }

        @Override
        public AnswerDetails[] newArray(int size) {
            return new AnswerDetails[size];
        }
    };

}
