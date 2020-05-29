package com.lingc.notificationfilter.dialogapppicker;

import android.content.Intent;

/**
 * Created by Paul on 12/07/15.
 */
public interface ShortcutHandler {
    Intent getCreateShortcutIntent();
    void onShortcutCancelled();
}