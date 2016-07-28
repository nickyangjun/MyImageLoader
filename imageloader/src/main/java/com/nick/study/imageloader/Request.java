package com.nick.study.imageloader;

/**
 * Created by yangjun1 on 2016/7/27.
 */
public class Request {
    private String imageUrl;
    private Object tag;

    public Request(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
