package com.example.ashutosh.studentportal.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashutosh.studentportal.BaseApplication;
import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.adapter.QuestionAdapter;
import com.example.ashutosh.studentportal.interfaces.OnQuestionItemClickListener;
import com.example.ashutosh.studentportal.modal.AnswerDetails;
import com.example.ashutosh.studentportal.modal.QuestionDetails;
import com.example.ashutosh.studentportal.modal.UserDetails;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import java.util.*;

/**
 * Created by Ashutosh on 05-03-2018.
 */

public class BaseDashboardActivity extends AppCompatActivity {

    //OnBackPressedListener onBackPressedListener;
    boolean isBackPressed = false;
    @BindView(R.id.img_btn_profile)
    CircleImageView profileBtnCIV;
    @BindView(R.id.custom_toolbar_dashboard)
    Toolbar toolbar;
    @BindView(R.id.question_et)
    EditText questionET;
    @BindView(R.id.add_iv)
    ImageView askQuestionIV;
    @BindView(R.id.tv1)
    TextView askQuestionTV;
    @BindView(R.id.question_rv)
    RecyclerView questionRV;


    public static List<QuestionDetails> questionList;
    public static DatabaseReference dbRef;
    public static UserDetails userDetails;
    QuestionDetails questionDetails;
    QuestionAdapter adapter;
    public static ImageLoader imageLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setSupportActionBar(toolbar);

        BaseApplication.initImageLoader(this);
        imageLoader = ImageLoader.getInstance();

        setTextAddListener();

        questionList = MyPreferenceManager.getQuestionList(this);
        userDetails = MyPreferenceManager.getUserDetail(this);
        if(questionList==null)
        {
            questionList = new ArrayList<QuestionDetails>();
            new QuestionFetcherTask().execute();
            Toast.makeText(this, "No questions!!", Toast.LENGTH_SHORT).show();
        }

        dbRef = FirebaseDatabase.getInstance().getReference();

        if(isConnectedToInternet())
        {
            dbRef.child("users").child(userDetails.getParentID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("ProfileURL").exists() && !dataSnapshot.child("ProfileURL").getValue().equals("null"))
                    {
                        String profileUrl = dataSnapshot.child("ProfileURL").getValue().toString();
                        userDetails.setProfileURL(profileUrl);
                        MyPreferenceManager.setUserDetail(BaseDashboardActivity.this,userDetails);

                        try {
                            URL url = new URL(userDetails.getProfileURL());
                            Uri uri = Uri.parse(url.toURI().toString());
                            imageLoader.loadImage(String.valueOf(uri), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    profileBtnCIV.setImageBitmap(loadedImage);
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
                    else if(dataSnapshot.child("ProfileURL").exists() && dataSnapshot.child("ProfileURL").getValue().equals("null"))
                    {
                        profileBtnCIV.setImageDrawable(getResources().getDrawable(R.drawable.default_profile_image));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        adapter = new QuestionAdapter(this, questionList, new OnQuestionItemClickListener() {
            @Override
            public void onCardClick(View view, int position) {
                Intent intent = new Intent(BaseDashboardActivity.this,AnswerActivity.class);
                intent.putExtra("questionPosition",position);
                startActivity(intent);
            }
        });
        questionRV.setAdapter(adapter);
        questionRV.setLayoutManager(new LinearLayoutManager(this));

    }

    public void setTextAddListener()
    {
        questionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final List<QuestionDetails> filteredList = new ArrayList<>();

                for(int i=0;i<questionList.size();i++)
                {
                    String temp = questionList.get(i).getQuestion();
                    if(temp.contains(query))
                        filteredList.add(questionList.get(i));
                }

                questionRV.setLayoutManager(new LinearLayoutManager(BaseDashboardActivity.this));
                adapter = new QuestionAdapter(BaseDashboardActivity.this, filteredList, new OnQuestionItemClickListener() {
                    @Override
                    public void onCardClick(View view, int position) {

                        int pos2 = 0;
                        for(int i=0;i<questionList.size();i++)
                            if(filteredList.get(position).getQuestion().equals(questionList.get(i).getQuestion()))
                                pos2=i;

                        Intent intent = new Intent(BaseDashboardActivity.this,AnswerActivity.class);
                        intent.putExtra("questionPosition",pos2);
                        startActivity(intent);
                    }
                });
                questionRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.add_iv)
    public void onAddIconClicked()
    {
        takeQuestion();
    }

    @OnClick(R.id.tv1)
    public void onAskTextClicked()
    {
        takeQuestion();
    }

    public void takeQuestion()
    {
        final Dialog dialog = new Dialog(BaseDashboardActivity.this);
        dialog.setContentView(R.layout.ask_question_dialog);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        final EditText takeQueET = (EditText)dialog.findViewById(R.id.take_que_et);
        TextView postTV = (TextView) dialog.findViewById(R.id.post_tv);

        takeQueET.setText(questionET.getText().toString().trim());

        postTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!takeQueET.getText().toString().trim().isEmpty())
                {
                    dialog.dismiss();
                    String key = dbRef.child("questions").push().getKey();
                    questionDetails = new QuestionDetails();
                    if(userDetails == null)
                        userDetails = MyPreferenceManager.getUserDetail(BaseApplication.getDefaultContext());
                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String date = df.format(c.getTime());
                    SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
                    String time = dt.format(c.getTime());

                    questionDetails.setParentKey(key);
                    questionDetails.setQuetionerName(userDetails.getFirstName()+" "+userDetails.getLastName());
                    questionDetails.setQuetionerEmail(userDetails.getEmailID());
                    questionDetails.setQuetionerProfileURL(userDetails.getProfileURL());
                    questionDetails.setQuestion(takeQueET.getText().toString().trim());
                    questionDetails.setLikes("0");
                    questionDetails.setDate(date);
                    questionDetails.setTime(time);

                    questionList.add(0,questionDetails);
                    adapter.notifyData(questionList);
                    MyPreferenceManager.setQuestionList(BaseDashboardActivity.this,questionList);
                    new UploaderTask().execute();

                }
                else
                {
                    takeQueET.setError("First fill the question!");
                }
            }
        });

    }

