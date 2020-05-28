package com.lingc.notificationfilter;

import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lingc.notificationfilter.entitys.notificationfilter.NotificationFilterDao;
import com.lingc.notificationfilter.entitys.notificationfilter.NotificationFilterDataBase;
import com.lingc.notificationfilter.entitys.notificationfilter.NotificationFilterEntity;

import java.util.ArrayList;
import java.util.List;

public class FiliterActivity extends AppCompatActivity {
    private final static  String TAG ="FiliterActivity";
    private NotificationFilterDao notificationFilterDao =null;
//    private String filiter_path = "";
    private List<NotificationFilterEntity> notificationMatcherArrayList = null;


    @SuppressLint("HandlerLeak")
    private Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (notificationMatcherArrayList.isEmpty()){
                findViewById(R.id.no_filiter).setVisibility(View.VISIBLE);
                pd.dismiss();
                return;
            }else {
                findViewById(R.id.no_filiter).setVisibility(View.GONE);
            }
            ArrayList<String> data = new ArrayList<String>();
            for (NotificationFilterEntity notificationMatcher: notificationMatcherArrayList) {
                data.add(notificationMatcher.name);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(FiliterActivity.this,android.R.layout.simple_list_item_1,data);
            ListView listView = (ListView)findViewById(R.id.filiters_list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(FiliterActivity.this,AddFiliterActivity.class);
                    intent.putExtra("notification", notificationMatcherArrayList.get(position));
                    startActivity(intent);

                }
            });
            pd.dismiss();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        filiter_path = SpUtil.getSp(this,"APPSETTING").getString("filiter_path", "filit.json");

        Log.e(TAG,"create");
        setContentView(R.layout.activity_filiter);

        findViewById(R.id.new_filiter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"goto AddFiliterActivity");
                Intent intent = new Intent(FiliterActivity.this,AddFiliterActivity.class);
                intent.putExtra("ID",-1);
                startActivity(intent);
                return;
            }
        });
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>();
    }

    @Override
    protected void onResume() {
        Log.e(TAG,"get Data");
        if(notificationFilterDao==null){
            NotificationFilterDataBase db =NotificationFilterDataBase.getInstance(getApplicationContext());
            notificationFilterDao = db.NotificationFilterDao();
        }
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
                //在新线程里执行长耗时方法
                notificationMatcherArrayList = notificationFilterDao.loadAll();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

}
