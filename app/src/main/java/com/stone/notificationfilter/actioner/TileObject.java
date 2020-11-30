package com.stone.notificationfilter.actioner;

import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by LingC on 2019/8/6 16:06
 */
public class TileObject {
    private final  static  String TAG = "TileObject";
    public static int showTileNum = 0;
    public static  int mMostShowTitleNum =0;

//    public static NotificationInfo lastFloatingTileKey;

    public static List<FloatingTileActioner> showingFloatingTileList = new ArrayList<>();
    private static boolean isClearMultiNotification = false;
    // key: Tile的y, value: Tile是否在显示
    // 数量一般为指定数量
    public static List<Boolean> positionArray = new ArrayList<>();

    public static List<FloatingTileActioner> waitingForShowingTileList = new ArrayList<>();

//    public static  synchronized NotificationInfo getLastNOtificationKey(){
//            return lastFloatingTileKey;
//    }

    public static synchronized void addShowFloatingTile(FloatingTileActioner floatingTileActioner){
        showingFloatingTileList.add(floatingTileActioner);
//        lastFloatingTileKey = floatingTileActioner.
    }
    public static synchronized void addWaitFloatingTile(FloatingTileActioner floatingTileActioner){
        waitingForShowingTileList.add(floatingTileActioner);
    }

    public static synchronized int getNextPosition() {
        for (int i = 0; i <positionArray.size() ; i++) {
            if(!positionArray.get(i)){
                positionArray.set(i,true);
                showTileNum++;
                return i;
            }
        }
        return -1;
    }

    public static synchronized void setMostShowTitleNum(int mostShowTitleNum){
        for (;mMostShowTitleNum < mostShowTitleNum; mMostShowTitleNum++){
            positionArray.add(mMostShowTitleNum,false);
        }

    }
    public static synchronized void removeSingleShowingTile(FloatingTileActioner floatingTileActioner){
        int showID = floatingTileActioner.showID;
//        Log.e(TAG,String.valueOf(showID));
        positionArray.set(showID,false);
        showingFloatingTileList.remove(floatingTileActioner);
        showTileNum--;
        if (!isClearMultiNotification){
            showWaitingTile();
        }
    }

    public static synchronized void clearShowingTile() {
        isClearMultiNotification =true;
        while (showingFloatingTileList != null && !showingFloatingTileList.isEmpty()){
            showingFloatingTileList.get(0).removeTile();
        }
        isClearMultiNotification =false;
        showWaitingTile();
    }

    public static synchronized void showWaitingTile() {
        while (!TileObject.waitingForShowingTileList.isEmpty()) {
            if (showTileNum == mMostShowTitleNum)break;
            FloatingTileActioner floatingTile = TileObject.waitingForShowingTileList.get(0);
            floatingTile.addViewToWindow();
            TileObject.waitingForShowingTileList.remove(floatingTile);
        }
    }

    public static synchronized void clearAllTile() {
        if(waitingForShowingTileList !=null){
            waitingForShowingTileList.clear();
        }
        // 待显示列表必须在这之前清除，否则会触发显示机会事件
        clearShowingTile();
    }
    public static void getDisplayInform (){
    }


}
