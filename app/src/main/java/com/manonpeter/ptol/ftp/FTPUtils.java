package com.manonpeter.ptol.ftp;

import com.manonpeter.ptol.utils.CommonUtils;
import com.manonpeter.ptol.utils.PropUtils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * FTP
 */
public class FTPUtils {

    // ftp地址 ， ftp admin账号 ， ftp admin密码 , url , 工作目录
    private static String hostname , username , password , url , workingDir , replacePath;
    // ftp端口
    private static int port, mode;
    // FTPClient;
    private static FTPClient ftpClient;

    static {
        Properties p = PropUtils.getProp();
        if(p != null){
            hostname = p.getProperty("com.manonpeter.ptol.ftp.hostname");
            username = p.getProperty("com.manonpeter.ptol.ftp.admin.username");
            password = p.getProperty("com.manonpeter.ptol.ftp.admin.password");
            url = p.getProperty("com.manonpeter.ptol.url");
            workingDir = p.getProperty("com.manonpeter.ptol.ftp.wd");
            replacePath = p.getProperty("com.manonpeter.ptol.replace.path");

            port = Integer.parseInt(p.getProperty("com.manonpeter.ptol.ftp.port"));
            mode = Integer.parseInt(p.getProperty("com.manonpeter.ptol.ftp.mode"));
        }else{
            throw new RuntimeException("ptol.properties 加载失败！");
        }
    }

    /**
     * 初始化
     */
    private static void init(){
        if(ftpClient==null || ftpClient.getReplyCode()!=230){
            ftpLogin();
        }
    }

    /**
     * 返回FTPClient实例对象
     * @return
     */
    public static FTPClient getFTPClient(){
        // 1.检查fileClient 是否已初始化
        init();

        return ftpClient;
    }

    /**
     * FTPClient登录
     */
    private static void ftpLogin(){
        // 1.初始化
        ftpClient = new FTPClient();

        // 2.登录
        try {
            ftpClient.connect(hostname, port);
            ftpClient.login(username,password);
            // 3.设置连接模式
            if(mode==0){
                ftpClient.enterLocalPassiveMode(); //被动模式
            }else{
                ftpClient.enterLocalActiveMode();    //主动模式
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传FTP 服务器
     * @param filePath 文件路径
     * @return 图片链接地址
     */
    public static String upload(String filePath){
        // 1.检查fileClient 是否已初始化
        init();

        File file = new File(filePath);
        if(file.isFile()){
            try{
                // 截取文件后缀“.”之后的格式，除文件类型，重命名文件名
                int lastIndex = file.getName().lastIndexOf(".");
                String newFileName = CommonUtils.getUUID() + file.getName().substring(lastIndex);

                // 设置文件类型，“二进制文件类型”
                ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
                ftpClient.makeDirectory(workingDir);// 如目录不存在则创建
                ftpClient.changeWorkingDirectory(workingDir);// 更换工作路径
                // 上传文件
                ftpClient.storeFile( newFileName , new FileInputStream(file));
                // 上传成功后返回链接地址
                return workingDir.replace(replacePath , url) + newFileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取图片链接列表
     * @param workingDirectory 工作路径
     * @param replacePath 置换路径
     * @param imgLinkList 存储文件链接列表
     * @return
     */
    private static List<String> getImgLinkList(String workingDirectory , String replacePath , List<String> imgLinkList){
        // 1.检查fileClient 是否已初始化
        init();

        // 存储文件地址集合
        if(imgLinkList == null){
            imgLinkList = new ArrayList<String>();
        }
        if (workingDirectory.startsWith("/") && workingDirectory.endsWith("/")) {
            //更换目录到当前目录
            try {
                ftpClient.changeWorkingDirectory(workingDirectory);
                FTPFile[] files = ftpClient.listFiles();
                for (FTPFile file : files) {
                    if (file.isFile()) {
                        imgLinkList.add(workingDirectory.replace( replacePath , url ) + file.getName());
                    } else if (file.isDirectory()) { // 如果该 File 为目录，则递归该文件夹
                        // "." 表示当前目录，".." 表示上级目录
                        if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
                            getImgLinkList(workingDirectory + file.getName() + "/" , replacePath , imgLinkList);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return imgLinkList;
    }

    /**
     * 获取图片链接列表
     * @return
     */
    public static List<String> getImgLinkList(){
        try {
            ftpClient.makeDirectory(workingDir);// 如目录不存在则创建
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getImgLinkList(workingDir , replacePath , null);
    }

    /**
     * 删除文件
     * @param replacePath 置换地址
     * @param imgLink 图片链接地址
     * @return
     */
    private static boolean delImgLink(String replacePath ,String imgLink){
        // 1.检查fileClient 是否已初始化
        init();

        // 存储文件地址集合
        if(imgLink == null){
            return false;
        }
        // 置换一下路径
        imgLink = imgLink.replace(url , replacePath);
        // 得到“/”最后出现的位置
        int lastindex = imgLink.replace(url , replacePath).lastIndexOf("/");
        // 得到路径
        String workingDirectory = imgLink.substring(0 ,lastindex + 1);
        if (workingDirectory.startsWith("/") && workingDirectory.endsWith("/")) {
            //更换目录到当前目录
            try {
                ftpClient.changeWorkingDirectory(workingDirectory);
                return ftpClient.deleteFile(imgLink.substring(lastindex + 1));// 删除文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除文件
     * @param imgLink 图片链接地址
     * @return
     */
    public static boolean delImgLink(String imgLink){
        return delImgLink(replacePath , imgLink);
    }

}
