package com.stephen.curry.memesou.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class BitmapUtils {
    private static final String TAG="BitmapUtils";

    public Bitmap decodeSampleBitmapFromResource(Resources resources,int resId,int reqWidth,int reqHeight){
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeResource(resources,resId,options);
        options.inSampleSize=calculateInSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeResource(resources,resId,options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options,int reqW,int reqH){
        //具体实现，因为时间关系，略过
        return 100;
    }

}
