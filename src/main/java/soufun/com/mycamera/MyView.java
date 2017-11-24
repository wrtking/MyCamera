package soufun.com.mycamera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.IOException;

/**
 * 废弃类，这种继承SurfaceView实现效果实现比较复杂
 * 主要问题是surfaceCreated()方法没有执行的时候，去执行surfaceHolder.lockCanvas()，报错！
 * Created by wrt on 2017/11/14.
 */

public class MyView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Context context;
    private Bitmap bitmap;
    private Paint mPaint;
    private int bitmapWidth, bitmapHeigth;
    private int widthReduce, heightReduce;
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private Canvas canvas;
    private boolean mIsRunning;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MyView(Context context, AttributeSet attributeSet, int deflyable) {
        super(context, attributeSet, deflyable);
        init();
    }

    public void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.phone);
        bitmapWidth = bitmap.getWidth();
        bitmapHeigth = bitmap.getHeight();
        Log.i("wrt", "bitmapWidth==" + bitmapWidth + "---bitmapHeight==" + bitmapHeigth);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
//        setZOrderOnTop(true);
//        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mCamera == null) {
            try {
                mCamera = mCamera.open();
            } catch (Exception e) {
                mCamera = null;
                Log.i("wrt", "相机打开失败" + e.toString());
            }
        }
        widthReduce = getMeasuredWidth() - bitmapWidth;
        heightReduce = getMeasuredHeight() - bitmapHeigth;
        Log.i("wrt", "getMeasuredWidth==" + getMeasuredWidth() + "---getMeasuredHeight==" + getMeasuredHeight());
        Log.i("wrt", "widthReduce==" + widthReduce + "---heightReduce==" + heightReduce);
        mIsRunning = true;
        canvas = surfaceHolder.lockCanvas();
        surfaceHolder.unlockCanvasAndPost(canvas);

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Camera.Parameters parameters;
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
                //return;
            }
            parameters = mCamera.getParameters();
            mCamera.stopPreview();
        } else {
            return;
        }

        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            mCamera.setDisplayOrientation(90);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                parameters.set("orientation", "portrait");
                parameters.set("rotation", "90");
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "landscape");
                parameters.set("rotation", "0");
            }
        }
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewSize(800, 480);
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        mCamera.cancelAutoFocus();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        new Thread(this).start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
        }
        mCamera = null;
        mIsRunning = false;
    }

    @Override
    public void run() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.colorSurfaceView));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(70);
        Log.i("wrt", "mPaint==" + mPaint);


        canvas = surfaceHolder.lockCanvas();
        if(canvas!=null){
            //绘制上面的条形
            canvas.drawRect(0, 0, getMeasuredWidth(), heightReduce / 2, mPaint);
            //绘制下面的条形
            canvas.drawRect(0, getMeasuredHeight() - heightReduce / 2, getMeasuredWidth(), getMeasuredHeight(), mPaint);
            //绘制左边的条形
            canvas.drawRect(0, heightReduce / 2, widthReduce / 2, getMeasuredHeight() - heightReduce / 2, mPaint);
            //绘制右边的条形
            canvas.drawRect(getMeasuredWidth() - widthReduce / 2, heightReduce / 2, getMeasuredWidth(), getMeasuredHeight() - heightReduce / 2, mPaint);
        }
        //绘制上面的条形
        surfaceHolder.unlockCanvasAndPost(canvas);
        Log.i("wrt", "draw====");
    }

}
