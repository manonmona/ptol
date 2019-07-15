package com.manonpeter.ptol.utils;

import java.util.Properties;

public class PropUtils {
    private static Properties prop;

    /**
     * 初始化
     * @param prop
     */
    public static void init(Properties prop){
        PropUtils.prop = prop;
    }

    /**
     * 获取
     * @return
     */
    public static Properties getProp(){
        return prop;
    }
}
