package ir.cafebazaar.notepad.activities.home;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import ir.cafebazaar.notepad.R;
import ir.cafebazaar.notepad.utils.BitmapTool;
import ir.cafebazaar.notepad.utils.GlideEngine;

public class SsfPhotoCameraActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "SsfPhotoCameraActivity";


    Button mPhotoButton = null;
    private ImageView imageView;
    Button mCameraButton = null;

    private static final int CAMERA = 101;
    private static final int PHOTO = 100;

    Button mPhotoCameraButton = null;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onCreate()");
        super.onCreate(arg0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }

        applyWritePermission();

        this.setTitle("相册");
        this.setContentView(R.layout.activity_photo);

        mPhotoButton = (Button) this.findViewById(R.id.photo_ssf);
        mPhotoButton.setOnClickListener(this);
        mCameraButton = this.findViewById(R.id.camera_ssf);
        mCameraButton.setOnClickListener(this);
        imageView = (ImageView) this.findViewById(R.id.imageView);

        mPhotoCameraButton = this.findViewById(R.id.photo_camera_ssf);

    }


    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick()");
        int id = v.getId();
        if (id == R.id.photo_ssf) {
            toPicture();
        } else if (id == R.id.camera_ssf) {
            toCamera();
        }else if(id == R.id.photo_camera_ssf){
            toPictureCamera();
        }

    }

    //跳转相册
    private void toPictureCamera() {
//        Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
//        intent.setType("image/*");
//        startActivityForResult(intent, 100);
        EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                .setCount(9)
//                .start(PHOTO);

                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        for(int i = 0; i < photos.size(); i++){
                            Log.e(TAG, photos.get(i).toString());
                        }
                        ContentResolver cr = SsfPhotoCameraActivity.this.getContentResolver();
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(photos.get(0).uri));
                            imageView.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            Log.e("Exception", e.getMessage(), e);
                        }

                    }
                });
        Log.i(TAG, "跳转相册摄像机成功");
    }

    //跳转相册
    private void toPicture() {
//        Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
//        intent.setType("image/*");
//        startActivityForResult(intent, 100);
        EasyPhotos.createAlbum(this, false, GlideEngine.getInstance())
                .start(PHOTO);
        Log.i(TAG, "跳转相册成功");
    }

    //跳转相机
    private void toCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  //跳转到 ACTION_IMAGE_CAPTURE
//        //判断内存卡是否可用，可用的话就进行存储
//        //putExtra：取值，Uri.fromFile：传一个拍照所得到的文件，fileImg.jpg：文件名
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "fileImg.jpg")));
//        startActivityForResult(intent, 101); // 101: 相机的返回码参数（随便一个值就行，只要不冲突就好）
        EasyPhotos.createCamera(this)
                .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                .start(CAMERA);
        Log.e(TAG, "跳转相机成功");
    }

    private void applyWritePermission() {

        String permissions1 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String permissions2 = Manifest.permission.READ_EXTERNAL_STORAGE;
        String permissions3 = Manifest.permission.CAMERA;

        if (Build.VERSION.SDK_INT >= 23) {
            int check1 = ContextCompat.checkSelfPermission(this, permissions1);
            if (check1 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            int check2 = ContextCompat.checkSelfPermission(this, permissions2);
            if (check2 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            int check3 = ContextCompat.checkSelfPermission(this, permissions3);
            if (check3 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //从相册返回
        if (requestCode == PHOTO && resultCode == Activity.RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
            ContentResolver cr = this.getContentResolver();
            ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);

            Log.e(TAG, String.valueOf(resultPhotos.size()));
            for(int i = 0; i < resultPhotos.size(); i++){
                Log.e(TAG, resultPhotos.get(i).toString());
            }
            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(resultPhotos.get(0).uri));
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }

        //从相机返回
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK && data != null) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ContentResolver cr = this.getContentResolver();
            ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
            for(int i = 0; i < resultPhotos.size(); i++){
                Log.e(TAG, resultPhotos.get(i).toString());
            }

//            imageView.setImageBitmap(photo);
            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(resultPhotos.get(0).uri));
                imageView.setImageBitmap(bitmap);
//                BitmapTool.saveBitmap(resultPhotos.get(0).name, bitmap, this);

            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }


    }


}
