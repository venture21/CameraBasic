package com.venture.android.camerabasic;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private final int REQ_PERMISSION = 100;           // 권한 요청 코드
    private final int REQ_CAMERA     = 101;           // 카메라 권한 요청
    private final int REQ_GALLERY    = 102;


    Button btnCamera;
    Button btnGallery;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWidget();
        buttonDisable();
        setListener();
        checkPermission();

    }
    // 버튼 비활성화 하기
    private void buttonDisable() {
        btnCamera.setEnabled(false);
    }

    private void buttonEnable() {
        btnCamera.setEnabled(true);
    }

    private void init(){
        // 권한 처리가 통과 되었을때만 버튼을 활성화 시켜준다.
        buttonEnable();
    }


    // 권한 관련 체크
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        // 버전체크를 통해 마시멜로보다 낮으면 런타임권한 체크를 하지 않는다.
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (PermissionControl.checkPermission(this, REQ_PERMISSION)) {
                init();
            }
        } else {
            init();

        }
    }

    // 위젯 세팅
    private void setWidget() {
        btnCamera  = (Button) findViewById(R.id.btnCamera);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        imageView  = (ImageView) findViewById(R.id.imageView);
    }

    // 리스너 세팅
    private void setListener(){
        btnCamera.setOnClickListener(clickListener);
        btnGallery.setOnClickListener(clickListener);
    }

    // 리스너 정의
    Uri fileUri = null;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch(v.getId()){
                case R.id.btnCamera: //카메라 버튼 동작
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // 롤리팝 이상 버전에서는 아래 코드를 반영해야 한다.
                    // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                        ContentValues values = new ContentValues(1);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    // --- 여기 까지 컨텐트 uri 강제세팅 ---

                    startActivityForResult(intent, REQ_CAMERA);
                    break;

                case R.id.btnGallery:
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // 이미지 여러개 가져오는 플래그
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("image/*");   // 외부 저장소에 있는 이미지만 가져오기 위한 필터링
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("Camera","resultCode==============================="+resultCode);

        switch(requestCode) {

            case REQ_GALLERY:
                if(resultCode == RESULT_OK) {
                    fileUri = data.getData();
                    Glide.with(this)
                            .load(fileUri)
                            .into(imageView);
                }
                break;
            case REQ_CAMERA :
                if (requestCode == REQ_CAMERA && resultCode == RESULT_OK) { // 사진 확인처리됨 RESULT_OK = -1
                    // 롤리팝 체크
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        Log.i("Camera", "data.getData()===============================" + data.getData());
                        fileUri = data.getData();
                    }
                    Log.i("Camera", "fileUri===============================" + fileUri);
                    if (fileUri != null) {
                        // 글라이드로 이미지 세팅하면 자동으로 사이즈 조절
                        Glide.with(this)
                                .load(fileUri)
                                .into(imageView);
                    } else {
                        Toast.makeText(this, "사진파일이 없습니다", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // resultCode 가 0이고 사진이 찍혔으면 uri 가 남는데
                    // uri 가 있을 경우 삭제처리...
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_PERMISSION){
            if( PermissionControl.onCheckResult(grantResults)){
                init();
            }else{
                Toast.makeText(this, "권한을 허용하지 않으시면 프로그램을 실행할 수 없습니다.", Toast.LENGTH_LONG).show();
                //finish();
            }
        }
    }
}
