package com.example.ashutosh.studentportal.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashutosh.studentportal.BaseApplication;
import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.interfaces.OnQuestionItemClickListener;
import com.example.ashutosh.studentportal.modal.AnswerDetails;
import com.example.ashutosh.studentportal.modal.QuestionDetails;
import com.example.ashutosh.studentportal.preference.MyPreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.hdodenhof.circleimageview.CircleImageView;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static com.example.ashutosh.studentportal.ui.AnswerActivity.finalPos;
import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.dbRef;
import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.questionList;

/**
 * Created by Ashutosh on 09-03-2018.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    Context context;
    List<AnswerDetails> data;
    DatabaseReference dbRef;
    OnQuestionItemClickListener clickListener;
    public static ImageLoader imageLoader;


    public AnswerAdapter(Context context, List<AnswerDetails> data,OnQuestionItemClickListener listener)
    {
        this.context = context;
        this.data = data;
        clickListener = listener;
        dbRef = FirebaseDatabase.getInstance().getReference();
        BaseApplication.initImageLoader(context);
        imageLoader = ImageLoader.getInstance();
    }


    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complete_answer_card, null);
        AnswerAdapter.ViewHolder viewHolder = new AnswerAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //holder.profileIV.setImageDrawable(context.getResources().getDrawable(R.drawable.default_profile_image));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onCardClick(v,position);
            }
        });

        dbRef.child("users").child(data.get(position).getAnswererEmail().substring(0,data.get(position).getAnswererEmail().indexOf('@'))).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("ProfileURL").exists() && !dataSnapshot.child("ProfileURL").getValue().equals("null"))
                {
                    String profileUrl = dataSnapshot.child("ProfileURL").getValue().toString();
                    //MyPreferenceManager.setQuestionList(context,questionList);
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

        holder.nameOfQuestioner.setText(data.get(position).getAnswererName());
        holder.emailOfQuestioner.setText(data.get(position).getAnswererEmail());
        holder.questionTV.setText(data.get(position).getAnswer());
        holder.numberOfLikes.setText(data.get(position).getUpvote());
        if(data.get(position).getIsUpvoted()=="true")
            holder.likeBtnIV.setImageDrawable(context.getResources().getDrawable(R.drawable.upvote_solid_drawable));
        else
            holder.likeBtnIV.setImageDrawable(context.getResources().getDrawable(R.drawable.upvote_hollow_drawable));
        holder.timeTV.setText(data.get(position).getTime());
        holder.dateTV.setText(data.get(position).getDate());
        holder.likeBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.get(position).getIsUpvoted()=="true")
                {
                    holder.likeBtnIV.setImageDrawable(context.getResources().getDrawable(R.drawable.upvote_solid_drawable));
                    data.get(position).setIsUpvoted("false");
                    int l=Integer.parseInt(data.get(position).getUpvote());
                    data.get(position).setUpvote(String.valueOf(l-1));
                    holder.numberOfLikes.setText(data.get(position).getUpvote());
                }
                else
                {
                    holder.likeBtnIV.setImageDrawable(context.getResources().getDrawable(R.drawable.upvote_hollow_drawable));
                    data.get(position).setIsUpvoted("true");
                    int l=Integer.parseInt(data.get(position).getUpvote());
                    data.get(position).setUpvote(String.valueOf(l+1));
                    holder.numberOfLikes.setText(data.get(position).getUpvote());
                }
                questionList.get(finalPos).setAnswerDetailsList(data);
                notifyDataSetChanged();
                MyPreferenceManager.setQuestionList(BaseApplication.getDefaultContext(),questionList);
                Toast.makeText(context, data.get(position).getParentKey(), Toast.LENGTH_SHORT).show();
                dbRef.child("questions").child(questionList.get(finalPos).getParentKey()).child("answers").child(data.get(position).getParentKey()).child("Upvote").setValue(data.get(position).getUpvote());
                //dbRef.child("questions").child(data.get(position).getParentKey()).child("Likes").setValue(data.get(position).getUpvote());
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
            questionTV = (TextView) itemView.findViewById(R.id.tv_answer);
            numberOfLikes = (TextView)itemView.findViewById(R.id.number_of_likes_tv);
            likeBtnIV = (ImageView)itemView.findViewById(R.id.like_btn_iv);
            timeTV = (TextView)itemView.findViewById(R.id.time_tv);
            dateTV = (TextView)itemView.findViewById(R.id.date_tv);

        }

    }

    public void notifyData(List<AnswerDetails> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.data = myList;
        notifyDataSetChanged();
    }

}
