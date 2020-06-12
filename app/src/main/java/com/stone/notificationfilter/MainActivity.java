package com.stone.notificationfilter;

import android.content.DialogInterface;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItemFileStorage;
import com.stone.notificationfilter.util.SpUtil;
//import com.stone.notificationfilter.util.filestore.FileStorage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 1.0.190807
 * Hello World
 *
 * 1.1.190808
 * 悬浮磁贴图标增加圆角
 * 优化删除全部磁贴功能
 * 解决显示空白磁贴的问题
 * 优化其他内容
 *
 * 1.2.190813
 * 加入了 输入法防挡 功能
 */
public class MainActivity extends BaseActivity {
    public final String TAG ="MainActivity";
    private Toolbar mToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolBar);
        //设置标题
        getSupportActionBar().setTitle(R.string.app_name);


//        NotificationHandlerItem notificationHandlerItem = new NotificationHandlerItem();
//        try {
//            NotificationHandlerItemFileStorage fileStorage = new NotificationHandlerItemFileStorage(getApplicationContext(),"notificationHandles.json",true);
//            notificationHandlerItem.orderID=2;
//            notificationHandlerItem.name="weqwe";
//            notificationHandlerItem.notificationPatterItems.add(new NotificationHandlerItem.NotificationPatterItem("123","contain","sada",true));
//            notificationHandlerItem.notificationPatterItems.add(new NotificationHandlerItem.NotificationPatterItem("123","contain","sada",true));
//            fileStorage.store(String.valueOf(System.currentTimeMillis()), notificationHandlerItem);
//            ArrayList<NotificationHandlerItem> notificationHandlerItems =fileStorage.getAllAsArrayList();
//            Log.i(TAG,String.valueOf(fileStorage.getSize()));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        boolean isFirstBoot = SpUtil.getBoolean(this,"appSettings","isFirstBoot", true);
        if (isFirstBoot) {
//            NotificationHandlerItemFileStorage notificationHandlerItemFileStorage = new NotificationHandlerItemFileStorage(getApplicationContext())
            new AlertDialog.Builder(this)
                    .setTitle("欢迎使用 " + getString(R.string.app_name))
                    .setMessage("在使用之前，您需要了解一些内容：" +
                            "\n本项目的开发的原因是：原作者的弃坑，，" +
                            "\n但恰好项目开源，本人又是程序，加之需要该应用" +
                            "\n所以三天入门安卓，再连肝四天完成本应用初版" +
                            "\n由于刚入门安卓开发，应用或许仍有BUG" +
                            "\n所做的改进：" +
                            "\n    几乎重写整个项目架构，修复BUG" +
                            "\n    强大的正则规则匹配系统，无限的自定义" +
                            "\n 悬浮通知提醒时，默认展开状态，上滑下滑关闭，五秒不操作也会关闭" +
                            "\n 右划后仅显示图标，不会自动关闭，可左划和点击恢复展开状态" +
                            "\n 展开状态下，点击悬浮通知，触发通知栏点击事件" +
                            "\n\n应用启动需要通知使用权和悬浮窗权限，带有感叹号的是未赋予的权限，您必须赋予后才能正常使用" +
                            "\n在修改应用配置时（如磁贴位置，磁贴方向）应用会清除所有悬浮磁贴（包括显示中和待显示）")
                    .setCancelable(false)
                    .setPositiveButton("了解", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SpUtil.putBoolean(MainActivity.this,"appSettings","isFirstBoot", false);
                        }
                    })
                    .show();
        }
        replaceFragment(new MainFragment());

//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fl, new MyFragment()).commit();



//        startService(new Intent(this, NotificationCollectorMonitorService.class));
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment,fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

//    private void initToolbar(showHomeAsUp: Boolean) {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
//        if (toolbar != null) {
//            setSupportActionBar(toolbar)
//            tvTitle = toolbar.findViewById(R.id.tv_toolbar_title)
//        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
//        supportActionBar?.setDisplayShowHomeEnabled(showHomeAsUp)
//        if (tvTitle != null) {
//            supportActionBar?.setDisplayShowTitleEnabled(false)
//        }
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    public boolean showMenu(View anchor) {
//        PopupMenu popup = new PopupMenu(this, anchor);
//        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
//        popup.show();
//        return true;
//    }

}
