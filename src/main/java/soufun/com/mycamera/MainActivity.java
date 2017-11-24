package soufun.com.mycamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wrt on 2017/11/20.
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback {
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Button btn;
    private int mScreenWidth, mScreenHeight;
    private MyImageView myImageView;
    private RelativeLayout rllayout;
    private int widthBitmap, heightBitmap, widthPhone, heightPhone, widthReduce, heightReduce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        myImageView = (MyImageView) findViewById(R.id.myImageView);
        rllayout = (RelativeLayout) findViewById(R.id.rllayout);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPictureCallback);
            }
        });
        getScreenMetrix(MainActivity.this);

    }

    private void init() {
        mHolder = surfaceView.getHolder();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mHolder.addCallback(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            try {
                mCamera = mCamera.open();
            } catch (Exception e) {
                mCamera = null;
                Log.i("wrt", "相机打开失败" + e.toString());
            }
        }
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
        }
        mCamera = null;
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
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Camera.Parameters parameters;
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
                return;
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
        Log.i("wrt", "mScreenWidth==" + mScreenWidth + "---mScreenHeight==" + mScreenHeight);
        //自定义控件MyImageView的宽高
        widthBitmap = myImageView.getMeasuredWidth();
        heightBitmap = myImageView.getMeasuredHeight();
        Log.i("wrt", "widthBitmap==" + widthBitmap + "---heightBitmap==" + heightBitmap);
        //给的图片的宽高
        widthPhone = BitmapFactory.decodeResource(getResources(), R.mipmap.card_photo).getWidth();
        heightPhone = BitmapFactory.decodeResource(getResources(), R.mipmap.card_photo).getHeight();
        Log.i("wrt", "widthPhone==" + widthPhone + "---heightPhone==" + heightPhone);
        //得到的差距
        widthReduce = widthBitmap - widthPhone;
        heightReduce = heightBitmap - heightPhone;
        Log.i("wrt", "widthReduce==" + widthPhone / 2 + "---widthReduce==" + heightPhone / 2);

        //获取摄像头支持的pictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
            Log.i("wrt", "size.width==" + size.width + "---size.heigth==" + size.height);
        }
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) mScreenHeight) / mScreenWidth);
        if (null != picSize) {
            Log.i("wrt", "picSize===" + picSize.width + "-----" + picSize.height);
            parameters.setPictureSize(picSize.width, picSize.height);
        }
        //获取摄像头支持的previewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : previewSizeList) {
            Log.i("wrt", "size.width000==" + size.width + "---size.heigth000==" + size.height);
        }
        Camera.Size previewSize = getProperSize(previewSizeList, ((float) mScreenHeight) / mScreenWidth);

        if (null != previewSize) {
            Log.i("wrt", "previewSiz===" + previewSize.width + "-----" + previewSize.height);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }

        parameters.setPictureFormat(ImageFormat.JPEG);
        //自动对焦
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        mCamera.cancelAutoFocus();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
        }
        mCamera = null;
    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h=4f:3
     *
     * @param pictureSizeList
     * @param screenRatio
     * @return
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i("wrt", "screenRatio==" + screenRatio);
        Camera.Size result = null;
        List<Camera.Size> list = new ArrayList<Camera.Size>();
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0 && (widthReduce + widthPhone) < size.width && (heightReduce + heightPhone) < size.height) {
                list.add(size);
            }
        }
        if (list.size() == 0) {
            Log.i("wrt", "--------------");
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3 && (widthReduce + widthPhone) < size.width && (heightReduce + heightPhone) < size.height) {
                    list.add(size);
                }
            }
        }
        //选择所有合适分辨率里面最小的
        int min = 0;
        if (list.size() > 0) {
            min = list.get(0).width;
        }
        for (int i = 0; i < list.size(); i++) {
            if (min >= list.get(i).width) {
                min = list.get(i).width;
                result = list.get(i);
            }
        }
        return result;
    }

    /**
     * 获取屏幕的宽高，单位是px
     *
     * @param context
     */
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }


    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            Log.i("wrt", "bitmap.getWidth()==" + bitmap.getWidth() + "---bitmap.getHeight()==" + bitmap.getHeight());


            Bitmap rectBitmap = Bitmap.createBitmap(bitmap1, widthReduce / 2, heightReduce / 2, widthPhone, heightPhone);

            String savaPath = "/mnt/sdcard/rectPhoto/";
            long dataTake = System.currentTimeMillis();
            String jpegName = savaPath + dataTake + ".jpg";
            File folder = new File(jpegName);
            if (!folder.getParentFile().exists()) {
                folder.getParentFile().mkdirs();
            }
            try {
                FileOutputStream fos = new FileOutputStream(folder);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                rectBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                Intent intent = new Intent(MainActivity.this, MyActivity.class);
                intent.putExtra("filePath", folder.getAbsolutePath());
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("wrt", "e=====" + e.toString());
            }
        }
    };
}
