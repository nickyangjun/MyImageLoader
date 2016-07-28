package com.nick.study.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.nick.study.imageloader.ImageCache.ImageCache;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yangjun1 on 2016/7/27.
 */
public class RequestHandleThread extends HandlerThread {
    private static String TAG = RequestHandleThread.class.getSimpleName();
    private ImageCache mImageCache;
    //任务队列
    LinkedHashMap<String, LinkedList<Request>> mRequestHashMap = new LinkedHashMap<>();
    // 线程池,线程数量为CPU的数量
    ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().
            availableProcessors());

    MyHandler mHandler;
    Handler mMianHandler;

    public RequestHandleThread(ImageCache cache, Handler displayHandler) {
        super(TAG);
        mImageCache = cache;
        mMianHandler = displayHandler;
    }

    public void setImageCache(ImageCache cache){
        mImageCache = cache;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new MyHandler();
        mHandler.sendEmptyMessage(0);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Set<String> set = mRequestHashMap.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                final String k = iterator.next();
                mExecutorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = downloadImage(k);
                        if (bitmap == null) {
                            return;
                        }
                        LinkedList<Request> requestLinkedList = mRequestHashMap.get(k);
                        for (Request request : requestLinkedList) {
                            if (request instanceof ViewRequest) {
                                if (((ViewRequest) request).getView().getTag().equals(k)) {
                                    Message msg = mMianHandler.obtainMessage();
                                    ((ViewRequest) request).getView().setTag(bitmap);
                                    msg.obj = ((ViewRequest) request).getView();
                                    mMianHandler.sendMessage(msg);
                                }
                            }
                        }
                        mRequestHashMap.remove(k);
                    }
                });
            }
        }
    }

    private Bitmap downloadImage(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); //允许输入流，即允许下载
            conn.setUseCaches(false); //不使用缓冲
            conn.setRequestMethod("GET"); //使用get请求
            InputStream is = conn.getInputStream();   //获取输入流，此时才真正建立链接
            bitmap = BitmapFactory.decodeStream(is);
            if (mImageCache != null) {
                mImageCache.put(imageUrl, bitmap);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void AddRequest(Request request) {
        LinkedList<Request> requestLinkedList = mRequestHashMap.get(request.getImageUrl());
        if (requestLinkedList != null) {
            if (!requestLinkedList.contains(request)) {
                requestLinkedList.addFirst(request);
            }
        } else {
            requestLinkedList = new LinkedList<>();
            requestLinkedList.add(request);
            mRequestHashMap.put(request.getImageUrl(), requestLinkedList);
        }

        if (mHandler == null) {
            if(!this.isAlive()) {
                start();
            }
        } else {
            mHandler.sendEmptyMessage(0);
        }
    }
}
