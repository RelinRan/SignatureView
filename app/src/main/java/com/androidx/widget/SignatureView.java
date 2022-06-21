package com.androidx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 签名控件
 */
public class SignatureView extends View {

    //路径
    private Path path;
    //画笔
    private Paint paint;
    //画笔颜色
    private int strokeColor;
    //线宽
    private float strokeWidth;
    //路径
    private List<Path> paths;
    //文件夹
    private String dir = "Signature";

    public SignatureView(Context context) {
        super(context);
        iniAttributeSet(context, null);
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        iniAttributeSet(context, attrs);
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniAttributeSet(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void iniAttributeSet(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SignatureView);
            strokeColor = array.getColor(R.styleable.SignatureView_strokeColor, Color.parseColor("#333333"));
            strokeWidth = array.getDimension(R.styleable.SignatureView_strokeWidth, 8);
            dir = array.getString(R.styleable.SignatureView_dir);
            dir = dir == null ? "Signature" : dir;
            array.recycle();
        }
        paths = new ArrayList<>();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                path.moveTo(x, y);
                paths.add(path);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                paths.add(path);
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(strokeColor);
        paint.setStrokeWidth(strokeWidth);
        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paint);
        }
    }

    /**
     * 同步获取文件
     */
    public File getFile() {
        return toFile(getBitmap(), null);
    }

    /**
     * 异步获取文件
     *
     * @param listener
     */
    public void getFile(OnSignatureFileListener listener) {
        toFile(getBitmap(), listener);
    }

    /**
     * @param bitmap 位图
     * @return 转换Bitmap为文件
     */
    public File toFile(Bitmap bitmap, OnSignatureFileListener listener) {
        bitmap = removeBackground(bitmap);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        File dirFile = new File(getContext().getExternalCacheDir(), dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(dirFile, "IMS_" + format.format(new Date()) + ".png");
        BufferedOutputStream stream;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            if (listener != null) {
                listener.onSignatureFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 移除文件背景
     *
     * @param bitmap 图片文件
     * @return
     */
    public Bitmap removeBackground(Bitmap bitmap) {
        int portraitWidth = bitmap.getWidth();
        int portraitHeight = bitmap.getHeight();
        int[] colors = new int[portraitWidth * portraitHeight];
        bitmap.getPixels(colors, 0, portraitWidth, 0, 0, portraitWidth, portraitHeight);// 获得图片的ARGB值
        for (int i = 0; i < colors.length; i++) {
            int a = Color.alpha(colors[i]);
            int r = Color.red(colors[i]);
            int g = Color.green(colors[i]);
            int b = Color.blue(colors[i]);
            if (r > 240 && g > 240 && b > 240) {
                colors[i] = 0x00FFFFFF;
            }
        }
        return Bitmap.createBitmap(colors, 0, portraitWidth, portraitWidth, portraitHeight, Bitmap.Config.ARGB_4444);
    }

    /**
     * @return 绘制bitmap
     */
    public Bitmap getBitmap() {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            int specSize = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED);
            measure(specSize, specSize);
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
        if (width <= 0 || height <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (getRight() <= 0 || getBottom() <= 0) {
            layout(0, 0, width, height);
            draw(canvas);
        } else {
            layout(getLeft(), getTop(), getRight(), getBottom());
            draw(canvas);
        }
        return bitmap;
    }

    public interface OnSignatureFileListener {

        void onSignatureFile(File file);

    }

    /**
     * 重置
     */
    public void clear() {
        paths.clear();
        invalidate();
    }

    /**
     * 设置线条宽度
     *
     * @param strokeWidth
     */
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    /**
     * 设置线条颜色
     *
     * @param strokeColor
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    /**
     * 设置文件夹名
     *
     * @param dir
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * 获取文件夹
     *
     * @return
     */
    public File getDirFile() {
        return new File(getContext().getExternalCacheDir() + File.separator + dir);
    }

}

