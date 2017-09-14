package com.stephen.curry.memesou.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stephen.curry.memesou.bean.RegionInfo;

public class RegionDAO {
	
	public static List<RegionInfo> getProvencesOrCity(int type){
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		List<RegionInfo> regionInfos = new ArrayList<RegionInfo>();//String.valueOf(type)
		Cursor cursor = db.rawQuery("select * from REGIONS where type="+type,null);
		while (cursor.moveToNext()) {
			RegionInfo regionInfo = new RegionInfo();
			int _id = cursor.getInt(cursor
					.getColumnIndex("_id"));
			int parent = cursor.getInt(cursor
					.getColumnIndex("parent"));
			String name = cursor.getString(cursor
					.getColumnIndex("name"));
			int type1 = cursor.getInt(cursor
					.getColumnIndex("type"));
			regionInfo.setId(_id);
			regionInfo.setParent(parent);
			regionInfo.setName(name);
			regionInfo.setType(type1);
			regionInfos.add(regionInfo);
		}
		cursor.close();
		db.close();
		return regionInfos;
	}
	
	public static List<RegionInfo> getProvencesOrCityOnParent(int parent){
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		List<RegionInfo> regionInfos = new ArrayList<RegionInfo>();//String.valueOf(type)
		Cursor cursor = db.rawQuery("select * from REGIONS where parent="+parent,null);
		while (cursor.moveToNext()) {
			RegionInfo regionInfo = new RegionInfo();
			int _id = cursor.getInt(cursor
					.getColumnIndex("_id"));
			int parent1 = cursor.getInt(cursor
					.getColumnIndex("parent"));
			String name = cursor.getString(cursor
					.getColumnIndex("name"));
			int type = cursor.getInt(cursor
					.getColumnIndex("type"));
			regionInfo.setId(_id);
			regionInfo.setParent(parent1);
			regionInfo.setName(name);
			regionInfo.setType(type);
			regionInfos.add(regionInfo);
		}
		cursor.close();
		db.close();
		return regionInfos;
	}
}
