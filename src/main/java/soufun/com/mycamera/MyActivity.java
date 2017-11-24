package soufun.com.mycamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by wrt on 2017/11/14.
 */

public class MyActivity extends Activity{
    private ImageView iv;
    private String filePath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myactivity);
        iv=(ImageView)findViewById(R.id.iv);
        Intent intent=getIntent();
        filePath=intent.getStringExtra("filePath");
        Log.i("wrt","filePath=="+filePath);

        Bitmap bitmap= BitmapFactory.decodeFile(filePath);
        iv.setImageBitmap(bitmap);

    }
}
