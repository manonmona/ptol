package com.manonpeter.ptol.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.manonpeter.ptol.R;
import com.manonpeter.ptol.activity.utils.ActivityUtils;
import com.manonpeter.ptol.ftp.FTPUtils;
import com.manonpeter.ptol.utils.PropUtils;

import java.io.IOException;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    // 上传图片按钮 ， 管理图片链接按钮 ， 例子按钮
    private Button uploadBtn,picListBtn,expBth;
    // 复制对话框
    private AlertDialog copyLinkDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // *************** 加载ptol.properties **************************
        try {
            // 1.
            Properties prop = new Properties();
            // 2.加载assets 文件夹中的ptol.properites 文件
            prop.load(this.getAssets().open("ptol.properties"));
            // 3.将得到的ptol.properties 保存起来
            PropUtils.init(prop);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取可用图片链接
        picListBtn = (Button)this.findViewById(R.id.picListBtn);
        picListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ImgLinkManage.class);
                startActivity(intent);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<String> list = FTPUtils.getImgLinkList();
//                        for(String str : list){
//                            System.out.println(str);
//                        }
//                    }
//                }).start();
            }
        });

        // 获取layout中上传按钮,打开相册选择图片
        uploadBtn = (Button)this.findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else{
                    openAlbum();
                }
            }
        });


        // 打开例子
        expBth = (Button)this.findViewById(R.id.expBth);
        expBth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 打开例子activity
                /*Intent intent = new Intent();
                intent.setClass(MainActivity.this,PhotoExample.class);
                startActivity(intent);*/
                //如果不关闭当前的会出现好多个页面
//                MainActivity.this.finish();

            }
        });
        // 隐藏该按钮
        expBth.setVisibility(View.INVISIBLE);
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }

    /**
     * 请求权限结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this, "You denied the permision.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /*case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;*/
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT >= 19){
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }else{
                        //4.4以下系统使用这个方法处理图片
                        handeleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
//    Toast.makeText(this,"到了handleImageOnKitKat(Intent data)方法了", Toast.LENGTH_LONG).show();
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            //如果是 document 类型的 Uri，则通过 document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的 id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是 content 类型的 uri ， 则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是 file 类型的 Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        uploadImage(imagePath);//显示选中的图片
    }

    private void handeleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        uploadImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过 Uri 和 selection 来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void uploadImage(final String imagePath) {
        if(imagePath != null){
            // 1.检查对话框copyLinkDialog 是否已初始化
            uploadImgSuccessDialogInit();
            // 2.开启子线-------主线程不能做耗时操作，所以需要启动子线程完成
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 3.上传图片
                    final String imgLink = FTPUtils.upload(imagePath);

                    // 4.显示上传成功对话框-----------这里需要使用handler处理
                    Handler handler=new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 4.显示上传成功对话框
                            uploadImgSuccessDialogShow(imgLink);
                        }
                    });
                }
            }).start();
        }else{
            Toast.makeText(this,"failed to get image", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 上传成功对话框
     */
    private void uploadImgSuccessDialogShow(final String imageLink){
        // 1.设置对话框提示内容
        copyLinkDialog.setMessage(imageLink);
        // 2.显示对话框
        copyLinkDialog.show();
        // 3.为对话框复制按钮设置点击事件
        copyLinkDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.copy( MainActivity.this , imageLink );
                if(1 != 2)return;
                copyLinkDialog.dismiss();
            }
        });
    }

    /**
     * 初始化--------图片上传工作对话框
     */
    private void uploadImgSuccessDialogInit(){
        if(copyLinkDialog == null){
            copyLinkDialog = new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setTitle("图片上传成功！")
                    .setNeutralButton("关闭" , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton("复制图片链接地址" , null)
                    .setCancelable(true)
                    .create();
        }
    }


}
