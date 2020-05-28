package com.lingc.notificationfilter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.lingc.notificationfilter.entitys.notificationitem.NotificationItemDao;
import com.lingc.notificationfilter.entitys.notificationitem.NotificationItemDataBase;
import com.lingc.notificationfilter.entitys.notificationitem.NotificationItemEntity;
//import com.lingc.nfloatingtile.notificationlog.adapters.NotificationLogAdapter;
import com.lingc.notificationfilter.notificationlog.adapters.NotificationLogSwipeAdapter;
import com.lingc.notificationfilter.notificationlog.objects.NotificationLogItem;
import com.lingc.notificationfilter.util.PackageUtil;
import com.lingc.notificationfilter.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class CheckNotificationLogActivity extends AppCompatActivity {
    private final static  String TAG ="CheckNotificActivity";
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private RecyclerView mRecyclerView;
    private List<NotificationItemEntity> notificationItemEntities;
    private ProgressDialog pd;


    private NotificationItemDao notificationItemDao;

    @SuppressLint("HandlerLeak")
    private Handler handler =new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            if(notificationItemEntities ==null || notificationItemEntities.size()==0){
                return;
            }
            final ArrayList<NotificationLogItem> notificationLogItemList = new ArrayList<>();
            for (NotificationItemEntity notificationItemEntity: notificationItemEntities) {
                NotificationLogItem notificationLogItem = new NotificationLogItem();
                notificationLogItem.id = notificationItemEntity.ID;
                notificationLogItem.mPackageName = notificationItemEntity.packageName;
                notificationLogItem.mAppName = notificationItemEntity.appName;
                notificationLogItem.mContent = notificationItemEntity.content;
                notificationLogItem.mPostTime = TimeUtil.getStrTime(notificationItemEntity.postTime);
                notificationLogItem.mTitle = notificationItemEntity.title;
                Drawable appicon = PackageUtil.getAppIconFromPackname(getApplicationContext(),notificationItemEntity.packageName);
                if (appicon ==null)
                {
                    appicon = getDrawable(R.drawable.ic_launcher_foreground);
                }
                notificationLogItem.setAppIcon(appicon);
                notificationLogItemList.add(notificationLogItem);
            }
            OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
                private long datele_id = -1;
                @Override
                public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                    Log.e(TAG,String.valueOf(pos));
                    if(notificationItemDao ==null){
                        NotificationItemDataBase db =NotificationItemDataBase.getInstance(getApplicationContext());
                        notificationItemDao = db.NotificationItemDao();
                    }
                    datele_id = notificationLogItemList.get(pos).id;

                }
                @Override
                public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}
                @Override
                public void onItemSwiped(RecyclerView.ViewHolder viewHolder, final int pos) {
                    new Thread(){
                        public void run(){
                            //在新线程里执行长耗时方法
                            if( datele_id != -1)
                            {
                                notificationItemDao.deleteByID(datele_id);
                            }

                        }
                    }.start();


                }

                @Override
                public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

                }
            };
            OnItemDragListener onItemDragListener = new OnItemDragListener() {
                @Override
                public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos){}
                @Override
                public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {}
                @Override
                public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {}
            };
            NotificationLogSwipeAdapter notificationLogSwipeAdapter = new NotificationLogSwipeAdapter(notificationLogItemList);
            notificationLogSwipeAdapter.getDraggableModule().setOnItemSwipeListener(onItemSwipeListener);
            notificationLogSwipeAdapter.getDraggableModule().setOnItemDragListener(onItemDragListener);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerView.setAdapter(notificationLogSwipeAdapter);
            pd.dismiss();


            
        }
    };

    @Override
    protected void onResume() {
        if(notificationItemDao ==null){
            NotificationItemDataBase db =NotificationItemDataBase.getInstance(getApplicationContext());
            notificationItemDao = db.NotificationItemDao();
        }

        pd= ProgressDialog.show(CheckNotificationLogActivity.this, "加载数据", "Loading…");
        new Thread(){
            public void run(){
                //在新线程里执行长耗时方法
                notificationItemEntities = notificationItemDao.loadAllDESC();
                handler.sendEmptyMessage(0);
            }
        }.start();

        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_notificarion_log_activity);
        mRecyclerView = findViewById(R.id.recycler_view);

    }
}
