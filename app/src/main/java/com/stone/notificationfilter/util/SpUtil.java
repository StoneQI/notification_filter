package com.stone.notificationfilter.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Create by LingC on 2019/8/6 17:21
 */
public class SpUtil {
    private static String SEPARATE =";";
//    public static String setToStr(Set<String> strings){
////        return StringUtils.join(strings.toArray(), ";");
//        return "";
//    }
    public static Set<String> string2Set(String string) {
//        if (StringUtils.isEmpty(string)) {
////            return null;
////        }
        String[] stringlist = string.split(SEPARATE);

        return new HashSet<String>(Arrays.asList(stringlist));
    }

    /**
     * 字符串Set转换为字符串List
     *
     * @param stringSet 字符串Set
     * @return 字符串List
     */
    public static String set2String(Set<String> stringSet) {
//        if (stringSet == null) {
//            return null;
//        }
        String[] strsTrue = stringSet.toArray(new String[stringSet.size()]);

        StringBuffer sb1 = new StringBuffer();
        String allStr4="";
        for(int i=0;i<strsTrue.length;i++){
            allStr4=sb1.append(strsTrue[i]).toString();
        }
        return allStr4;
    }

    public static SharedPreferences getSp(Context context,String tag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

}
