package com.example.facedetectapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.AsyncTask;
import android.util.FloatMath;
import android.util.Log;

/**
 * 非同期で顔検出する
 * @author utsumiisao
 *
 */
public class FaceDetectAsync extends AsyncTask<Bitmap, Void, Face[]> {
    
    /**
     * 検出する顔の数
     */
    private final int MAX_FACES = 10;
    
    /**
     * 検出した顔の数
     */
    private int mFaceNum  = 0;
    
    /**
     * 画像編集するためのCanvas
     */
    private Canvas mCanvas;
    
    /**
     * 画像編集されたbitmap
     */
    private Bitmap mEditBitmap;
    
    /**
     * Activityから呼び出されるCallback interface
     */
    private Callback mCallback;
    
    private ProgressDialog mWaitDialog;
    
    /**
     * コンストラクタ
     */
    public FaceDetectAsync(ProgressDialog waitDialog, Callback callback) {
        mWaitDialog = waitDialog;
        mCallback = callback;
    }
    
    @Override
    protected void onPreExecute() {
        if(mWaitDialog!=null) {
            mWaitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mWaitDialog.setCanceledOnTouchOutside(false);
            mWaitDialog.setMessage("処理中です...");
            mWaitDialog.show();    
        }        
        super.onPreExecute();
    }
    
    @Override
    protected Face[] doInBackground(Bitmap... params) {
        mEditBitmap = detect(params[0]);        
        mCanvas = new Canvas(mEditBitmap);
        Face[] faces = new Face[MAX_FACES];
        FaceDetector detector = new FaceDetector(mEditBitmap.getWidth(), mEditBitmap.getHeight(), MAX_FACES);
        mFaceNum = detector.findFaces(mEditBitmap, faces);
        return faces;
    }
    
    @Override
    protected void onPostExecute(Face[] faces) {
        if(mWaitDialog!=null) {
            mWaitDialog.dismiss();
        }
        Paint paint = getPaint();
        String mark = "★";
        FontMetrics fontMetrics = paint.getFontMetrics();
        //小数点切り上げ
        int textWidth = (int) FloatMath.ceil(paint.measureText(mark));
        // http://wikiwiki.jp/android/?%A5%C6%A5%AD%A5%B9%A5%C8%A4%CE%C9%C1%B2%E8(FontMetrics) 参照
        int textHeight = (int) FloatMath.ceil(Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.leading));
        if(mFaceNum>0) {
            for (Face face : faces) {
                if(face == null) {
                    continue;
                }
                PointF point = new PointF();
                //目と目の間の座標を取得
                face.getMidPoint(point);
                float disH = face.eyesDistance() / 2;
                //右目
                float leftX = point.x - disH - (textWidth / 2);
                float leftY = point.y + (textHeight / 2);
                mCanvas.drawText(mark, leftX, leftY, paint);
                
                //左目
                float rightX = point.x + disH - (textWidth / 2);
                float rightY = point.y + (textHeight / 2);
                mCanvas.drawText(mark, rightX, rightY, paint);
                Log.d("------pose-----", String.valueOf(face.pose(Face.EULER_X)));
            }
        }        
        
        mCallback.onPostExcute(mEditBitmap);
        super.onPostExecute(faces);
    }
    
    /**
     * Paintインスタンス取得
     * @return
     */
    private Paint getPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(100);
        return paint;
    }
    
    /**
     * 16bitのBitampに変換する
     * @param original
     * @return
     */
    private Bitmap detect(Bitmap original) {
        Bitmap copy = original.copy(Bitmap.Config.RGB_565, true);
        original.recycle();
        return copy;
    }
    
    public static interface Callback {
        public void onPostExcute(Bitmap editBitmap);
    }

}
