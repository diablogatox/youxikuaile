package com.orfid.youxikuaile;

import android.app.Application;

import java.util.HashMap;

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
    }

    public static HashMap getUser() {
        return user;
    }

    public static DatabaseHandler getDbHandler() { return db; }
}
