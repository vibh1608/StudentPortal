package com.example.ashutosh.studentportal.modal;

/**
 * Created by Ashutosh on 06-03-2018.
 */
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.*;

public class UserDetails implements Parcelable,Serializable {

    @SerializedName("ParentID")
    @Expose
    private String parentID;
    @SerializedName("emailID")
    @Expose
    private String emailID;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("ProfileURL")
    @Expose
    private String profileURL="";
    @SerializedName("ProfileURI")
    @Expose
    private String profileURI="";

    protected UserDetails(Parcel in) {
        parentID = in.readString();
        emailID = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        password = in.readString();
        profileURL=in.readString();
        profileURI=in.readString();
    }

    public UserDetails()
    {

    }

    public UserDetails(String parentID,String firstName,String lastName,String emailID,String password,String profileURL)
    {
        this.parentID = parentID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailID = emailID;
        this.password = password;
        this.profileURL = profileURL;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getProfileURI() {
        return profileURI;
    }

    public void setProfileURI(String profileURI) {
        this.profileURI = profileURI;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parentID);
        dest.writeString(emailID);
        dest.writeString(firstName);
        dest.writeString(password);
        dest.writeString(lastName);
        dest.writeString(profileURL);
        dest.writeString(profileURI);
    }

    public static final Parcelable.Creator<UserDetails> CREATOR = new Parcelable.Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

}