    public class UploaderTask extends AsyncTask<Void,Void,Void>
    {
        QuestionDetails tempDetails = questionDetails;
        @Override
        protected Void doInBackground(Void... params) {
            if(tempDetails!=null)
            {
                dbRef.child("questions").child(tempDetails.getParentKey()).child("QuetionerName").setValue(tempDetails.getQuetionerName());
                dbRef.child("questions").child(tempDetails.getParentKey()).child("QuetionerEmail").setValue(tempDetails.getQuetionerEmail());
                dbRef.child("questions").child(tempDetails.getParentKey()).child("QuetionerProfileURL").setValue(tempDetails.getQuetionerProfileURL());
                dbRef.child("questions").child(tempDetails.getParentKey()).child("Question").setValue(tempDetails.getQuestion());
                dbRef.child("questions").child(tempDetails.getParentKey()).child("Likes").setValue(tempDetails.getLikes());
                dbRef.child("questions").child(tempDetails.getParentKey()).child("Time").setValue(tempDetails.getTime());
                dbRef.child("questions").child(tempDetails.getParentKey()).child("Date").setValue(tempDetails.getDate());
            }

            return null;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        FragmentManager fManager = getSupportFragmentManager();
        int count = fManager.getBackStackEntryCount();

        /*if(onBackPressedListener!=null)
        {
            //super.onBackPressed();
            onBackPressedListener.doBack();
            onBackPressedListener = null;
        }
        else*/ if (count>1) {
            fManager.popBackStack();
            /*fragChat.setVisibility(View.VISIBLE);
            FragmentManager.BackStackEntry bs1 = fManager.getBackStackEntryAt(count - 1);
            bs1.getName();
            Log.e("Log", "Fragment Name : " + bs1.getName());

            FragmentManager.BackStackEntry bs = fManager.getBackStackEntryAt(count - 1);
            fManager.popBackStack(bs.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);*/
        }
        else if(count==1)
        {
            fManager.popBackStack();
            //toolbar.setVisibility(View.VISIBLE);
            //slideToolbarDown();
        }
        else {
            if (!isBackPressed) {
                isBackPressed = true;
                Toast.makeText(this, "Press again to exit the app.", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isBackPressed = false;
                }
            }, 2000);
        }
    }

