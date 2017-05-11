package com.application.library.widget;

import android.graphics.drawable.AnimationDrawable;


public class CustomAnimationDrawable extends AnimationDrawable implements Cloneable {

    @Override
    public CustomAnimationDrawable clone() {
        CustomAnimationDrawable o = null;
        try {
            o = (CustomAnimationDrawable) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }


}
