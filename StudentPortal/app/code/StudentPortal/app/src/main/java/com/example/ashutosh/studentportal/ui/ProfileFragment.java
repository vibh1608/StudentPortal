package com.example.ashutosh.studentportal.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashutosh.studentportal.BaseApplication;
import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.interfaces.OnBackPressedListener;
import com.example.ashutosh.studentportal.modal.UserDetails;
import com.example.ashutosh.studentportal.preference.MyPreferenceManager;
import com.example.ashutosh.studentportal.util.Permissions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ashutosh.studentportal.ui.BaseDashboardActivity.userDetails;

/**
 * Created by Ashutosh on 05-03-2018.
 */

public class ProfileFragment extends Fragment implements OnBackPressedListener {

    public static String TAG = "profileFragment";
    RelativeLayout changer;
    View view;
    //ProfileFragment profileFragment;

    @BindView(R.id.back_main_btn)
    ImageView backBtnIV;
    @BindView(R.id.tvName)
    TextView nameTV;
    @BindView(R.id.tvUsername)
    TextView userNameTV;
    @BindView(R.id.account_card)
    CardView accountCard;
    public static CircleImageView profileImageCIV;
    @BindView(R.id.imgAddBtn)
    ImageView addProfileImageBtnIV;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    ImageView expandedImageView=null;

    boolean result,result2;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    private Animator mCurrentAnimator;
    public static Uri profileImageUri;
    public static String parentID;
    public UserDetails details;

    public static ImageLoader imageLoader;

    private int mShortAnimationDuration;

    public static StorageReference storageReference;
    public static DatabaseReference databaseReference;

