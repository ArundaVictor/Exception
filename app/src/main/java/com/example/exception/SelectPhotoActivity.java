package com.example.exception;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SelectPhotoActivity extends AppCompatActivity {

    public static final int MY_REQUEST_CAMERA   = 10;
    public static final int MY_REQUEST_WRITE_CAMERA   = 11;
    public static final int CAPTURE_CAMERA   = 12;

    public static final int MY_REQUEST_READ_GALLERY   = 13;
    public static final int MY_REQUEST_WRITE_GALLERY   = 14;
    public static final int MY_REQUEST_GALLERY   = 15;

    private BottomSheetBehavior selectPhotoBehaviour;
    private SimpleDraweeView swView;

    public File filen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        LinearLayout llselect = (LinearLayout)findViewById(R.id.llselect);
        selectPhotoBehaviour = BottomSheetBehavior.from(llselect);

        swView = (SimpleDraweeView) findViewById(R.id.img1);
        swView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectPhotoBehaviour.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    selectPhotoBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    selectPhotoBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        TextView txtcamera = (TextView)findViewById(R.id.dlg_camera);
        TextView txtgallery = (TextView)findViewById(R.id.dlg_gallery);

        txtcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                checkPermissionCW();
            }
        });

        txtgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                checkPermissionRG();
            }
        });

    }

    private void checkPermissionCA(){
        int permissionCheck = ContextCompat.checkSelfPermission(SelectPhotoActivity.this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SelectPhotoActivity.this, new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CAMERA);
        } else {
            catchPhoto();
        }
    }
    private void checkPermissionCW(){
        int permissionCheck = ContextCompat.checkSelfPermission(SelectPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SelectPhotoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_WRITE_CAMERA);
        } else {
            checkPermissionCA();
        }
    }
    private void catchPhoto() {
        filen = getFile();
        if(filen!=null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                Uri photocUri = Uri.fromFile(filen);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photocUri);
                startActivityForResult(intent, CAPTURE_CAMERA);
            } catch (ActivityNotFoundException e) {

            }
        } else {
            Toast.makeText(SelectPhotoActivity.this, "please check your sdcard status", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionRG(){
        int permissionCheck = ContextCompat.checkSelfPermission(SelectPhotoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SelectPhotoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_READ_GALLERY);
        } else {
            checkPermissionWG();
        }
    }
    private void checkPermissionWG(){
        int permissionCheck = ContextCompat.checkSelfPermission(SelectPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // int permissionCheck2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SelectPhotoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_WRITE_GALLERY);
        } else {
            getPhotos();
        }
    }
    private void getPhotos() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, MY_REQUEST_GALLERY);
    }

    public File getFile(){

        File fileDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        if (!fileDir.exists()){
            if (!fileDir.mkdirs()){
                return null;
            }
        }


        File mediaFile = new File(fileDir.getPath() + File.separator + "temp.jpg");
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK){
            Log.e("msg", "photo not get");
            return;
        }

        switch (requestCode) {

            case CAPTURE_CAMERA:

                swView.setImageURI(Uri.parse("file:///" + filen));
                break;


            case MY_REQUEST_GALLERY:
                try {

                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    filen = getFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(filen);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    swView.setImageURI(Uri.parse("file:///" + filen));//fresco library

                } catch (Exception e) {

                    Log.e("", "Error while creating temp file", e);
                }
                break;

        }
    }

    public void onRequestPermissionsResult (int requestCode, String[] permissions,  int[] grantResults)
    {

        switch (requestCode) {
            case MY_REQUEST_CAMERA:
                catchPhoto();
                break;
            case MY_REQUEST_WRITE_CAMERA:
                checkPermissionCA();
                break;
            case MY_REQUEST_READ_GALLERY:
                checkPermissionWG();
                break;
            case MY_REQUEST_WRITE_GALLERY:
                getPhotos();
                break;
        }
    }
}
