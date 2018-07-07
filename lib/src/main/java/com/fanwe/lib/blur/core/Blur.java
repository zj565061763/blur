package com.fanwe.lib.blur.core;

import android.graphics.Bitmap;
import android.view.View;

public interface Blur
{
    /**
     * 设置模糊半径，默认10
     *
     * @param radius
     * @return
     */
    Blur setRadius(int radius);

    /**
     * 设置压缩倍数，默认8
     *
     * @param downSampling
     * @return
     */
    Blur setDownSampling(int downSampling);

    /**
     * 设置覆盖层颜色，默认透明
     *
     * @param color
     * @return
     */
    Blur setColor(int color);

    /**
     * 设置返回的模糊Bitmap是否要保持压缩后的宽和高，默认false-保持原来的宽和高
     *
     * @param keepDownSamplingSize true-保持压缩后的宽和高，false-保持原来的宽和高
     * @return
     */
    Blur setKeepDownSamplingSize(boolean keepDownSamplingSize);

    /**
     * 设置调用模糊方法{@link #blur(View)}或者{@link #blur(Bitmap)}后是否自动调用{@link #destroy()}，默认true自动释放
     *
     * @param destroyAfterBlur
     * @return
     */
    Blur setDestroyAfterBlur(boolean destroyAfterBlur);

    /**
     * 返回压缩倍数
     *
     * @return
     */
    int getDownSampling();

    /**
     * 得到View的模糊Bitmap
     *
     * @param view
     * @return
     */
    Bitmap blur(View view);

    /**
     * 模糊Bitmap，传入的对象不会被回收
     *
     * @param bitmap
     * @return
     */
    Bitmap blur(Bitmap bitmap);

    /**
     * 释放资源，调用此方法后依旧可以使用此对象
     */
    void destroy();
}