    public static Drawable profileDrawable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        result= Permissions.checkPermission(getActivity());
        result2 = Permissions.checkCameraPermission(getActivity());
        mShortAnimationDuration = getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime);
        ((ProfileActivity)getActivity()).setOnBackPressedListener(this);
        details = userDetails;
        if(details == null)
        {
            details = MyPreferenceManager.getUserDetail(getActivity());
        }
        parentID = details.getParentID();
        BaseApplication.initImageLoader(getActivity());
        imageLoader = ImageLoader.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("profile/"+details.getParentID()+".jpg");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(details.getParentID());
        if(details.getProfileURL().equals("null"))
        {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("ProfileURL").exists())
                    {
                        details.setProfileURL(dataSnapshot.child("ProfileURL").getValue().toString());
                        MyPreferenceManager.setUserDetail(getActivity(),details);
                        details = MyPreferenceManager.getUserDetail(getActivity());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("ProfileURL").exists())
                    {
                        details.setProfileURL(dataSnapshot.child("ProfileURL").getValue().toString());
                        MyPreferenceManager.setUserDetail(getActivity(),details);
                        details = MyPreferenceManager.getUserDetail(getActivity());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        profileDrawable = new BitmapDrawable(getResources(),imageLoader.loadImageSync(details.getProfileURL()));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);


        profileImageCIV = (CircleImageView)view.findViewById(R.id.civProfilePic);
        changer = (RelativeLayout)view.findViewById(R.id.changer_rl);
        Drawable coverPhoto = ResourcesCompat.getDrawable(getResources(), R.drawable.background_image, null);
        LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
        layerDrawable.setDrawableByLayerId(R.id.image_item, coverPhoto);
        changer.setBackground(layerDrawable);

        View photoHeader = view.findViewById(R.id.photoHeader);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /* For devices equal or higher than lollipop set the translation above everything else */
            photoHeader.setTranslationZ(6);
            /* Redraw the view to show the translation */
            photoHeader.invalidate();
        }

        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTV.setText(userDetails.getFirstName()+" "+userDetails.getLastName());
        userNameTV.setText(userDetails.getEmailID());

        expandedImageView = (ImageView) view.findViewById(R.id.expanded_image);
        Toast.makeText(getActivity(), details.getProfileURL(), Toast.LENGTH_SHORT).show();

        if(!details.getProfileURL().equals("null"))
        {
            try {
                URL url = new URL(details.getProfileURL());
                Uri uri = Uri.parse(url.toURI().toString());
                imageLoader.loadImage(String.valueOf(uri), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        profileImageCIV.setImageBitmap(loadedImage);
                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
            }

        }
        else
            profileImageCIV.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.default_profile_image));

    }

    @OnClick(R.id.account_card)
    public void onAccountCardClicked()
    {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AccountFragment())
                .setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right,0,0)
                .addToBackStack("accountFragment")
                .commit();
    }

    @OnClick(R.id.civProfilePic)
    public void onProfileImageClicked()
    {
        scrollView.setVisibility(View.GONE);
        changer.setBackgroundColor(Color.parseColor("#000000"));
        if(profileDrawable!=null)
        zoomImageFromThumb(profileImageCIV,profileDrawable);
        else
            zoomImageFromThumb(profileImageCIV,getActivity().getResources().getDrawable(R.drawable.default_profile_image));
    }

    @OnClick(R.id.scroll_view)
    public void onScrollViewClicked()
    {
        Drawable coverPhoto = ResourcesCompat.getDrawable(getResources(), R.drawable.background_image, null);
        LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
        layerDrawable.setDrawableByLayerId(R.id.image_item, coverPhoto);
        changer.setBackground(layerDrawable);
        scrollView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.back_main_btn)
    public void onBackPressed()
    {
        getActivity().finish();
    }

    @OnClick(R.id.imgAddBtn)
    public void onAddProfileBtnClicked()
    {
        selectImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Permissions.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    else if(userChoosenTask.equals("Choose from Library"))
                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    //code for deny
                }
                break;
            case Permissions.MY_PERMISSIONS_REQUEST_CAMERA:
                if(grantResults.length >0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    cameraIntent();
                }
                else
                {
                    Toast.makeText(getActivity(), "Not Granted", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result) {
                        //result2 = Permissions.checkCameraPermission(getActivity());
                        if (result2)
                            cameraIntent();
                    }
                    else
                        Toast.makeText(getActivity(), "Permissions are not given!", Toast.LENGTH_SHORT).show();

                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask ="Choose from Gallery";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data,SELECT_FILE);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        profileImageUri = getImageUri(getActivity(),thumbnail);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        Log.d("File Path",destination.getAbsolutePath());
        Toast.makeText(getActivity(), destination.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        //profileImageUri = Uri.fromFile(destination);
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profileImageCIV.setImageBitmap(thumbnail);
        //profileBitmap=thumbnail;
        //new ProfileImageUploaderTask().execute();
        uploadFile();

        /*ByteArrayOutputStream blob = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100 , blob);
        byte[] bitmapdata = blob.toByteArray();
        MyPreferenceManager.setProfileImageBytes(getActivity(),bitmapdata);*/
        //MyPreferenceManager.setUserDetail(getActivity(),userDetails);


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data,int type) {

        profileImageUri=data.getData();

        //Log.d("File Path",profileImageUri.getPath());
        //Toast.makeText(getActivity(), profileImageUri.getPath(), Toast.LENGTH_SHORT).show();

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uploadFile();
        //if(type==SELECT_FILE)
        profileImageCIV.setImageBitmap(bm);
        //new ProfileImageUploaderTask().execute();
        //profileBitmap=bm;
        /*ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100 , blob);
        byte[] bitmapdata = blob.toByteArray();
        MyPreferenceManager.setProfileImageBytes(getActivity(),bitmapdata);*/

    }

    public void uploadFile()
    {
        //UserDetails userDetails;
        if(profileImageUri!=null)
        {
            storageReference = FirebaseStorage.getInstance().getReference().child("profile/"+userDetails.getParentID()+".jpg");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userDetails.getParentID());

            storageReference.putFile(profileImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            databaseReference.child("ProfileURL").setValue(downloadUrl.toString());
                            details.setProfileURL(downloadUrl.toString());
                            userDetails.setProfileURL(downloadUrl.toString());
                            MyPreferenceManager.setUserDetail(BaseApplication.getDefaultContext(),userDetails);

                            /*try {
                                URL url = new URL(userDetails.getProfileURL());
                                Uri uri = Uri.parse(url.toURI().toString());
                                imageLoader.loadImage(String.valueOf(uri), new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        profileImageCIV.setImageBitmap(loadedImage);
                                    }
                                });

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                //Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                                //Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
                            }*/

                            //MyPreferenceManager.setUserDetail(getActivity(),userDetails);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });

        }
    }

    public class ProfileImageUploaderTask extends AsyncTask<Void,Void,Void>
    {
        StorageReference storageReference;
        DatabaseReference databaseReference;
        UserDetails userDetails;
        @Override
        protected Void doInBackground(Void... params) {

            if(profileImageUri!=null)
            {
                userDetails = MyPreferenceManager.getUserDetail(BaseApplication.getDefaultContext());
                storageReference = FirebaseStorage.getInstance().getReference().child("profile/"+userDetails.getParentID()+".jpg");
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userDetails.getParentID());

                storageReference.putFile(profileImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                databaseReference.child("ProfileURL").setValue(downloadUrl.toString());
                                userDetails.setProfileURL(downloadUrl.toString());
                                MyPreferenceManager.setUserDetail(BaseApplication.getDefaultContext(),userDetails);

                                try {
                                    URL url = new URL(userDetails.getProfileURL());
                                    Uri uri = Uri.parse(url.toURI().toString());
                                    imageLoader.loadImage(String.valueOf(uri), new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            profileImageCIV.setImageBitmap(loadedImage);
                                        }
                                    });

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                    //Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                    //Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
                                }

                                //MyPreferenceManager.setUserDetail(getActivity(),userDetails);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });

            }

            return null;
        }
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
        view.findViewById(R.id.changer_rl)
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


    @Override
    public void doBack() {
        if(expandedImageView.getVisibility()==View.VISIBLE)
        {
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
            getActivity().finish();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
