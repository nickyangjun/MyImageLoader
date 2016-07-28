package com.nick.study.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.nick.study.imageloader.ImageCache.ImageCache;
import com.nick.study.imageloader.ImageCache.MemCache;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/7/24.
 */
public class ImageLoader {
    private static ImageLoader Instance = null;
    // 图片缓存
    ImageCache mImageCache = null;
    //请求处理者
    RequestHandleThread mRequestHandleThread;

    public void setImageCache(ImageCache cache){
        mImageCache = cache;
        mRequestHandleThread.setImageCache(cache);
    }
    private ImageLoader() {
        mImageCache = new MemCache();
        mRequestHandleThread = new RequestHandleThread(mImageCache,mHandler);
    }
    public static ImageLoader getInstance(){
        if(Instance == null){
            synchronized (ImageLoader.class) {
                if(Instance == null) {
                    Instance = new ImageLoader();
                }
            }
        }
        return Instance;
    }

    //在主线程中显示图片
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageView imageView = (ImageView) msg.obj;
            imageView.setImageBitmap((Bitmap) imageView.getTag());
        }
    };

    public  void displayImage(final String url, final ImageView imageView) {
        imageView.setTag(url);
        if(mImageCache != null){
            Bitmap bitmap = mImageCache.get(url);
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
                return;
            }
        }
        ViewRequest request = new ViewRequest(url,imageView);
        mRequestHandleThread.AddRequest(request);
    }
}
