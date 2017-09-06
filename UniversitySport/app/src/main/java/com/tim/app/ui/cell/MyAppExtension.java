package com.tim.app.ui.cell;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;

import static com.bumptech.glide.request.RequestOptions.decodeTypeOf;

/**
 * @创建者 倪军
 * @创建时间 2017/8/28
 * @描述
 */
@GlideExtension
public class MyAppExtension {

    // Size of mini thumb in pixels.
    private static final int MINI_THUMB_SIZE = 100;

    private MyAppExtension() {
    } // utility class

    @GlideOption
    public static void miniThumb(RequestOptions options, int size) {
        options
                .fitCenter()
                .override(size);
    }

    private static final RequestOptions DECODE_TYPE_GIF = decodeTypeOf(GifDrawable.class).lock();

    @GlideType(GifDrawable.class)
    public static void asGiff(RequestBuilder<GifDrawable> requestBuilder) {
        requestBuilder
                .transition(new DrawableTransitionOptions())
                .apply(DECODE_TYPE_GIF);
    }
}
