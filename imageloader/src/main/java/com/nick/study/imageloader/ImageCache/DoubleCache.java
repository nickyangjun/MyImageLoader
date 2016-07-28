package com.nick.study.imageloader.ImageCache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by yangjun1 on 2016/7/27.
 */
public class DoubleCache implements ImageCache {
    DiskCache mDiskCache;
    MemCache mMemCache;
    public DoubleCache(Context context){
        mDiskCache = new DiskCache(context);
        mMemCache = new MemCache();
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        mMemCache.put(key,bitmap);
        mDiskCache.put(key,bitmap);
    }

    @Override
    public Bitmap get(String key) {
        Bitmap bitmap = null;
        bitmap = mMemCache.get(key);
        if(bitmap != null) {
            return bitmap;
        }else {
            bitmap = mDiskCache.get(key);
            if(bitmap != null){
                //存入内存缓存，下次方便从内存中直接取用
                mMemCache.put(key,bitmap);
            }
            return bitmap;
        }
    }
}
