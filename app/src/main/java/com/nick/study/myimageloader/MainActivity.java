package com.nick.study.myimageloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.nick.study.imageloader.ImageCache.DoubleCache;
import com.nick.study.imageloader.ImageLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        ImageView imageView2 = (ImageView) findViewById(R.id.image2);

        ImageLoader.getInstance().setImageCache(new DoubleCache(this));
        ImageLoader.getInstance().displayImage("http://img14.360buyimg.com/da/jfs/t2776/61/2855395343/27826/15e3a27e/57762f73n72e59b5f.jpg",imageView);
        ImageLoader.getInstance().displayImage("http://img14.360buyimg.com/da/jfs/t2776/61/2855395343/27826/15e3a27e/57762f73n72e59b5f.jpg",imageView2);

    }
}
