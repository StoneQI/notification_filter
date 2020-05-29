package com.stone.notificationfilter;

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
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterDao;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterDataBase;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterEntity;
import com.stone.notificationfilter.dialogapppicker.DialogAppPicker;
import com.stone.notificationfilter.dialogapppicker.objects.AppItem;
import com.stone.notificationfilter.dialogapppicker.objects.ShortcutItem;
import com.stone.notificationfilter.util.SpUtil;

import java.util.HashSet;
import java.util.Set;

public class AddFiliterActivity extends AppCompatActivity {
    private final static  String TAG ="AddFiliterActivity";

    private NotificationFilterDao notificationFilterDao =null;
    private NotificationFilterEntity notificationFiliter =null;
    private Set<String> packageNames = null;
    private boolean isUpdate = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                new AlertDialog.Builder(AddFiliterActivity.this)
                        .setTitle("提醒")
                        .setMessage("添加成功")
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("继续添加", null)
                        .show();
            }
            if(msg.what==0){
                new AlertDialog.Builder(AddFiliterActivity.this)
                        .setTitle("提醒")
                        .setMessage("修改成功")
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("再次修改", null)
                        .show();
            }

//            setSystemNotificationMatchers();
//            customNotificationMatchers.addAll(systemNotificationMatchers);
            Log.e(TAG,"------------> msg.what = " + msg.what);
        }
    };

    @Override
    protected void onResume() {
        if(notificationFilterDao ==null){
            NotificationFilterDataBase db =NotificationFilterDataBase.getInstance(getApplicationContext());
            notificationFilterDao = db.NotificationFilterDao();
        }
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_filiter);
        setTitle("规则操作");
        final EditText filter_id =(EditText)findViewById(R.id.filter_ID);
        final EditText filter_name =(EditText)findViewById(R.id.filter_name);
        final EditText filter_tiitle_match =(EditText)findViewById(R.id.filter_tiitle_match);
        final EditText filter_content_match =(EditText)findViewById(R.id.filter_content_match);
        final EditText filter_tiitle_extra =(EditText)findViewById(R.id.filter_tiitle_extra);
        final EditText filter_title_replace =(EditText)findViewById(R.id.filter_title_replace);
        final EditText filter_content_extra =(EditText)findViewById(R.id.filter_content_extra);
        final EditText filter_content_replace =(EditText)findViewById(R.id.filter_content_replace);
        final Button update_notification = (Button)findViewById(R.id.update_notification);
        final Button  detele_notification = (Button)findViewById(R.id.delete_notification);

        final Switch isBreak =(Switch) findViewById(R.id.isBreak);
        final Spinner actioner =(Spinner)findViewById(R.id.actioner);

        Intent intent = getIntent();
        notificationFiliter = intent.getParcelableExtra("notification");
        if(notificationFiliter != null){
            isUpdate =true;
            filter_id.setText(String.valueOf(notificationFiliter.orderID));
            filter_name.setText(notificationFiliter.name);
            filter_tiitle_match.setText(notificationFiliter.titlePattern);
            filter_content_match.setText(notificationFiliter.contextPatter);
            filter_tiitle_extra.setText(notificationFiliter.titleFiliter);
            filter_title_replace.setText(notificationFiliter.titleFiliterReplace);
            filter_content_extra.setText(notificationFiliter.contextFiliter);
            filter_content_replace.setText(notificationFiliter.contextFiliterReplace);
            packageNames  = SpUtil.string2Set(notificationFiliter.packageNames);
            isBreak.setChecked(notificationFiliter.breakDown);
            actioner.setSelection(notificationFiliter.actioner);

        }else {
            update_notification.setText("添加");
            notificationFiliter = new NotificationFilterEntity();
            packageNames = new HashSet<String>();
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
                notificationFiliter.titlePattern =filter_tiitle_match.getText().toString();
                notificationFiliter.contextPatter =filter_content_match.getText().toString();
                notificationFiliter.titleFiliter =filter_tiitle_extra.getText().toString();
                notificationFiliter.titleFiliterReplace =filter_title_replace.getText().toString();
                notificationFiliter.contextFiliter =filter_content_extra.getText().toString();
                notificationFiliter.contextFiliterReplace =filter_content_replace.getText().toString();
                notificationFiliter.breakDown = isBreak.isChecked();
                notificationFiliter.actioner = actioner.getSelectedItemPosition();
                if (packageNames.size() !=0){
                    notificationFiliter.packageNames = SpUtil.set2String(packageNames) ;
                }
                if(isUpdate){
                    updateNotificationFilter();
                }else {
                    addNotificationFilter();
                }
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
                mDialog.setOnItemChooseListener(new DialogAppPicker.OnItemChooseListener() {
                    @Override
                    public void onAppSelected(AppItem item) {
                        if (packageNames.contains(item.getPackageName())){
                            packageNames.remove(item.getPackageName());
                        }else {
                            packageNames.add(item.getPackageName());
                        }
                    }

                    @Override
                    public void onShortcutSelected(ShortcutItem item) {
                        // Do something with the shortcut
                    }
                });
                mDialog.getDialog()
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                packageNames.clear();
                            }
                        })
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
                notificationFilterDao.deleteOne(notificationFiliter);
//                Toast.makeText(AddFiliterActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                finish();
            }
            }).start();

    }

    private void addNotificationFilter() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                notificationFilterDao.insertOne(notificationFiliter);
//                Toast.makeText(AddFiliterActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessage( 1);
//                finish();
            }
        }).start();

    }

    private void updateNotificationFilter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                notificationFilterDao.updateOne(notificationFiliter);
//                Toast.makeText(AddFiliterActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessage( 0);
//                finish();
            }
        }).start();
    }
}
