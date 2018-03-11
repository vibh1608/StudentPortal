package com.example.ashutosh.studentportal.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashutosh.studentportal.BaseApplication;
import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.preference.MyPreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.ashutosh.studentportal.ui.AnswerActivity.finalPos;
import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.dbRef;
import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.questionList;

/**
 * Created by Ashutosh on 11-03-2018.
 */

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.back_main_btn)
    ImageView backBtnIV;
    @BindView(R.id.tvName)
    TextView nameTV;
    @BindView(R.id.tvUsername)
    TextView userNameTV;
    public static CircleImageView profileImageCIV;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    ImageView expandedImageView=null;

    RelativeLayout changer;

    public static ImageLoader imageLoader;
    public static Drawable profileDrawable;
    public String profileUrl;

    private int mShortAnimationDuration;
    private Animator mCurrentAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.fragment_user_profile);

        expandedImageView = (ImageView)findViewById(R.id.expanded_image);
        profileImageCIV = (CircleImageView)findViewById(R.id.civProfilePic);
        changer = (RelativeLayout)findViewById(R.id.changer_rl);
        Drawable coverPhoto = ResourcesCompat.getDrawable(getResources(), R.drawable.background_image, null);
        LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
        layerDrawable.setDrawableByLayerId(R.id.image_item, coverPhoto);
        changer.setBackground(layerDrawable);

        View photoHeader = findViewById(R.id.photoHeader);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /* For devices equal or higher than lollipop set the translation above everything else */
            photoHeader.setTranslationZ(6);
            /* Redraw the view to show the translation */
            photoHeader.invalidate();
        }

        ButterKnife.bind(this);

        mShortAnimationDuration =getResources().getInteger(android.R.integer.config_shortAnimTime);

        BaseApplication.initImageLoader(this);
        imageLoader = ImageLoader.getInstance();

        String type = getIntent().getStringExtra("isFrom");
        final int posi = getIntent().getIntExtra("position",0);

        if(type.equals("question"))
        {
            nameTV.setText(questionList.get(posi).getQuetionerName());
            userNameTV.setText(questionList.get(posi).getQuetionerEmail());

            String temp = questionList.get(posi).getQuetionerEmail().substring(0,questionList.get(posi).getQuetionerEmail().indexOf('@'));
            dbRef.child("users").child(temp).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("ProfileURL").exists() && !dataSnapshot.child("ProfileURL").getValue().equals("null"))
                    {
                        profileUrl = dataSnapshot.child("ProfileURL").getValue().toString();
                        questionList.get(posi).setQuetionerProfileURL(profileUrl);
                        MyPreferenceManager.setQuestionList(UserProfileActivity.this,questionList);
                        try {
                            URL url = new URL(profileUrl);
                            Uri uri = Uri.parse(url.toURI().toString());
                            imageLoader.loadImage(String.valueOf(uri), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    profileImageCIV.setImageBitmap(loadedImage);
                                }
                            });

                            loadDrawable();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            Toast.makeText(UserProfileActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                            Toast.makeText(UserProfileActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        profileImageCIV.setImageDrawable(getResources().getDrawable(R.drawable.default_profile_image));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            profileDrawable = new BitmapDrawable(getResources(),imageLoader.loadImageSync(questionList.get(posi).getQuetionerProfileURL()));

        }
        else if(type.equals("answer"))
        {
            nameTV.setText(questionList.get(finalPos).getAnswerDetailsList().get(posi).getAnswererName());
            userNameTV.setText(questionList.get(finalPos).getAnswerDetailsList().get(posi).getAnswererEmail());
            String email = questionList.get(finalPos).getAnswerDetailsList().get(posi).getAnswererEmail();
            String temp = email.substring(0,email.indexOf('@'));

            dbRef.child("users").child(temp).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("ProfileURL").exists() && !dataSnapshot.child("ProfileURL").getValue().equals("null"))
                    {
                        profileUrl = dataSnapshot.child("ProfileURL").getValue().toString();
                        questionList.get(posi).setQuetionerProfileURL(profileUrl);
                        MyPreferenceManager.setQuestionList(UserProfileActivity.this,questionList);
                        try {
                            URL url = new URL(profileUrl);
                            Uri uri = Uri.parse(url.toURI().toString());
                            imageLoader.loadImage(String.valueOf(uri), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    profileImageCIV.setImageBitmap(loadedImage);
                                }
                            });

                            loadDrawable();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            Toast.makeText(UserProfileActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                            Toast.makeText(UserProfileActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        profileImageCIV.setImageDrawable(getResources().getDrawable(R.drawable.default_profile_image));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @OnClick(R.id.civProfilePic)
    public void onProfileClicked()
    {
        scrollView.setVisibility(View.GONE);
        changer.setBackgroundColor(Color.parseColor("#000000"));
        if(profileDrawable!=null)
            zoomImageFromThumb(profileImageCIV,profileDrawable);
        else
        {
            zoomImageFromThumb(profileImageCIV,getResources().getDrawable(R.drawable.default_profile_image));
            Toast.makeText(this, "Profile default", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadDrawable()
    {
        profileDrawable = new BitmapDrawable(getResources(),imageLoader.loadImageSync(profileUrl));
    }

    @OnClick(R.id.back_main_btn)
    public void onBackBtnClicked()
    {
            /*if(expandedImageView.getVisibility()==View.VISIBLE)
            {
                Drawable coverPhoto = ResourcesCompat.getDrawable(getResources(), R.drawable.background_image, null);
                LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
                layerDrawable.setDrawableByLayerId(R.id.image_item, coverPhoto);
                changer.setBackground(layerDrawable);
                scrollView.setVisibility(View.VISIBLE);
                expandedImageView.setVisibility(View.GONE);
            }
            else*/
            this.finish();
    }

    @Override
    public void onBackPressed() {
        if(expandedImageView.getVisibility()==View.VISIBLE)
        {
            /*Drawable coverPhoto = ResourcesCompat.getDrawable(getResources(), R.drawable.background_image, null);
            LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
            layerDrawable.setDrawableByLayerId(R.id.image_item, coverPhoto);
            changer.setBackground(layerDrawable);
            scrollView.setVisibility(View.VISIBLE);
            expandedImageView.setVisibility(View.GONE);*/

            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            //profileImageCIV.getGlobalVisibleRect(startBounds);
            //findViewById(R.id.changer_rl)
            //        .getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            float startScale;
            if ((float) finalBounds.width() / finalBounds.height()
                    > (float) startBounds.width() / startBounds.height()) {
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }

                final float startScaleFinal = startScale;

                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        profileImageCIV.setAlpha(1f);
                        Drawable coverPhoto = ResourcesCompat.getDrawable(getResources(), R.drawable.background_image, null);
                        LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
                        layerDrawable.setDrawableByLayerId(R.id.image_item, coverPhoto);
                        changer.setBackground(layerDrawable);
                        scrollView.setVisibility(View.VISIBLE);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        profileImageCIV.setAlpha(1f);
                        Drawable coverPhoto = ResourcesCompat.getDrawable(getResources(), R.drawable.background_image, null);
                        LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
                        layerDrawable.setDrawableByLayerId(R.id.image_item, coverPhoto);
                        changer.setBackground(layerDrawable);
                        scrollView.setVisibility(View.VISIBLE);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;

        }
        else
            this.finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void zoomImageFromThumb(final CircleImageView thumbView, Drawable image) {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        expandedImageView.setImageDrawable(image);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.changer_rl)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);


        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        Drawable coverPhoto = ResourcesCompat.getDrawable(getResources(), R.drawable.background_image, null);
                        LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
                        layerDrawable.setDrawableByLayerId(R.id.image_item, coverPhoto);
                        changer.setBackground(layerDrawable);
                        scrollView.setVisibility(View.VISIBLE);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        Drawable coverPhoto = ResourcesCompat.getDrawable(getResources(), R.drawable.background_image, null);
                        LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
                        layerDrawable.setDrawableByLayerId(R.id.image_item, coverPhoto);
                        changer.setBackground(layerDrawable);
                        scrollView.setVisibility(View.VISIBLE);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

}
