package com.stone.notificationfilter.util;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import java.util.Set;

public class MMKVAdpter extends PreferenceDataStore {
    @Override
    public void putString(String key, @Nullable String value) {
        super.putString(key, value);
    }
    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return super.getString(key, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return super.getStringSet(key, defValues);
    }
    @Override
    public void putStringSet(String key, @Nullable Set<String> values) {
        super.putStringSet(key, values);
    }
    @Override
    public void putBoolean(String key, boolean value) {
        super.putBoolean(key, value);
    }

    @Override
    public int getInt(String key, int defValue) {
        return super.getInt(key, defValue);
    }

    @Override
    public void putInt(String key, int value) {
        super.putInt(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return super.getBoolean(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return super.getFloat(key, defValue);
    }

    @Override
    public void putFloat(String key, float value) {
        super.putFloat(key, value);
    }

}
