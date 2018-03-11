package com.example.ashutosh.studentportal.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashutosh.studentportal.BaseApplication;
import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.adapter.AnswerAdapter;
import com.example.ashutosh.studentportal.adapter.QuestionAdapter;
import com.example.ashutosh.studentportal.interfaces.OnQuestionItemClickListener;
import com.example.ashutosh.studentportal.modal.AnswerDetails;
import com.example.ashutosh.studentportal.modal.QuestionDetails;
import com.example.ashutosh.studentportal.preference.MyPreferenceManager;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.dbRef;
import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.questionList;
import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.userDetails;

/**
 * Created by Ashutosh on 09-03-2018.
 */

public class AnswerActivity extends AppCompatActivity {

    @BindView(R.id.question_card)
    CardView parentCard;
    @BindView(R.id.profile_civ)
    CircleImageView profileCIV;
    @BindView(R.id.tv_name)
    TextView nameTV;
    @BindView(R.id.tv_email)
    TextView emailTV;
    @BindView(R.id.tv_question)
    TextView questionTV;
    @BindView(R.id.like_btn_iv)
    ImageView likeBtnIV;
    @BindView(R.id.number_of_likes_tv)
    TextView numberOfLikesTV;
    @BindView(R.id.time_tv)
    TextView timeTV;
    @BindView(R.id.date_tv)
    TextView dateTV;
    @BindView(R.id.answer_tv)
    TextView answerBtnTV;
    @BindView(R.id.answers_rv)
    RecyclerView answerRV;
    @BindView(R.id.back_btn_iv)
    ImageView backBtnIV;
    @BindView(R.id.no_answer_text)
    TextView noAns;
    @BindView(R.id.answer_head)
    RelativeLayout answerHead;

    QuestionDetails questionDetails;
    public static ImageLoader imageLoader;
    public static DatabaseReference databaseReference;
    public DatabaseReference dbQRef;
    List<QuestionDetails> list;

    public static List<AnswerDetails> answerList;

