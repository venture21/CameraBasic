package com.venture.android.camerabasic;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

public class GalleryActivity extends AppCompatActivity {

    Cursor imageCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
    }


    private void init() {
        String[] img = {MediaStore.Images.Thumbnails._ID};


    }




}
