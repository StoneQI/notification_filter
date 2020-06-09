package com.stone.notificationfilter.notificationhandler;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.stone.notificationfilter.BaseActivity;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.dialogapppicker.DialogAppPicker;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItemFileStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class AddFiliterActivity extends BaseActivity {
    private final static  String TAG ="AddFiliterActivity";
    private  long notificationHandlerItemID;
    private NotificationHandlerItemFileStorage notificationHandlerItemFileStorage =null;
    private NotificationHandlerItem notificationFiliter =null;
    private HashSet<String> packageNames = null;
    private boolean isUpdate = false;
    private AddNotificationPatterView addNotificationPatterView;

    private static NotificationHandlerItem notificationHandlerItem;

    public static HashMap<Integer,NotificationHandlerItem.NotificationPatterItem> notificationPatterItemHashMap;

    private static boolean isOnNotificationReplace=false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                new AlertDialog.Builder(AddFiliterActivity.this)
                        .setTitle("提醒")
                        .setMessage("操作成功")
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("继续操作", null)
                        .show();
            }
            Log.e(TAG,"------------> msg.what = " + msg.what);
        }
    };

    @Override
    protected void onResume() {
        if (notificationHandlerItemFileStorage ==null){
            try {
                notificationHandlerItemFileStorage = new NotificationHandlerItemFileStorage(getApplicationContext(),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_filiter);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("规则操作");
        if (notificationHandlerItemFileStorage ==null){
            try {
                notificationHandlerItemFileStorage = new NotificationHandlerItemFileStorage(getApplicationContext(),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final EditText filter_id =(EditText)findViewById(R.id.filter_ID);
        final EditText filter_name =(EditText)findViewById(R.id.filter_name);
        final EditText filter_tiitle_extra =(EditText)findViewById(R.id.filter_tiitle_extra);
        final EditText filter_title_replace =(EditText)findViewById(R.id.filter_title_replace);
        final EditText filter_content_extra =(EditText)findViewById(R.id.filter_content_extra);
        final EditText filter_content_replace =(EditText)findViewById(R.id.filter_content_replace);
        final Button update_notification = (Button)findViewById(R.id.update_notification);
        final Button  detele_notification = (Button)findViewById(R.id.delete_notification);

        final LinearLayout notification_patter_view = (LinearLayout)findViewById(R.id.notification_patter_view);

        final Switch isBreak =(Switch) findViewById(R.id.isBreak);
        final Spinner actioner =(Spinner)findViewById(R.id.actioner);
        final Spinner sceen_status_on =(Spinner)findViewById(R.id.sceen_status_on);

        Intent intent = getIntent();
        notificationHandlerItemID = intent.getLongExtra("notificationHandlerID",-1);

        if(notificationHandlerItemID != -1){
            notificationFiliter = notificationHandlerItemFileStorage.get(String.valueOf(notificationHandlerItemID));
            isUpdate =true;
            filter_id.setText(String.valueOf(notificationFiliter.orderID));
            filter_name.setText(notificationFiliter.name);
            addNotificationPatterView = new AddNotificationPatterView(getApplicationContext(),notification_patter_view,notificationFiliter.notificationPatterItems);
            filter_tiitle_extra.setText(notificationFiliter.titleFiliter);
            filter_title_replace.setText(notificationFiliter.titleFiliterReplace);
            filter_content_extra.setText(notificationFiliter.contextFiliter);
            filter_content_replace.setText(notificationFiliter.contextFiliterReplace);
            packageNames  = (notificationFiliter.packageNames);
            isBreak.setChecked(notificationFiliter.breakDown);
            actioner.setSelection(notificationFiliter.actioner);
            sceen_status_on.setSelection(notificationFiliter.sceen_status_on);

        }else {
            update_notification.setText("添加");
            notificationFiliter = new NotificationHandlerItem();
            notificationFiliter.ID = System.currentTimeMillis();
            notificationHandlerItemID = notificationFiliter.ID;
            packageNames = new HashSet<String>();
            addNotificationPatterView = new AddNotificationPatterView(getApplicationContext(),notification_patter_view,notificationFiliter.notificationPatterItems);

//            notificationFiliter.orderID = NotificationFilterManager.getInstance().getNextID(AddFiliterActivity.this);
//            filter_id.setText(String.valueOf(notificationFiliter.orderID));
        }


        update_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filter_id.getText().toString().length() ==0 || filter_name.getText().toString().length()==0){
                    new AlertDialog.Builder(AddFiliterActivity.this)
                            .setTitle("提醒")
                            .setMessage("规则ID和规则名称不能为空")
                            .setPositiveButton("关闭", null)
                            .show();
                    return;
                }
                v.setEnabled(false);

                notificationFiliter.orderID = Integer.parseInt(filter_id.getText().toString());
                notificationFiliter.name = filter_name.getText().toString();
                notificationFiliter.notificationPatterItems = addNotificationPatterView.getNotificationPatterItems();
                notificationFiliter.titleFiliter =filter_tiitle_extra.getText().toString();
                notificationFiliter.titleFiliterReplace =filter_title_replace.getText().toString();
                notificationFiliter.contextFiliter =filter_content_extra.getText().toString();
                notificationFiliter.contextFiliterReplace =filter_content_replace.getText().toString();
                notificationFiliter.breakDown = isBreak.isChecked();
                notificationFiliter.actioner = actioner.getSelectedItemPosition();
                notificationFiliter.sceen_status_on = sceen_status_on.getSelectedItemPosition();
                if (packageNames.size() !=0){
                    notificationFiliter.packageNames =  packageNames;
                }
                addOrUpdateNotificationFilter();

                v.setEnabled(true);


            }
        });

        detele_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotificationFilter();

            }
        });


        findViewById(R.id.appPicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"appPicker Clicked");
                DialogAppPicker mDialog = new DialogAppPicker(AddFiliterActivity.this,packageNames);
                mDialog.getDialog()
                        .create()
                        .show();
                // Don't forget this also
//                @Override
//                public void onActivityResult(int requestCode, int resultCode, Intent data) {
////                    super.onActivityResult(requestCode, resultCode, data);
//                    mDialog.onActivityResult(requestCode, resultCode, data);
//                }
            }
        });
        Log.e(TAG,"start");


        findViewById(R.id.notification_replace_input_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnNotificationReplace){
                    isOnNotificationReplace = false;
                    findViewById(R.id.notification_replace_input).setVisibility(View.GONE);
                }
                else {
                    isOnNotificationReplace =true;
                    findViewById(R.id.notification_replace_input).setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void deleteNotificationFilter() {
        if(!isUpdate) {
            new AlertDialog.Builder(AddFiliterActivity.this)
                    .setTitle("提醒")
                    .setMessage("添加状态下不能删除")
                    .setPositiveButton("关闭", null)
                    .show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    notificationHandlerItemFileStorage.remove(String.valueOf(notificationHandlerItemID));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                notificationFilterDao.deleteOne(notificationFiliter);
//                Toast.makeText(AddFiliterActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                finish();
            }
            }).start();

    }

    private void addOrUpdateNotificationFilter() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    notificationHandlerItemFileStorage.store(String.valueOf(notificationHandlerItemID),notificationFiliter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                notificationFilterDao.insertOne(notificationFiliter);
//                Toast.makeText(AddFiliterActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessage( 1);
//                finish();
            }
        }).start();

    }

}
