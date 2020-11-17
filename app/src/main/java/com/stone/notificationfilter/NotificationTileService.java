package com.stone.notificationfilter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.stone.notificationfilter.util.NotificationCollectorMonitorService;

@SuppressLint("Registered")
@RequiresApi(api = Build.VERSION_CODES.N)
public class NotificationTileService extends TileService {
    private static final String TAG = "NotificationTileService";

    @Override
    public void onClick() {
        Log.d("TAG", "onClick()");
        //获取自定义的Tile.
        Tile tile = getQsTile();
        if (tile == null) {
            return;
        }
        Log.d(TAG, "Tile state: " + tile.getState());
        switch (tile.getState()) {
            case Tile.STATE_ACTIVE:
                //当前状态是开，设置状态为关闭.
                tile.setState(Tile.STATE_INACTIVE);
                //更新快速设置面板上的图块的颜色，状态为关.
                tile.updateTile();
                NotificationService.offListener();
                Toast.makeText(getApplicationContext(), R.string.service_stop, Toast.LENGTH_SHORT).show();

                break;
            case Tile.STATE_UNAVAILABLE:
                break;
            case Tile.STATE_INACTIVE:
                //当前状态是关，设置状态为开.
                tile.setState(Tile.STATE_ACTIVE);
                //更新快速设置面板上的图块的颜色，状态为开.
                tile.updateTile();
                startService(new Intent(getApplicationContext(), NotificationCollectorMonitorService.class));
                NotificationService.onListener();
                Toast.makeText(getApplicationContext(), R.string.service_start, Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
    }

}
