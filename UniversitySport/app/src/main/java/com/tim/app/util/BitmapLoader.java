package com.tim.app.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.tim.app.server.net.ServerAddressManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

/**
 * Created by guodong on 2016/7/7 14:55.
 */
public class BitmapLoader {

    private static volatile BitmapLoader ins = null;

    private BitmapLoader() {

    }

    public static BitmapLoader ins() {
        BitmapLoader mins = ins;
        if (mins == null) {
            synchronized (BitmapLoader.class) {
                mins = ins;
                if (mins == null) {
                    mins = new BitmapLoader();
                    ins = mins;
                }
            }
        }
        return mins;
    }

    public void loadImage(String url, int placeholder, ImageView imageView) {
        loadImage(url, placeholder, imageView, null);
    }

    public void loadImage(String url, int placeholder, ImageView imageView, ImageLoadingListener listener) {
        url = getRealUrl(url);
        DisplayImageOptions options;
        if (placeholder > 0) {
            options = new DisplayImageOptions.Builder().showImageOnLoading(placeholder)
                    .showImageOnFail(placeholder).showImageForEmptyUri(placeholder).cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading(false)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .displayer(new FadeInBitmapDisplayer(100))
                    .build();
        } else {
            options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(false).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .displayer(new FadeInBitmapDisplayer(100))
                    .build();
        }
        ImageLoader.getInstance().displayImage(url, imageView, options, listener);
    }

    public void loadLocalImage(String path, int placeholder, final ImageView imageView, int width, int height) {
        String url = Uri.decode(Uri.fromFile(new File(path)).toString());
        DisplayImageOptions options;
        if (placeholder > 0) {
            options = new DisplayImageOptions.Builder().showImageOnLoading(placeholder)
                    .showImageOnFail(placeholder).showImageForEmptyUri(placeholder).cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading(false)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .displayer(new SimpleBitmapDisplayer())
                    .build();
        } else {
            options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(false).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .displayer(new SimpleBitmapDisplayer())
                    .build();
        }
        ImageLoader.getInstance().displayImage(url, new ImageViewAware(imageView, false), options, new ImageSize(width, height), null, null);
    }

    public void loadQiniuImage(String url, int placeholder, ImageView imageView) {
        url = getRealUrl(url);
        DisplayImageOptions options;
        if (placeholder > 0) {
            options = new DisplayImageOptions.Builder().showImageOnLoading(placeholder)
                    .showImageOnFail(placeholder).showImageForEmptyUri(placeholder).cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading(false)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .displayer(new FadeInBitmapDisplayer(100))
                    .build();
        } else {
            options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(false).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .displayer(new FadeInBitmapDisplayer(100))
                    .build();
        }
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    public void loadQiniuImageForCircle(String url, Drawable placeholder, ImageView imageView, ImageLoadingListener listener) {
        url = getRealUrl(url);
        DisplayImageOptions options;
        if (null != placeholder) {
            options = new DisplayImageOptions.Builder().showImageOnLoading(placeholder)
                    .showImageOnFail(placeholder).showImageForEmptyUri(placeholder).cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading(false)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .displayer(new FadeInBitmapDisplayer(100))
                    .build();
        } else {
            options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(false).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .displayer(new FadeInBitmapDisplayer(100))
                    .build();
        }
        ImageLoader.getInstance().displayImage(url, imageView, options, listener);
    }

    public void loadBitmap(String url, int placeholder, SimpleImageLoadingListener listener) {
        url = getRealUrl(url);
        DisplayImageOptions options;
        if (placeholder > 0) {
            options = new DisplayImageOptions.Builder().showImageOnLoading(placeholder)
                    .showImageOnFail(placeholder).showImageForEmptyUri(placeholder).cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading(false)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
        } else {
            options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(false).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
        }
        ImageLoader.getInstance().loadImage(url, options, listener);
    }

    public String getRealUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        if (url.contains("http://")) {
            return url;
        }
        return ServerAddressManager.getImageServerDomain() + url;
    }

//    public String getQiniuUrl(String url) {
//        if (StringUtil.isEmpty(url)) {
//            return "";
//        }
//        if (url.contains("http://")) {
//            return url;
//        } else {
//            return url;
//        }
//    }
}
