package com.stone.notificationfilter.actioner.floatnotification;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.util.PackageUtil;

import java.lang.ref.WeakReference;
import java.util.Timer;

public class FloatNotificationItem {

    private WeakReference<Context> contextWeakReference =null;
    private static final String TAG ="FloatNotificationItem";
    private OnViewChangeListener mOnViewChangeListener=null;
    private View view;
    private Timer mtimer =null;
    private int viewID = 0;
    private int viewWidth;
    private int viewHeight;
    private boolean isShow =false;
    private boolean isOpen = true;
    private PendingIntent intent =null;
    private boolean mIsLeft = true;
    private NotificationInfo notificationInfo =null;
    public FloatNotificationItem(Context context, NotificationInfo notificationInfo) {
        this.contextWeakReference = new WeakReference<>(context);
        final LinearLayout messageLay = view.findViewById(R.id.window_messgae_lay);
        ImageView imageView = view.findViewById(R.id.window_icon_img);
        if (this.notificationInfo.largeIcon ==null){
            imageView.setImageDrawable(PackageUtil.getAppIconFromPackname(context, this.notificationInfo.packageName));
        } else {
            imageView.setImageIcon(this.notificationInfo.largeIcon);
        }

        final TextView titleText = view.findViewById(R.id.window_title_text);
        final TextView contentText = view.findViewById(R.id.window_content_text);
        titleText.setText(this.notificationInfo.title);
        contentText.setText(this.notificationInfo.content);

        intent = this.notificationInfo.intent;
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        viewWidth = view.getMeasuredWidth();
        viewHeight = view.getMeasuredHeight();
    }

    public void showReadly( boolean isLeft){
        this.mIsLeft =isLeft;
        if (this.mIsLeft){
//            view = View.inflate(this.contextWeakReference, R.layout.window_lay_left, null);
            mIsLeft =true;

        }else{
//            view = View.inflate(context, R.layout.window_lay_right, null);
            mIsLeft =false;
        }
        setOnTouchListenr();
    }

    public void setOnViewChangeListener(FloatNotificationItem.OnViewChangeListener l){
        this.mOnViewChangeListener = l;
    }
    public interface OnViewChangeListener{
        public void onUpdateView(int viewID);
        public void onRemove(int viewID);
    }

    private void setOnTouchListenr() {
//        final View.OnTouchListener editPosFloatingOnTouchListener = new View.OnTouchListener() {
//            private int x;
//            private int y;
//            int viewx =0;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                Log.e(TAG,"layoutParams"+String.valueOf(layoutParams.x)+","+String.valueOf(layoutParams.y));
////                Log.e(TAG,"raw:"+String.valueOf(x)+","+String.valueOf(y));
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        x = (int) event.getRawX();
//                        if(isLeft) {
//                            viewx = x;
//                        }else {
//                            viewx = mScreenWidth-x;
//                        }
//                        y = (int) event.getRawY();
//                        Log.e(TAG,"viewx:"+String.valueOf(viewx));
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//
//                        int nowX = (int) event.getRawX();
//                        int nowY = (int) event.getRawY();
//                        Log.e(TAG,"viewwidth:"+viewWidth);
//                        int movedX;
//                        if(isLeft){
//                            if(nowX-viewx>0){
//                                movedX = nowX-viewx;
//                            }else
//                            {
//                                movedX =0;
////                                if(isLeft) {
////                                    viewx = x;
////                                }else {
////                                    viewx = mScreenWidth-x;
////                                }
//                            }
//                        }else {
//                            if((mScreenWidth-nowX-viewx)>0){
//                                movedX = mScreenWidth-nowX-viewx;
//                            }else
//                            {
//                                movedX =0;
////                                if(isLeft) {
////                                viewx = x;
////                                }else {
////                                    viewx = mScreenWidth-x;
////                                }
//                            }
//                        }
//                        Log.e(TAG,"viewx:"+String.valueOf(movedX));
//                        int movedY = nowY - y;
//                        x = nowX;
//                        y = nowY;
//                        layoutParams.x = movedX;
//                        layoutParams.y = layoutParams.y + movedY;
//                        // 更新悬浮窗控件布局
//                        windowManager.updateViewLayout(v, layoutParams);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_x", x).apply();
//                        SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_y", layoutParams.y).apply();
//                        removeTile();
////                        mtimer.cancel();
//                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        };
        final GestureDetector gestureDetector= new GestureDetector(this.view.getContext(), new GestureDetector.OnGestureListener(){
            protected static final float FLIP_DISTANCE = 50;

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }


            @Override
            public void onShowPress(MotionEvent e) {
                return;
            }


            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (isOpen){
                    try {
                        if(intent !=null){
                            intent.send();
                            removeTile();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }else {
                    showNoificationInfo();
                }
                Log.i(TAG, "点击...");
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                return;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i(TAG, velocityX+","+velocityY);
                if (e1.getX() - e2.getX() > FLIP_DISTANCE) {
                    if(!mIsLeft){
                        showNoificationInfo();
                        mOnViewChangeListener.onUpdateView(viewID);
                    }else {
                        closeNoificationInfo();
                    }

                    Log.i(TAG, "向左滑...");
                    return true;
                }

                if (e1.getY() - e2.getY() > 40) {
                    if(isOpen){
                        removeTile();
                    }
                    Log.i(TAG, "向下滑...");
                    return true;
                }
                if (e2.getY() - e1.getY() > 40) {
                    if(isOpen){
                        removeTile();
                    }
                    Log.i(TAG, "向上滑...");
                    return true;
                }
                if (e2.getX() - e1.getX() > 50) {
                    if(mIsLeft){
                        showNoificationInfo();
                    }else {
                        closeNoificationInfo();
                    }
                    return true;
                }


                Log.d("TAG", e2.getX() + " " + e2.getY());

                return false;
            }
        });
//        if (isEditPos) {
//            view.setOnTouchListener(editPosFloatingOnTouchListener);
//        } else {
        view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
        });

    }



    private void closeNoificationInfo() {
        view.findViewById(R.id.window_messgae_lay).setVisibility(View.GONE);
        isOpen =false;
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        viewWidth = view.getMeasuredWidth();
        viewHeight = view.getMeasuredHeight();
//        mOnViewChangeListener.onUpdateView();
        Log.i(TAG, "向右滑...");
        if(mtimer != null)
        {
            mtimer.cancel();
        }
    }

    private void showNoificationInfo() {
        view.findViewById(R.id.window_messgae_lay).setVisibility(View.VISIBLE);
        isOpen = true;
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        viewWidth = view.getMeasuredWidth();
        viewHeight = view.getMeasuredHeight();

        //        mOnViewChangeListener.onUpdateView();
//        windowManager.updateViewLayout(view, layoutParams);
    }
    public int getViewHeight(){
        return viewHeight;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    /**
     * 设置位置
     */


    public void removeTile() {
        // It's time to show some Floating Tile that waiting for showing.
//        if (isRemove) {
//            return;
//        }
//        TileObject.showTileNum--;
//        isRemove = true;
//        TileObject.showingFloatingTileList.remove(this);
//        TileObject.positionArray.put(layoutParams.y, false);
//        showWaitingTile();
//        removeView();

        mOnViewChangeListener.onRemove(viewID);
    }
}
