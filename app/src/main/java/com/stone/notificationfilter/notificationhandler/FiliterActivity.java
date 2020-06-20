package com.stone.notificationfilter.notificationhandler;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.stone.notificationfilter.BaseActivity;
import com.stone.notificationfilter.notificationhandler.AddFiliterActivity;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterDao;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterDataBase;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterEntity;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemDataBase;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemEntity;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerAdapter;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItemFileStorage;
import com.stone.notificationfilter.notificationlog.adapters.NotificationLogSwipeAdapter;
import com.stone.notificationfilter.notificationlog.objects.NotificationLogItem;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;

public class FiliterActivity extends BaseActivity {
    private final static  String TAG ="FiliterActivity";
//    private NotificationFilterDao notificationFilterDao =null;
//    private String filiter_path = "";
    private ArrayList<NotificationHandlerItem> notificationMatcherArrayList = new ArrayList<>();
    private NotificationHandlerAdapter notificationHandlerAdapter=null;

    private static NotificationHandlerItemFileStorage notificationHandlerItemFileStorage;
//    private static ArrayAdapter<String> adapter = null;
    private RecyclerView mRecyclerView;
//    private View view =null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filiter_fragment);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("所有规则");

        Log.e(TAG,"create");

        mRecyclerView = findViewById(R.id.filiters_list);

        notificationHandlerAdapter = new NotificationHandlerAdapter(notificationMatcherArrayList);
        notificationHandlerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(FiliterActivity.this,AddFiliterActivity.class);
                intent.putExtra("notificationHandlerID", notificationMatcherArrayList.get(position).ID);
                startActivity(intent);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(notificationHandlerAdapter);

        findViewById(R.id.new_filiter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"goto AddFiliterActivity");
                Intent intent = new Intent(FiliterActivity.this,AddFiliterActivity.class);
                startActivity(intent);
                return;
            }
        });
    }
//    @Override
//    public View onCreate(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.filiter_fragment,container,false);
//
//
//        return view;
//    }

    @SuppressLint("HandlerLeak")
    private Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){

            super.handleMessage(msg);

            if(notificationMatcherArrayList ==null || notificationMatcherArrayList.size()==0){
                findViewById(R.id.no_filiter).setVisibility(View.VISIBLE);
            }else
            {
                findViewById(R.id.no_filiter).setVisibility(View.GONE);
            }
            notificationHandlerAdapter.notifyDataSetChanged();
            pd.dismiss();
        }
    };

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        filiter_path = SpUtil.getSp(this,"APPSETTING").getString("filiter_path", "filit.json");
//
////        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>();
//    }

    @Override
    public void onResume() {
        Log.e(TAG,"get Data");

        processThread();
        super.onResume();
    }

    //声明变量
    private Button b1;
    private ProgressDialog pd;

    private void processThread(){
        //构建一个下载进度条
        pd= ProgressDialog.show(FiliterActivity.this, "Load", "Loading…");
        new Thread(){
            public void run(){

                try {
                    notificationHandlerItemFileStorage = new NotificationHandlerItemFileStorage(getApplicationContext(),true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ArrayList<NotificationHandlerItem> notificationHandlerItems= notificationHandlerItemFileStorage.getAllAsArrayList();

                notificationMatcherArrayList.clear();
                if (notificationHandlerItems.size() !=0){
                    notificationMatcherArrayList.addAll(notificationHandlerItems);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

}
