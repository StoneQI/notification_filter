package com.stone.notificationfilter.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterEntity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NotificationFilterManager{

    private  final String TAG ="NotifiFilterManager" ;
    private  static ArrayList<NotificationFilterEntity> notificationMatcherList = null;
    private  String filePath = "filter.json";
    private  int nextID = 0;
    private  boolean isRead =false;
//    private static Context context = null;
    private static volatile NotificationFilterManager singleton;
    private NotificationFilterManager(){};
    public static NotificationFilterManager getInstance()
    {
        if (singleton==null)
        {
            synchronized (NotificationFilterManager.class)
            {
                if (singleton==null)
                {
                    singleton=new NotificationFilterManager();
                    singleton.isRead =false;
                }
            }
        }
        return singleton;
    }

    public  ArrayList<NotificationFilterEntity> getData(Context ctext){
        if (ctext != null){
            notificationMatcherList = readNotificationMatcher(ctext);
        }
        if (notificationMatcherList==null){
            notificationMatcherList = new ArrayList<NotificationFilterEntity>();
        }else {
            setNextID();
        }
        return notificationMatcherList;
    }
    public int  getNextID(Context context){
        if(nextID == 0){
            getData(context);
        }
        return nextID+1;
    }
    private void setNextID() {
        if (notificationMatcherList != null){
            Collections.sort(notificationMatcherList, new Comparator<NotificationFilterEntity>() {
                @Override
                public int compare(NotificationFilterEntity o1, NotificationFilterEntity o2) {
                    int diff = o1.orderID -o2.orderID;
                    if(o1.orderID >= o2.orderID){
                        return -1;
                    }
                    return 1;
                }
            });
            if (!notificationMatcherList.isEmpty()){
                nextID = notificationMatcherList.get(notificationMatcherList.size()-1).orderID;
            }
        }
    }

    public boolean addNotificationMatch(NotificationFilterEntity value, Context context){
        if(context == null && value== null){
            return false;
        }
//        if(notificationMatcherList.contains(value)){
//            return false;
//        }
        if(notificationMatcherList.add(value))
        {
            if (!saveNotificationMatcherToFile(context)){
                return false;
            }
        }
        setNextID();
        return  true;
    }

    public  boolean updateNotificationMatch(int key, NotificationFilterEntity value, Context context){

        if(context == null && value==null){
            return false;
        }
        NotificationFilterEntity temp = null;
        try{
            temp = notificationMatcherList.set(key,value);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        if(!saveNotificationMatcherToFile(context)){
            notificationMatcherList.set(key,temp);
            return false;
        }
        setNextID();
        return true;

    }
    public   int getCount(){
        return  notificationMatcherList.size();
    }

    public NotificationFilterEntity getNotificationMatcher(int key, Context context){
        if (notificationMatcherList == null || context!=null){
            getData(context);
        }
        try{
            return notificationMatcherList.get(key);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

//        return  notificationMatcherList.size();
    }

    public   boolean deleteNotificationMatch(int key,Context context){
        if(context == null){
            return false;
        }
        NotificationFilterEntity temp=null;
        try{
            temp = notificationMatcherList.remove(key);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        if(!saveNotificationMatcherToFile(context)){
            notificationMatcherList.add(temp);
            return false;
        };
        setNextID();
        return true;
    }
    private  ArrayList<NotificationFilterEntity> readNotificationMatcher(Context context){
        String content = readFilterFile(context);
        if (content.isEmpty()){
            return null;
        }
        Gson gson = new Gson();
        ArrayList<NotificationFilterEntity> notificationMatchers;
        try {
            notificationMatchers = gson.fromJson(content,new TypeToken<ArrayList<NotificationFilterEntity>>(){}.getType());
        }catch (JsonParseException e){
            e.printStackTrace();
            return null;
        }

        return notificationMatchers;

    }

    private String readFilterFile(Context context) {
        FileInputStream fos = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            fos = context.openFileInput(filePath);
            reader = new BufferedReader((new InputStreamReader(fos)));
            String line = "";
            while ((line = reader.readLine())!= null){
                content.append(line);
            }
            fos.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e) {
            Log.e(TAG,"file");
            try{
                reader.close();

            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return content.toString();
    }

    private   boolean saveNotificationMatcherToFile(Context context){
        if (notificationMatcherList==null && notificationMatcherList.size()==0){
            return false;
        }
        String json = null;
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            json = gson.toJson(notificationMatcherList);
        }catch (JsonIOException e)
        {
            e.printStackTrace();
            return false;
        }
        FileOutputStream outputStream =null;
        try{
            outputStream = context.openFileOutput(filePath,Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
            return false;
        }
        catch (IOException e){
            Log.e(TAG,"file");
            try{
                outputStream.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return  false;
        }
        return true;
    }
}
