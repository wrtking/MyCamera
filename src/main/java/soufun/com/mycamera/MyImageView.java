package soufun.com.mycamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by wrt on 2017/11/15.
 */

public class MyImageView extends AppCompatImageView{
    private Paint mPaint;
    private Bitmap bitmap;
    private int widthBitmap,heightBitmap;
    private int widthReduce,heightReduce;

    public MyImageView(Context context){
        this(context,null);
    }
    public MyImageView(Context context, AttributeSet attributeSet){
        this(context,attributeSet,0);
    }
    public MyImageView(Context context,AttributeSet attributeSet,int deflyable){
        super(context,attributeSet,deflyable);
        bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.card_photo);
        widthBitmap=bitmap.getWidth();
        heightBitmap=bitmap.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMeasureSpecMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthMeasureSpecSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightMeasureSpecMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightMeasureSpecSize=MeasureSpec.getSize(heightMeasureSpec);
        if(widthMeasureSpecMode==MeasureSpec.AT_MOST&&heightMeasureSpecMode==MeasureSpec.AT_MOST){
            setMeasuredDimension(360,320);
        }else if(widthMeasureSpecMode==MeasureSpec.AT_MOST){
            setMeasuredDimension(360,heightMeasureSpecSize);
        }else if(heightMeasureSpecMode==MeasureSpec.AT_MOST){
            setMeasuredDimension(widthMeasureSpecSize,320);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        widthReduce=getMeasuredWidth()-widthBitmap;
        heightReduce=getMeasuredHeight()-heightBitmap;
        mPaint=new Paint();
        mPaint.setColor(getResources().getColor(R.color.colorSurfaceView));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(180);
        //绘制上面的条形
        canvas.drawRect(0, 0, getMeasuredWidth(), heightReduce / 2, mPaint);
        //绘制下面的条形
        canvas.drawRect(0, getMeasuredHeight() - heightReduce / 2, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        //绘制左边的条形
        canvas.drawRect(0, heightReduce / 2, widthReduce / 2, getMeasuredHeight() - heightReduce / 2, mPaint);
        //绘制右边的条形
        canvas.drawRect(getMeasuredWidth() - widthReduce / 2, heightReduce / 2, getMeasuredWidth(), getMeasuredHeight() - heightReduce / 2, mPaint);
        canvas.drawBitmap(bitmap,widthReduce/2,heightReduce/2,null);
        Log.i("wrt","widthReduce0000==="+widthReduce+"---heightReduce0000=="+heightReduce);
        Log.i("wrt","widthBitmap000==="+widthBitmap+"---heightBitmap0000=="+heightBitmap);
        Log.i("wrt","getMeasuredWidth==="+getMeasuredWidth()+"----getMeasureHeight=="+getMeasuredHeight());
    }
}
