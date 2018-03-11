package com.example.ashutosh.studentportal;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Ashutosh on 05-03-2018.
 */

public class BaseApplication extends Application {
    private static BaseApplication defaultContext;
    public static DatabaseReference mDatabase;
    //public static FirebaseStorage mStorage;
    public static ImageLoaderConfiguration config;

    public static BaseApplication getDefaultContext() {
        return defaultContext;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //FirebaseApp.initializeApp(this /*context */, FirebaseOptions.fromResource(this) /* context */);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mStorage.getInstance();
        //if (FirebaseApp.getApps(this).isEmpty()) {
        defaultContext = this;

        initImageLoader(getApplicationContext());

    }

    public static void initImageLoader(Context context) {
        DisplayImageOptions opts = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.defaultDisplayImageOptions(opts);
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

}
