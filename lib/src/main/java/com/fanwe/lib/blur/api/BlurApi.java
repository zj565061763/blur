package com.fanwe.lib.blur.api;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.fanwe.lib.blur.api.target.BackgroundTarget;
import com.fanwe.lib.blur.api.target.BlurTarget;
import com.fanwe.lib.blur.api.target.ImageViewTarget;
import com.fanwe.lib.blur.api.target.MainThreadTargetWrapper;
import com.fanwe.lib.blur.core.Blur;

abstract class BlurApi<S, R>
{
    private final Blur mBlur;

    public BlurApi(S source, Blur blur)
    {
        if (blur == null)
            throw new NullPointerException("blur must not be null");

        mBlur = blur;
    }

    protected final Blur getBlur()
    {
        return mBlur;
    }

    /**
     * {@link Blur#setRadius(int)}
     *
     * @param radius
     * @return
     */
    public R radius(int radius)
    {
        mBlur.setRadius(radius);
        return (R) this;
    }

    /**
     * {@link Blur#setDownSampling(int)}
     *
     * @param downSampling
     * @return
     */
    public R sampling(int downSampling)
    {
        mBlur.setDownSampling(downSampling);
        return (R) this;
    }

    /**
     * {@link Blur#setColorOverlay(int)}
     *
     * @param colorOverlay
     * @return
     */
    public R color(int colorOverlay)
    {
        mBlur.setColorOverlay(colorOverlay);
        return (R) this;
    }

    /**
     * {@link Blur#setKeepDownSamplingSize(boolean)}
     *
     * @param keepDownSamplingSize
     * @return
     */
    public R keepDownSamplingSize(boolean keepDownSamplingSize)
    {
        mBlur.setKeepDownSamplingSize(keepDownSamplingSize);
        return (R) this;
    }

    /**
     * 返回模糊的Bitmap
     *
     * @return
     */
    protected abstract Bitmap blurImplemention();

    /**
     * 模糊后设置给ImageView
     *
     * @param imageView
     * @return
     */
    public R into(ImageView imageView)
    {
        if (imageView != null)
            into(new MainThreadTargetWrapper(new ImageViewTarget(imageView)));
        return (R) this;
    }

    /**
     * 模糊后设置给view的背景
     *
     * @param view
     * @return
     */
    public R intoBackground(View view)
    {
        if (view != null)
            into(new MainThreadTargetWrapper(new BackgroundTarget(view)));
        return (R) this;
    }

    /**
     * 模糊后设置给某个目标
     *
     * @param target
     * @return
     */
    public R into(BlurTarget target)
    {
        if (target != null)
            target.onBlur(blurImplemention());
        return (R) this;
    }

    /**
     * 释放资源，调用此方法后依旧可以使用此对象
     */
    public void destroy()
    {
        mBlur.destroy();
    }
}
