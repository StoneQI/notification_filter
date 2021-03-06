package com.stone.notificationfilter.actioner;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by LingC on 2019/8/6 16:06
 */
public class TileObject {
 private final  static  String TAG = "TileObject";
    public static int showTileNum = 0;
    public static  int mMostShowTitleNum =0;

    public static FloatingTileActioner lastFloatingTile;

    public static List<FloatingTileActioner> showingFloatingTileList = new ArrayList<>();
    public static int currentPosition=0;
    private static boolean isClearMultiNOfici = false;
    // key: Tile的y, value: Tile是否在显示
    // 数量一般为指定数量
    public static List<Boolean> positionArray = new ArrayList<>();

    public static List<FloatingTileActioner> waitingForShowingTileList = new ArrayList<>();



    public static synchronized int getNextPosition() {
        for (int i = 0; i <positionArray.size() ; i++) {
            if(!positionArray.get(i)){
                positionArray.set(i,true);
                showTileNum++;
                return i;
            }
//            if(currentPosition==mMostShowTitleNum || showTileNum ==0){
//                currentPosition=0;
//            }
//            if(!positionArray.get(currentPosition)){
//                positionArray.set(currentPosition,true);
//                showTileNum++;
////                currentPosition++;
//                return currentPosition;
//            }
//            currentPosition++;


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
        Log.e(TAG,String.valueOf(showID));
        positionArray.set(showID,false);
        showingFloatingTileList.remove(floatingTileActioner);
        showTileNum--;
        if (!isClearMultiNOfici){
            showWaitingTile();
        }
    }

    public static synchronized void clearShowingTile() {

        isClearMultiNOfici =true;
        for (int i = 0; i < showingFloatingTileList.size();) {
            showingFloatingTileList.get(i).removeTile();
        }
        isClearMultiNOfici =false;
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
