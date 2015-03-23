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
public class MainApplication extends Application implements AMapLocationListener {

	private LocationManagerProxy mLocationManagerProxy;
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
        init();
    }

    private void init() {
    	mLocationManagerProxy = LocationManagerProxy.getInstance(this);
    	 
        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法     
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 60*1000, 100, this);
 
        mLocationManagerProxy.setGpsEnable(true);
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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
            //获取位置信息
            Double geoLat = amapLocation.getLatitude();
            Double geoLng = amapLocation.getLongitude();   
            Log.d("geoLat=====>", geoLat+"");
            Log.d("getLng=====>", geoLng+"");
            // save geo
            
        }
	}
}
