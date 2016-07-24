package com.nick.study.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/7/24.
 */
public class ImageLoader {
    private static ImageLoader Instance = null;
    private ImageCache mImageCache;
    // 线程池,线程数量为CPU的数量
    ExecutorService mExecutorService = Executors.newFixedThreadPool (Runtime.getRuntime().availableProcessors());
    //在主线程中显示图片
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageView imageView = (ImageView) msg.obj;
            imageView.setImageBitmap((Bitmap) imageView.getTag());
        }
    };

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
    private ImageLoader() {
       mImageCache = new ImageCache();
    }

    public  void displayImage(final String url, final ImageView imageView) {
        imageView.setTag(url);
        //先从cache中取图片
        if(mImageCache.get(url)!=null){
            imageView.setImageBitmap(mImageCache.get(url));
            return;
        }
        mExecutorService.submit(new Runnable() {
            @Override
            public  void run() {
                Bitmap bitmap = downloadImage(url);
                if (bitmap == null) {
                    return;
                }
                if (imageView.getTag().equals(url)) {
                    Message msg = mHandler.obtainMessage();
                    imageView.setTag(bitmap);
                    msg.obj = imageView;
                    mHandler.sendMessage(msg);
                }
                mImageCache.put(url, bitmap);
            }
        });
    }

    public  Bitmap downloadImage(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true); //允许输入流，即允许下载
            conn.setUseCaches(false); //不使用缓冲
            conn.setRequestMethod("GET"); //使用get请求
            InputStream is = conn.getInputStream();   //获取输入流，此时才真正建立链接
            bitmap = BitmapFactory.decodeStream(is);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
