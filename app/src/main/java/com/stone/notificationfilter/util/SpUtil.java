package com.stone.notificationfilter.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
            allStr4=sb1.append(strsTrue[i]+SEPARATE).toString();
        }
        return allStr4;
    }

    public static SharedPreferences getSp(Context context,String tag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
        return sharedPreferences;
    }


    private SpUtil() {
        throw new AssertionError();
    }

    /**
     * put string preferences
     *
     * @param context   context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putString(Context context, String preferenceName, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * get string preferences
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws ClassCastException if there is a preference with this
     *         name that is not a string
     * @see #getString(Context, String, String)
     */
    public static String getString(Context context, String preferenceName, String key) {
        return getString(context, preferenceName, key, null);
    }

    /**
     * get string preferences
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a string
     */
    public static String getString(Context context, String preferenceName, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     * put int preferences
     *
     * @param context  context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putInt(Context context, String preferenceName, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * get int preferences
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     *         name that is not a int
     * @see #getInt(Context,String, String, int)
     */
    public static int getInt(Context context,String preferenceName, String key) {
        return getInt(context, preferenceName, key, -1);
    }

    /**
     * get int preferences
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a int
     */
    public static int getInt(Context context, String preferenceName,String key, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    /**
     * put long preferences
     *
     * @param context  context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putLong(Context context,String preferenceName, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * get long preferences
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     *         name that is not a long
     * @see #getLong(Context,String, String, long)
     */
    public static long getLong(Context context,String preferenceName, String key) {
        return getLong(context,preferenceName, key,-1);
    }

    /**
     * get long preferences
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a long
     */
    public static long getLong(Context context, String preferenceName,String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    /**
     * put float preferences
     *
     * @param context  context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putFloat(Context context, String preferenceName,String key, float value) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * get float preferences
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     *         name that is not a float
     * @see #getFloat(Context,String, String, float)
     */
    public static float getFloat(Context context,String preferenceName, String key) {
        return getFloat(context, preferenceName, key, -1);
    }

    /**
     * get float preferences
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a float
     */
    public static float getFloat(Context context, String preferenceName,String key, float defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * put boolean preferences
     *
     * @param context  context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putBoolean(Context context, String preferenceName,String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * get boolean preferences, default is false
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws ClassCastException if there is a preference with this
     *         name that is not a boolean
     */
    public static boolean getBoolean(Context context, String preferenceName,String key) {
        return getBoolean(context, preferenceName,key, false);
    }

    /**
     * get boolean preferences
     *
     * @param context  context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a boolean
     */
    public static boolean getBoolean(Context context,String preferenceName, String key, boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }
}
