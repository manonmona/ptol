package com.manonpeter.ptol;

import com.manonpeter.ptol.ftp.FTPUtils;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testFTPLogin(){
        FTPUtils.getFTPClient();
    }

    @Test
    public void testProperties(){
        /*InputStream in = this.getClass().getClassLoader().getResourceAsStream("com/manonpeter/ptol/cfg/ptol.properties");
        Properties p = new Properties();
        try {
            p.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
//        System.out.println("success");
//        InputStream in = this.getClass().getClassLoader().getResourceAsStream("assets/ptol.properties");
        InputStream in = this.getClass().getResourceAsStream("assets/ptol.properties");
        Properties p = new Properties();
        try {
            p.load(in);
            System.out.println("??");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testProperties2(){
        InputStream in = this.getClass().getResourceAsStream("/assets/ptol.properties");
        Properties p = new Properties();
        try {
            p.load(in);
            System.out.println("??");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}