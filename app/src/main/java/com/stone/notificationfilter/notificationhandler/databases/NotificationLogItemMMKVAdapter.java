package com.stone.notificationfilter.notificationhandler.databases;

import android.content.Context;

public class NotificationLogItemMMKVAdapter extends JsonData{
    public NotificationLogItemMMKVAdapter(Context context, boolean autosave) {
        super(context, "Setting", "NotificationLog", autosave);
    }
}
