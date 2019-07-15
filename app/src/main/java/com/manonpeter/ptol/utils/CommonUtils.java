package com.manonpeter.ptol.utils;

import java.util.UUID;

public class CommonUtils {

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
