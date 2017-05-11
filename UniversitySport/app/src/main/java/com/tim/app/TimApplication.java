package com.tim.app;


import com.application.library.base.LSApplication;

public class TimApplication extends LSApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        RT.application = this;
        RT.ins().init();
    }

}
