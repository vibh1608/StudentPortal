package com.example.ashutosh.studentportal.adapter;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashutosh.studentportal.BaseApplication;
import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.interfaces.OnQuestionItemClickListener;
import com.example.ashutosh.studentportal.modal.QuestionDetails;
import com.example.ashutosh.studentportal.preference.MyPreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.dbRef;
import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.questionList;
import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.userDetails;
import java.util.*;

/**
 * Created by Ashutosh on 07-03-2018.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    Context context;
    List<QuestionDetails> data;
    DatabaseReference dbRef;
    OnQuestionItemClickListener clickListener;
    public static ImageLoader imageLoader;

    public QuestionAdapter(Context context,List<QuestionDetails> data,OnQuestionItemClickListener listener)
    {
        this.context = context;
        this.data = data;
        clickListener = listener;
        dbRef = FirebaseDatabase.getInstance().getReference();
        BaseApplication.initImageLoader(context);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.question_row, null);
        QuestionAdapter.ViewHolder viewHolder = new QuestionAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onCardClick(v,position);
            }
        });


        dbRef.child("users").child(data.get(position).getQuetionerEmail().substring(0,data.get(position).getQuetionerEmail().indexOf('@'))).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("ProfileURL").exists() && !dataSnapshot.child("ProfileURL").getValue().equals("null"))
                {
                    String profileUrl = dataSnapshot.child("ProfileURL").getValue().toString();
                    data.get(position).setQuetionerProfileURL(profileUrl);
                    //questionList = data;
                    MyPreferenceManager.setQuestionList(context,questionList);
                    try {
                        URL url = new URL(profileUrl);
                        Uri uri = Uri.parse(url.toURI().toString());
                        imageLoader.loadImage(String.valueOf(uri), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                holder.profileIV.setImageBitmap(loadedImage);
                            }
                        });

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        //Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                    holder.profileIV.setImageDrawable(context.getResources().getDrawable(R.drawable.default_profile_image));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.nameOfQuestioner.setText(data.get(position).getQuetionerName());
        holder.emailOfQuestioner.setText(data.get(position).getQuetionerEmail());
        holder.questionTV.setText(data.get(position).getQuestion());
        holder.numberOfLikes.setText(data.get(position).getLikes());
        if(data.get(position).getIsUpvoted()=="true")
            holder.likeBtnIV.setImageDrawable(context.getResources().getDrawable(R.drawable.like_solid_icon));
        else
            holder.likeBtnIV.setImageDrawable(context.getResources().getDrawable(R.drawable.like_hollow_icon));
        holder.timeTV.setText(data.get(position).getTime());
        holder.dateTV.setText(data.get(position).getDate());
        holder.likeBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.get(position).getIsUpvoted()=="true")
                {
                    holder.likeBtnIV.setImageDrawable(context.getResources().getDrawable(R.drawable.like_hollow_icon));
                    data.get(position).setIsUpvoted("false");
                    int l=Integer.parseInt(data.get(position).getLikes());
                    data.get(position).setLikes(String.valueOf(l-1));
                    holder.numberOfLikes.setText(data.get(position).getLikes());
                }
                else
                {
                    holder.likeBtnIV.setImageDrawable(context.getResources().getDrawable(R.drawable.like_solid_icon));
                    data.get(position).setIsUpvoted("true");
                    int l=Integer.parseInt(data.get(position).getLikes());
                    data.get(position).setLikes(String.valueOf(l+1));
                    holder.numberOfLikes.setText(data.get(position).getLikes());
                }
                //questionList = data;
                notifyDataSetChanged();
                MyPreferenceManager.setQuestionList(BaseApplication.getDefaultContext(),data);
                Toast.makeText(context, data.get(position).getParentKey(), Toast.LENGTH_SHORT).show();
                dbRef.child("questions").child(data.get(position).getParentKey()).child("Likes").setValue(data.get(position).getLikes());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView parent;
        CircleImageView profileIV;
        TextView nameOfQuestioner;
        TextView emailOfQuestioner;
        TextView questionTV;
        TextView numberOfLikes;
        ImageView likeBtnIV;
        TextView timeTV;
        TextView dateTV;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = (CardView)itemView.findViewById(R.id.parent_card);
            profileIV =(CircleImageView)itemView.findViewById(R.id.profile_civ);
            nameOfQuestioner = (TextView)itemView.findViewById(R.id.tv_name);
            emailOfQuestioner = (TextView)itemView.findViewById(R.id.tv_email);
            questionTV = (TextView) itemView.findViewById(R.id.tv_question);
            numberOfLikes = (TextView)itemView.findViewById(R.id.number_of_likes_tv);
            likeBtnIV = (ImageView)itemView.findViewById(R.id.like_btn_iv);
            timeTV = (TextView)itemView.findViewById(R.id.time_tv);
            dateTV = (TextView)itemView.findViewById(R.id.date_tv);

        }

    }

    public void notifyData(List<QuestionDetails> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.data = myList;
        notifyDataSetChanged();
    }


}