    public static int finalPos;
    public String ansKey = "";
    public AnswerDetails answerDetails;
    AnswerAdapter answerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_answer);
        ButterKnife.bind(this);

        final int position = getIntent().getIntExtra("questionPosition",0);
        finalPos = position;

        list = questionList;

        questionDetails = list.get(position);

        dbQRef = FirebaseDatabase.getInstance().getReference().child("questions").child(questionDetails.getParentKey());

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(questionDetails.getQuetionerEmail().substring(0,questionDetails.getQuetionerEmail().indexOf('@')));

        BaseApplication.initImageLoader(this);
        imageLoader = ImageLoader.getInstance();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("ProfileURL").exists() && !dataSnapshot.child("ProfileURL").equals("null"))
                        {
                            questionDetails.setQuetionerProfileURL(dataSnapshot.child("ProfileURL").getValue().toString());
                            list.get(position).setQuetionerProfileURL(dataSnapshot.child("ProfileURL").getValue().toString());
                            MyPreferenceManager.setQuestionList(AnswerActivity.this,list);
                            try {
                                URL url = new URL(questionDetails.getQuetionerProfileURL());
                                Uri uri = Uri.parse(url.toURI().toString());
                                imageLoader.loadImage(String.valueOf(uri), new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        profileCIV.setImageBitmap(loadedImage);
                                    }
                                });

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                Toast.makeText(AnswerActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                                Toast.makeText(AnswerActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                            profileCIV.setImageDrawable(getResources().getDrawable(R.drawable.default_profile_image));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        nameTV.setText(questionDetails.getQuetionerName());
        emailTV.setText(questionDetails.getQuetionerEmail());
        questionTV.setText(questionDetails.getQuestion());
        numberOfLikesTV.setText(questionDetails.getLikes());

        if(questionDetails.getIsUpvoted().equals("true"))
            likeBtnIV.setImageDrawable(getResources().getDrawable(R.drawable.like_solid_icon));
        else
            likeBtnIV.setImageDrawable(getResources().getDrawable(R.drawable.like_hollow_icon));

        timeTV.setText(questionDetails.getTime());
        dateTV.setText(questionDetails.getDate());

        answerList = questionList.get(finalPos).getAnswerDetailsList();
        if(answerList==null)
            answerList = new ArrayList<AnswerDetails>();

        if(answerList.size()==0)
        {
            answerHead.setVisibility(View.GONE);
            noAns.setVisibility(View.VISIBLE);
            answerRV.setVisibility(View.GONE);
        }
        else
        {
            answerHead.setVisibility(View.VISIBLE);
            noAns.setVisibility(View.GONE);
            answerRV.setVisibility(View.VISIBLE);
        }

        answerAdapter = new AnswerAdapter(this, answerList, new OnQuestionItemClickListener() {
            @Override
            public void onCardClick(View view, int position) {
                Toast.makeText(AnswerActivity.this, "Transition", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AnswerActivity.this,UserProfileActivity.class);
                intent.putExtra("isFrom","answer");
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        answerRV.setAdapter(answerAdapter);
        answerRV.setLayoutManager(new LinearLayoutManager(this));

    }

    @OnClick(R.id.question_card)
    public void onQuestionCardClicked()
    {
        Toast.makeText(AnswerActivity.this, "Transition", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AnswerActivity.this,UserProfileActivity.class);
        intent.putExtra("isFrom","question");
        intent.putExtra("position",finalPos);
        startActivity(intent);
    }

    @OnClick(R.id.answer_tv)
    public void onAnswerBtnClicked()
    {
        final Dialog dialog = new Dialog(AnswerActivity.this);
        dialog.setContentView(R.layout.answer_dialog);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        final EditText answerET = (EditText)dialog.findViewById(R.id.answer_et);
        TextView postTV = (TextView) dialog.findViewById(R.id.post_tv);

        postTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!answerET.getText().toString().trim().isEmpty())
                {
                    dialog.dismiss();
                    ansKey = dbRef.child("questions").child("answers").push().getKey();
                    //questionDetails = new QuestionDetails();
                    if(userDetails == null)
                        userDetails = MyPreferenceManager.getUserDetail(BaseApplication.getDefaultContext());
                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String date = df.format(c.getTime());
                    SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
                    String time = dt.format(c.getTime());

                    if(questionDetails.getAnswerDetailsList()==null)
                        questionDetails.setAnswerDetailsList(new ArrayList<AnswerDetails>());

                    answerDetails = new AnswerDetails();

                    answerDetails.setParentKey(ansKey);
                    answerDetails.setAnswererName(userDetails.getFirstName()+" "+userDetails.getLastName());
                    answerDetails.setAnswererEmail(userDetails.getEmailID());
                    answerDetails.setAnswer(answerET.getText().toString().trim());
                    answerDetails.setUpvote("0");
                    answerDetails.setTime(time);
                    answerDetails.setDate(date);

                    //questionDetails.addToAnswers(answerDetails);
                    questionList.get(finalPos).addToAnswers(answerDetails);

                    /*questionDetails.setParentKey(key);
                    questionDetails.setQuetionerName(userDetails.getFirstName()+" "+userDetails.getLastName());
                    questionDetails.setQuetionerEmail(userDetails.getEmailID());
                    questionDetails.setQuetionerProfileURL(userDetails.getProfileURL());
                    questionDetails.setQuestion(takeQueET.getText().toString().trim());
                    questionDetails.setLikes("0");
                    questionDetails.setDate(date);
                    questionDetails.setTime(time);*/

                    //questionList.add(questionDetails);
                    //adapter.notifyData(questionList);
                    answerHead.setVisibility(View.VISIBLE);
                    noAns.setVisibility(View.GONE);
                    answerRV.setVisibility(View.VISIBLE);
                    answerAdapter.notifyData(questionList.get(finalPos).getAnswerDetailsList());
                    MyPreferenceManager.setQuestionList(AnswerActivity.this,questionList);
                    //new BaseDashboardActivity.UploaderTask().execute();
                    new AnswerUploaderTask().execute();

                }
                else
                {
                    answerET.setError("First fill the question!");
                }
            }
        });

    }

    public class AnswerUploaderTask extends AsyncTask<Void,Void,Void>
    {
        AnswerDetails tempAns = answerDetails;
        @Override
        protected Void doInBackground(Void... params) {
            if(tempAns!=null)
            {
                dbRef.child("questions").child(questionList.get(finalPos).getParentKey()).child("answers").child(tempAns.getParentKey()).child("AnswererName").setValue(tempAns.getAnswererName());
                dbRef.child("questions").child(questionList.get(finalPos).getParentKey()).child("answers").child(tempAns.getParentKey()).child("AnswererEmail").setValue(tempAns.getAnswererEmail());
                dbRef.child("questions").child(questionList.get(finalPos).getParentKey()).child("answers").child(tempAns.getParentKey()).child("Answer").setValue(tempAns.getAnswer());
                dbRef.child("questions").child(questionList.get(finalPos).getParentKey()).child("answers").child(tempAns.getParentKey()).child("Upvote").setValue(tempAns.getUpvote());
                dbRef.child("questions").child(questionList.get(finalPos).getParentKey()).child("answers").child(tempAns.getParentKey()).child("Time").setValue(tempAns.getTime());
                dbRef.child("questions").child(questionList.get(finalPos).getParentKey()).child("answers").child(tempAns.getParentKey()).child("Date").setValue(tempAns.getDate());
            }
            return null;
        }
    }

    @OnClick(R.id.like_btn_iv)
    public void onLikeBtnClicked()
    {
        if(questionDetails.getIsUpvoted()=="true")
        {
            likeBtnIV.setImageDrawable(getResources().getDrawable(R.drawable.like_hollow_icon));
            questionDetails.setIsUpvoted("false");
            list.get(finalPos).setIsUpvoted("false");
            int l=Integer.parseInt(questionDetails.getLikes());
            list.get(finalPos).setLikes(String.valueOf(l-1));
            questionDetails.setLikes(String.valueOf(l-1));
            numberOfLikesTV.setText(questionDetails.getLikes());
        }
        else
        {
            likeBtnIV.setImageDrawable(getResources().getDrawable(R.drawable.like_solid_icon));
            questionDetails.setIsUpvoted("true");
            list.get(finalPos).setIsUpvoted("true");
            int l=Integer.parseInt(questionDetails.getLikes());
            list.get(finalPos).setLikes(String.valueOf(l+1));
            questionDetails.setLikes(String.valueOf(l+1));
            numberOfLikesTV.setText(questionDetails.getLikes());
        }
        questionList = list;
        MyPreferenceManager.setQuestionList(AnswerActivity.this,list);
        Toast.makeText(AnswerActivity.this, list.get(finalPos).getParentKey(), Toast.LENGTH_SHORT).show();
        dbQRef.child("Likes").setValue(questionDetails.getLikes());
    }

    @OnClick(R.id.back_btn_iv)
    public void onBackBtnClicked()
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