    @OnClick(R.id.img_btn_profile)
    public void onProfileBtnClicked()
    {
        //slideToolbarUp();
        //toolbar.setVisibility(View.GONE);
        Intent intent = new Intent(BaseDashboardActivity.this,ProfileActivity.class);
        startActivity(intent);

    }

    public void slideToolbarDown(){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                toolbar.getHeight());                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        toolbar.startAnimation(animate);
    }

    public void slideToolbarUp(){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                toolbar.getHeight(),                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        toolbar.startAnimation(animate);
    }

    /*public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }*/

    public class QuestionFetcherTask extends AsyncTask<Void,Void,Void>
    {
        DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference();
        List<QuestionDetails> qlist;
        @Override
        protected Void doInBackground(Void... params) {

            databaseRef.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    qlist = new ArrayList<QuestionDetails>();
                    for(DataSnapshot snapShot:dataSnapshot.getChildren())
                    {
                        QuestionDetails q= new QuestionDetails();
                        q.setParentKey(snapShot.getKey());
                        q.setQuetionerName(snapShot.child("QuetionerName").getValue().toString());
                        q.setQuetionerEmail(snapShot.child("QuetionerEmail").getValue().toString());
                        q.setQuetionerProfileURL(snapShot.child("QuetionerProfileURL").toString());
                        q.setQuestion(snapShot.child("Question").getValue().toString());
                        q.setLikes(snapShot.child("Likes").getValue().toString());
                        q.setTime(snapShot.child("Time").getValue().toString());
                        q.setDate(snapShot.child("Date").getValue().toString());

                        DataSnapshot snshot = snapShot.child("answers");
                        if(snshot.exists())
                        {
                            List<AnswerDetails> aList = new ArrayList<AnswerDetails>();
                            for(DataSnapshot s2 : snshot.getChildren())
                            {
                                AnswerDetails answerDetails = new AnswerDetails();
                                answerDetails.setParentKey(s2.getKey());
                                Log.d("answerKey",s2.getKey());
                                answerDetails.setAnswererName(s2.child("AnswererName").getValue().toString());
                                answerDetails.setAnswererEmail(s2.child("AnswererEmail").getValue().toString());
                                answerDetails.setAnswer(s2.child("Answer").getValue().toString());
                                answerDetails.setUpvote(s2.child("Upvote").getValue().toString());
                                answerDetails.setTime(s2.child("Time").getValue().toString());
                                answerDetails.setDate(s2.child("Date").getValue().toString());
                                aList.add(answerDetails);
                            }
                            if(aList.size()!=0)
                            {
                                Collections.sort(aList, new Comparator<AnswerDetails>() {
                                    @Override
                                    public int compare(AnswerDetails o1, AnswerDetails o2) {
                                        try {
                                            Date d1 = new SimpleDateFormat("dd-MMM-yyyy").parse(o1.getDate());
                                            Date d2 = new SimpleDateFormat("dd-MMM-yyyy").parse(o2.getDate());

                                            return d2.compareTo(d1);

                                        }
                                        catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        return 0;
                                    }
                                });
                            }

                            q.setAnswerDetailsList(aList);

                        }

                        qlist.add(q);
                    }

                    if(qlist.size()!=0)
                    {
                        Collections.sort(qlist, new Comparator<QuestionDetails>() {
                            @Override
                            public int compare(QuestionDetails o1, QuestionDetails o2) {
                                try {
                                    Date d1 = new SimpleDateFormat("dd-MMM-yyyy").parse(o1.getDate());
                                    Date d2 = new SimpleDateFormat("dd-MMM-yyyy").parse(o2.getDate());

                                    return d2.compareTo(d1);

                                }
                                catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        });
                    }

                    questionList.clear();
                    questionList = qlist;
                    adapter.notifyData(questionList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
    }

    public boolean isConnectedToInternet()
    {
        //initialising status of the device whether is connected to internet or not
        boolean isConnected = false;

        //creating connectivityManager object to check connection status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //getting network information
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //if connected to internet then changing its value
        if (networkInfo != null) {
            isConnected = true;
        }

        //returning connection status
        return isConnected;
    }
}
