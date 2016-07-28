package com.nick.study.imageloader.ImageCache;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/7/24.
 */
public interface ImageCache {
    void put(String key, Bitmap bitmap);
    Bitmap get(String key);
}
