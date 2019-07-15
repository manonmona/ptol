package com.manonpeter.ptol;

import org.junit.Test;

public class StringUnitTest {
    @Test
    public void fun1(){
        delImgLink("/webroot/" , "https://123/lm.jpg");
    }
    private boolean delImgLink(String replacePath ,String imgLink){

        // 存储文件地址集合
        if(imgLink == null){
            return false;
        }
        System.out.println(imgLink);
        // 得到“/”最后出现的位置
        imgLink = imgLink.replace("https://123/" , replacePath);
        int lastindex = imgLink.lastIndexOf("/");
        // 得到路径
        String workingDirectory = imgLink.substring(0 ,lastindex + 1);

        System.out.println(workingDirectory);
        System.out.println(imgLink);
        return false;
    }
}
