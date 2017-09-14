package com.stephen.curry.memesou;

import android.app.Application;

import com.stephen.curry.memesou.db.DBManager;

public class MyApplication extends Application {
    private DBManager dbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        //导入数据库
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
    }
}
