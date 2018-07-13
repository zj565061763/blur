package com.fanwe.lib.blur.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.fanwe.lib.blur.api.BlurApi;
import com.fanwe.lib.blur.api.BlurApiFactory;
import com.fanwe.lib.blur.api.target.BlurTarget;

import java.lang.ref.WeakReference;

public class FBlurView extends View implements BlurView
{
    private final BlurApi mBlurApi;
    private boolean mBlurAsync;

    private WeakReference<View> mBlurSource;

    private Bitmap mBitmapBlurred;
    private boolean mIsDrawingBlur;
    private boolean mIsAttachedToWindow;

    private long mBlurTime;

    public FBlurView(Context context)
    {
        this(context, null);
    }

    public FBlurView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mBlurApi = BlurApiFactory.create(getContext());
        mBlurApi.setDestroyAfterBlur(false);
        mBlurApi.setKeepDownSamplingSize(true);

        final BlurViewAttrs viewAttrs = BlurViewAttrs.parse(context, attrs);
        setBlurRadius(viewAttrs.getBlurRadius());
        setBlurDownSampling(viewAttrs.getBlurDownSampling());
        setBlurColor(viewAttrs.getBlurColor());
        setBlurAsync(viewAttrs.isBlurAsync());
    }

    /**
     * 返回设置的要模糊的view
     *
     * @return
     */
    public final View getBlurSource()
    {
        return mBlurSource == null ? null : mBlurSource.get();
    }

    /**
     * 设置要模糊的view
     *
     * @param target
     */
    public final void setBlurSource(View target)
    {
        final View old = getBlurSource();
        if (old != target)
        {
            if (old != null)
            {
                final ViewTreeObserver observer = old.getViewTreeObserver();
                if (observer.isAlive())
                    observer.removeOnPreDrawListener(mOnPreDrawListener);
            }

            mBlurSource = target == null ? null : new WeakReference<>(target);

            if (target != null)
            {
                final ViewTreeObserver observer = target.getViewTreeObserver();
                if (observer.isAlive())
                    observer.addOnPreDrawListener(mOnPreDrawListener);

                blur();
            } else
            {
                mBlurApi.destroy();
            }
        }
    }

    private final ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener()
    {
        @Override
        public boolean onPreDraw()
        {
            blur();
            return true;
        }
    };

    @Override
    public final void setBlurRadius(int radius)
    {
        mBlurApi.setRadius(radius);
    }

    @Override
    public final void setBlurDownSampling(int downSampling)
    {
        mBlurApi.setDownSampling(downSampling);
    }

    @Override
    public final void setBlurColor(int color)
    {
        mBlurApi.setColor(color);
    }

    @Override
    public final void setBlurAsync(boolean async)
    {
        mBlurAsync = async;
    }

    @Override
    public final void blur()
    {
        if (!mIsAttachedToWindow)
            return;

        blurInternal();
    }

    private void blurInternal()
    {
        if (mIsDrawingBlur)
            return;

        final long blurTime = System.currentTimeMillis();
        mBlurApi.blur(getBlurSource()).async(mBlurAsync).into(new BlurTarget()
        {
            @Override
            public void onBlurred(Bitmap bitmap)
            {
                if (bitmap == null)
                    return;

                if (blurTime >= mBlurTime)
                {
                    mBlurTime = blurTime;
                    mBitmapBlurred = bitmap;
                    mIsDrawingBlur = true;
                    invalidate();
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        final View target = getBlurSource();
        if (target == null)
            return;

        if (mBitmapBlurred == null || mBitmapBlurred.isRecycled())
            return;

        final int scale = mBlurApi.settings().getDownSampling();

        canvas.save();
        canvas.translate(target.getX() - getX(), target.getY() - getY());
        canvas.scale(scale, scale);
        canvas.drawBitmap(mBitmapBlurred, 0, 0, null);
        canvas.restore();

        post(new Runnable()
        {
            @Override
            public void run()
            {
                mIsDrawingBlur = false;
            }
        });
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        mIsAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mIsAttachedToWindow = false;
        mBlurApi.destroy();
    }
}
