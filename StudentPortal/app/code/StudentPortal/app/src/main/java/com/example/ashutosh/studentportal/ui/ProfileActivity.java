package com.example.ashutosh.studentportal.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.ashutosh.studentportal.R;
import com.example.ashutosh.studentportal.interfaces.OnBackPressedListener;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Ashutosh on 08-03-2018.
 */

public class ProfileActivity extends AppCompatActivity {

    OnBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_profile);

        if(savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right,0,0)
                    .addToBackStack("profileFragment")
                    .commit();
        }

    }

    @Override
    public  void onBackPressed() {
        //super.onBackPressed();

        FragmentManager fm=getSupportFragmentManager();
        //Fragment fragment = fm.findFragmentById(R.id.fragment_container);


        if(onBackPressedListener!=null)
        {
            onBackPressedListener.doBack();
            //onBackPressedListener = null;
        }
        else if(fm.getBackStackEntryCount()>1)
        {
            fm.popBackStack();
        }
        else
        {
            super.onBackPressed();
            super.onBackPressed();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

}
