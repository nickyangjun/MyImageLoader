package com.nick.study.imageloader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Administrator on 2016/7/24.
 */
public class MemCache implements ImageCache {
    // 图片缓存
    LruCache<String, Bitmap> mImageCache;
    public MemCache(){
        initImageCache();
    }
    private void initImageCache() {
        // 计算可使用的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 取四分之一的可用内存作为缓存
        final int cacheSize = maxMemory / 4;
        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //Bitmap的每一行所占用的空间数乘以Bitmap的行数
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    @Override
    public void put(String key,Bitmap value){
        mImageCache.put(key,value);
    }

    @Override
    public Bitmap get(String key){
       return mImageCache.get(key);
    }
}
