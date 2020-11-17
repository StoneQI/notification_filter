package com.stone.notificationfilter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.stone.notificationfilter.util.SpUtil;

//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import com.stone.notificationfilter.util.filestore.FileStorage;

//TODO: 小窗口大小，弹幕通知显示不全
//
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
        setContentView(R.layout.activity_main_nav_host);


        NavController navController =  Navigation.findNavController(this,R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();


        mToolBar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolBar);

        NavigationUI.setupWithNavController(mToolBar,navController);
        createNotificationChannel();
        boolean isFirstBoot = SpUtil.getBoolean(this,"appSettings","isFirstBoot", true);
        if (isFirstBoot) {
//            NotificationHandlerItemFileStorage notificationHandlerItemFileStorage = new NotificationHandlerItemFileStorage(getApplicationContext())
            new AlertDialog.Builder(this)
                    .setTitle("欢迎使用 " + getString(R.string.app_name))
                    .setMessage("在使用之前，您需要了解一些内容：" +
                            "\n本项目的开发的原因是：原作者的弃坑，，" +
                            "\n但恰好项目开源，因此本人接手了该项目" +
                            "\n由于刚入门安卓开发，应用或许仍有BUG" +
                            "\n所做的改进：" +
                            "\n    几乎重写整个项目架构，修复BUG" +
                            "\n    强大的正则规则匹配系统，无限的自定义" +
                            "\n 悬浮通知提醒时，默认展开状态，上滑下滑关闭，五秒不操作也会关闭" +
                            "\n 右划后仅显示图标，不会自动关闭，可左划和点击恢复展开状态" +
                            "\n 展开状态下，点击悬浮通知，触发通知栏点击事件" +
                            "\n\n应用启动需要通知使用权和悬浮窗权限，带有感叹号的是未赋予的权限，您必须赋予后才能正常使用" +
                            "\n 在修改应用配置时（如磁贴位置，磁贴方向）应用会清除所有悬浮磁贴（包括显示中和待显示）")
                    .setCancelable(false)
                    .setPositiveButton("了解", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SpUtil.putBoolean(MainActivity.this,"appSettings","isFirstBoot", false);
                        }
                    })
                    .show();
        }

    }

    private void createNotificationChannel() {
        CharSequence name = "通知处理器";
        String description = "防止通知处理器被后台关闭和保存历史通知";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationManager mNotificationManager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel(NotificationService.NOTIFICATION_CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setShowBadge(false);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {

//        Navigation.findNavController(this,R.navigation.nav_graph).navigateUp();
        return super.onSupportNavigateUp();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        //对菜单项点击内容进行设置
//        int id = item.getItemId();
//        if (id == R.id.donate_list) {
//            AlertDialog.Builder m = new AlertDialog.Builder(this)
//                    .setIcon(R.drawable.ic_launcher).setMessage(R.string.donate_list)
//                    .setIcon(R.drawable.ic_launcher);
////                    .setPositiveButton("确认",null);
//            m.show();
//
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
