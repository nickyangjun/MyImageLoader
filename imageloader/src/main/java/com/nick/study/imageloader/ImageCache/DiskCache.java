package com.nick.study.imageloader.ImageCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yangjun1 on 2016/7/26.
 */
public class DiskCache implements ImageCache {
    private String mCacheDirName = "imageCache";
    private String mCacheDirPath;
    private File mCachDir = null;

    public DiskCache(Context context){
        initDiskCacheDir(context);
    }

    //初始化SD卡缓存的目录位置
    public void initDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //获取外部存储卡的位置，/sdcard/Android/data/xxxx(应用包名)/cache
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //如果无法获取外部存储卡的位置，则使用应用自己的私用空间，/data/data/xxxx(应用包名)/cache
            cachePath = context.getCacheDir().getPath();
        }
        mCacheDirPath = cachePath+File.separator+mCacheDirName;
        mCachDir = new File(mCacheDirPath);
        if(!mCachDir.exists()){
            mCachDir.mkdirs();
        }
    }

    //将下载的图片缓存到sd卡里
    @Override
    public void put(String key, Bitmap bitmap){
        if(!mCachDir.exists()){
            return;
        }
        //对传入的key值进行MD5处理
        String savedFilePath = mCacheDirPath+File.separator+hashKeyForDisk(key);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(savedFilePath));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //根据传入的key值返回缓存的Bitmap
    @Override
    public Bitmap get(String key){
        if(!mCachDir.exists()){
            return null;
        }
        String savedFilePath = mCacheDirPath+File.separator+hashKeyForDisk(key);
        File bitmapFile = new File(savedFilePath);
        if(bitmapFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(savedFilePath);
            return bitmap;
        }
        return null;
    }

    //将Key用MD5码编码
    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
