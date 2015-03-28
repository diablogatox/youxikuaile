package com.orfid.youxikuaile;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Administrator on 2015/2/28.
 */
public class MainApplication extends Application {

    private static MainApplication instance;
    public static MainApplication getInstance() { return instance; }
    public static DatabaseHandler db;
    public static HashMap user = new HashMap();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        initImageLoader(getApplicationContext());
    }

    

	public static HashMap getUser() {
        return user;
    }

    public static DatabaseHandler getDbHandler() { return db; }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .writeDebugLogs() // Remove for release app
//                .build();
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(context);
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

}
