package com.manonpeter.ptol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.manonpeter.ptol.R;
import com.manonpeter.ptol.activity.utils.ActivityUtils;
import com.manonpeter.ptol.ftp.FTPUtils;

import java.util.List;

public class ImgLinkManage extends AppCompatActivity {

    // 图片链接存储容器 ， 存储图片链接控件容器 ， 存储图片链接按钮控件容器
    private LinearLayout imgLinkList , imgLinkFrame , imgLinkButtonFrame;
    // 删除按钮 ， 复制按钮
    private Button deleteBtn , copyBtn;
    // 显示图片链接TextView
    private TextView imgLinkLable;

    // layout_warp 和 layout_match
    private LinearLayout.LayoutParams match_wrap = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            , warp_warp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_link_manage);

        imgLinkList = findViewById(R.id.imgLinkList);
        // 加载图片链接列表
        loadImgLinkList();
    }

    /**
     * 加载图片链接列表
     */
    private void loadImgLinkList(){
        imgLinkList.removeAllViews();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> list = FTPUtils.getImgLinkList();
                for(final String imgLink : list){

                    Handler handler=new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            imgLinkList.addView(getImgLinkListControl(ImgLinkManage.this , imgLink));
                            imgLinkList.addView(getImgLinkListControl2(ImgLinkManage.this , imgLink));
                        }
                    });
                }
            }
        }).start();

//        imgLinkList.addView(getImgLinkListControl2(ImgLinkManage.this , "http://baiduyundc00.hkg03.bdysite.com/images/lm.jpg"));
    }

    /**
     * 得到图片链接列表控件
     * @param c
     * @param imgLink
     * @return
     */
    private LinearLayout getImgLinkListControl(final Context c , final String imgLink){

        imgLinkFrame = new LinearLayout(c);
        imgLinkFrame.setOrientation(LinearLayout.VERTICAL);
        imgLinkFrame.setLayoutParams(match_wrap);

        imgLinkLable = new TextView(c);
        imgLinkLable.setText(imgLink);
        imgLinkLable.setLayoutParams(match_wrap);


        imgLinkButtonFrame = new LinearLayout(c);
        imgLinkButtonFrame.setOrientation(LinearLayout.HORIZONTAL);
        imgLinkButtonFrame.setLayoutParams(match_wrap);

        deleteBtn = new Button(c);
        deleteBtn.setText("删除");
        deleteBtn.setLayoutParams(warp_warp);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 删除成功，则更新图片链接列表
                        if (FTPUtils.delImgLink(imgLink)){
                            Handler handler=new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // 更新图片链接列表
                                    loadImgLinkList();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        copyBtn = new Button(c);
        copyBtn.setText("复制");
        copyBtn.setLayoutParams(warp_warp);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.copy( c , imgLink);
            }
        });

        imgLinkFrame.addView(imgLinkLable);
        imgLinkFrame.addView(imgLinkButtonFrame);
        imgLinkButtonFrame.addView(deleteBtn);
        imgLinkButtonFrame.addView(copyBtn);

        return imgLinkFrame;
    }


    /**
     * 得到图片链接列表控件
     * @param c
     * @param imgLink
     * @return
     */
    private LinearLayout getImgLinkListControl2(final Context c , final String imgLink){

        imgLinkFrame = new LinearLayout(c);
        imgLinkFrame.setOrientation(LinearLayout.VERTICAL);
        imgLinkFrame.setLayoutParams(match_wrap);

        ImageView img = new ImageView(c);
        img.setLayoutParams(warp_warp);
        Glide.with(c)
                .load(imgLink)
                .into(img);

        imgLinkButtonFrame = new LinearLayout(c);
        imgLinkButtonFrame.setOrientation(LinearLayout.HORIZONTAL);
        imgLinkButtonFrame.setLayoutParams(match_wrap);

        deleteBtn = new Button(c);
        deleteBtn.setText("删除");
        deleteBtn.setLayoutParams(warp_warp);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 删除成功，则更新图片链接列表
                        if (FTPUtils.delImgLink(imgLink)){
                            Handler handler=new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // 更新图片链接列表
                                    loadImgLinkList();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        copyBtn = new Button(c);
        copyBtn.setText("复制");
        copyBtn.setLayoutParams(warp_warp);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.copy( c , imgLink);
            }
        });

        imgLinkFrame.addView(img);
        imgLinkFrame.addView(imgLinkButtonFrame);
        imgLinkButtonFrame.addView(deleteBtn);
        imgLinkButtonFrame.addView(copyBtn);

        return imgLinkFrame;
    }

}
