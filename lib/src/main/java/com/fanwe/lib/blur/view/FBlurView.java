package com.fanwe.lib.blur.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.fanwe.lib.blur.core.Blur;
import com.fanwe.lib.blur.core.CompatBlur;

import java.lang.ref.WeakReference;

public class FBlurView extends View implements BlurView
{
    public FBlurView(Context context)
    {
        super(context);
    }

    public FBlurView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FBlurView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    private Blur mBlur;
    private WeakReference<View> mTarget;

    /**
     * 返回设置的要模糊的view
     *
     * @return
     */
    public final View getTarget()
    {
        return mTarget == null ? null : mTarget.get();
    }

    /**
     * 设置要模糊的view
     *
     * @param target
     */
    public final void setTarget(View target)
    {
        final View old = getTarget();
        if (old != target)
            mTarget = target == null ? null : new WeakReference<>(target);
    }

    private final Blur getBlur()
    {
        if (mBlur == null)
        {
            mBlur = new CompatBlur(getContext());
            mBlur.setKeepDownSamplingSize(true);
            mBlur.setDestroyAfterBlur(false);
        }
        return mBlur;
    }

    @Override
    public final void setBlurRadius(int radius)
    {
        getBlur().setRadius(radius);
    }

    @Override
    public final void setBlurDownSampling(int downSampling)
    {
        getBlur().setDownSampling(downSampling);
    }

    @Override
    public final void setBlurColor(int color)
    {
        getBlur().setColor(color);
    }

    @Override
    public void blur()
    {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        final View target = getTarget();
        if (target == null)
            return;

        final Bitmap bitmap = getBlur().blur(target);
        if (bitmap != null)
        {
            final int scale = getBlur().getDownSampling();

            canvas.save();
            canvas.translate(target.getX() - getX(), target.getY() - getY());
            canvas.scale(scale, scale);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.restore();
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (mBlur != null)
        {
            mBlur.destroy();
            mBlur = null;
        }
    }
}
