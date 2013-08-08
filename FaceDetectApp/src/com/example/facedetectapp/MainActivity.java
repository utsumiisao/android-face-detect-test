package com.example.facedetectapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 顔写真がある画像から顔を検出して目に☆マークを付ける
 * @author utsumiisao
 *
 */
public class MainActivity extends Activity {
    
    private Button mGalleryBtn;
    
    private Button mFaceDetectBtn;

    private ImageView mFaceImageView;
    
    /**
     * ギャラリーリクエストコード
     */
    private static final int GALLERY = 11111;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mGalleryBtn = (Button)findViewById(R.id.gallery_btn);
        mGalleryBtn.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_PICK);
                startActivityForResult(galleryIntent, GALLERY);
            }
        });
        
        mFaceDetectBtn = (Button)findViewById(R.id.face_detect_btn);
        mFaceDetectBtn.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {                
                FaceDetectAsync task = new FaceDetectAsync(new ProgressDialog(MainActivity.this) ,new FaceDetectAsync.Callback() {
                    @Override
                    public void onPostExcute(Bitmap editBitmap) {
                        mFaceImageView.setImageBitmap(editBitmap);                
                    }
                });
                task.execute(getImageViewToBitmap(mFaceImageView));
            }
        });        
        
        mFaceImageView = (ImageView)findViewById(R.id.face_image);
                
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK) {
            return;
        }
        
        switch (requestCode) {
        case GALLERY:
            Uri uri = data.getData();
            mFaceImageView.setImageURI(uri);
            break;

        default:
            break;
        }
    }
    
    /**
     * ImageViewからbitmapを取得する
     * @param imageView
     * @return
     */
    private Bitmap getImageViewToBitmap(ImageView imageView) {
        BitmapDrawable bd = (BitmapDrawable)imageView.getDrawable();
        return bd.getBitmap();
    }
    



}
