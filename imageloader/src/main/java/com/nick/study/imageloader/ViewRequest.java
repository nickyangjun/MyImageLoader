package com.nick.study.imageloader;

import android.view.View;

/**
 * Created by yangjun1 on 2016/7/27.
 */
public class ViewRequest extends Request{
    private View view;

    public ViewRequest(String url,View view){
        super(url);
        this.view = view;
    }
    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ViewRequest){
            ViewRequest m = (ViewRequest) o;
            return (m.getImageUrl().equals(this.getImageUrl()))
                    && (m.view.hashCode() == this.view.hashCode());
        }
       return false;
    }
}
