package com.stone.notificationfilter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemDao;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemDataBase;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemEntity;
//import com.lingc.nfloatingtile.notificationlog.adapters.NotificationLogAdapter;
import com.stone.notificationfilter.notificationlog.adapters.NotificationLogSwipeAdapter;
import com.stone.notificationfilter.notificationlog.objects.NotificationLogItem;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class CheckNotificationLogActivity extends BaseActivity {
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
            pd.dismiss();
            if(notificationItemEntities ==null || notificationItemEntities.size()==0){
                findViewById(R.id.no_log_data).setVisibility(View.VISIBLE);
                return;
            }else
            {
                mRecyclerView.removeAllViews();
                findViewById(R.id.no_log_data).setVisibility(View.GONE);
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
            
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(notificationItemDao ==null){
            NotificationItemDataBase db =NotificationItemDataBase.getInstance(getApplicationContext());
            notificationItemDao = db.NotificationItemDao();
        }

        pd= ProgressDialog.show(CheckNotificationLogActivity.this, "加载数据", "Loading…");
        new Thread(){
            public void run(){
                notificationItemEntities = notificationItemDao.loadAllDESC();
                handler.sendEmptyMessage(0);
            }
        }.start();


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_notificarion_log_activity);
        setTitle("查看记录日志");
        mRecyclerView = findViewById(R.id.recycler_view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_log_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("dayang","选择列表项时执行------------");
        //对菜单项点击内容进行设置
        int id = item.getItemId();
        if (id == R.id.delete_all_notification) {
            if(notificationItemDao ==null){
                NotificationItemDataBase db =NotificationItemDataBase.getInstance(getApplicationContext());
                notificationItemDao = db.NotificationItemDao();
            }

            pd= ProgressDialog.show(CheckNotificationLogActivity.this, "删除数据", "Loading…");
            new Thread(){
                public void run(){
                    notificationItemDao.deleteAll();
                    notificationItemEntities = notificationItemDao.loadAllDESC();
                    handler.sendEmptyMessage(0);
                }
            }.start();
        }
        return super.onOptionsItemSelected(item);
    }
}
