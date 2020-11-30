package com.stone.notificationfilter.dialogapppicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.dialogapppicker.adapters.mothers.IconListAdapter;
import com.stone.notificationfilter.dialogapppicker.objects.AppItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Paul on 12/07/15.
 */
public class DialogAppPicker {

    MaterialAlertDialogBuilder builder=null;
    Dialog mIconPickerDialog;
    View mPrincipalView;
    RecyclerView mListView;
    EditText mSearch;
    ProgressBar mProgressBar;
    private int mMode = 0;
    public static final int MODE_APP = 0;
    public static final int MODE_SHORTCUT = 1;
    private Context c;
    private boolean mNullItemEnabled = true;
    private Set<String> mDefaultPackageNames=null;
    private Resources mResources;
    private static IconListAdapter sIconPickerAdapter;
    Spinner mSpinner;

    public DialogAppPicker(Context c, Set<String> defaultPackageNames){
        this.c = c;
        this.mDefaultPackageNames = defaultPackageNames;
        builder = new MaterialAlertDialogBuilder(this.c);
        init(c);
    }

    private void init(Context c) {
        this.c = c;
        mResources = ((Activity) c).getResources();
        mPrincipalView = ((Activity) c).getLayoutInflater().inflate(R.layout.app_picker_preference, null);
        mListView = (RecyclerView) mPrincipalView.findViewById(R.id.icon_list);
        mSearch = (EditText) mPrincipalView.findViewById(R.id.input_search);
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) { }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) { }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3)
            {
                sIconPickerAdapter.getFilter().filter(arg0);
////                if (arg0.length() > 0) {
////                    searchClear.setVisibility(View.VISIBLE);
////                } else {
////                    searchClear.setVisibility(View.INVISIBLE);
////                }
//                if(mListView.getAdapter() == null)
//                    return;
//
//                ((IconListAdapter)mListView.getAdapter()).getFilter().filter(arg0);
            }
        });
        mProgressBar = (ProgressBar) mPrincipalView.findViewById(R.id.progress_bar);
        setData();
//        mSpinner = (Spinner) mPrincipalView.findViewById(R.id.mode_spinner);
//
//        ArrayAdapter<String> mModeSpinnerAdapter = new ArrayAdapter<String>(
//                c, android.R.layout.simple_spinner_item,
//                new ArrayList<String>(Arrays.asList(
//                        "Applications",
//                        "Raccourcis")));
//        mModeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(mModeSpinnerAdapter);
//        mSpinner.setOnItemSelectedListener(this);
//        mMode = mSpinner.getSelectedItemPosition();
    }



    public MaterialAlertDialogBuilder getDialog() {
        init(c);
        builder = new MaterialAlertDialogBuilder(c)
                .setTitle("选择需匹配规则的应用");
        builder.setView(mPrincipalView);
        setData();
//        builder.setPositiveButton("确定", null);
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        mIconPickerDialog = builder.create();

        return builder;
//        mIconPickerDialog.show();
    }

    public void dismiss(){
       mIconPickerDialog.dismiss();
    }

    private void setData() {
        AsyncTask mAsyncTask = new AsyncTask<Void, Void, ArrayList<AppItem>>() {
            PackageManager mPackageManager;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mListView.setVisibility(View.INVISIBLE);
                mSearch.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                mPackageManager = ((Activity) c).getPackageManager();
            }

            @Override
            protected ArrayList<AppItem> doInBackground(Void... arg0) {
                ArrayList<AppItem> itemList = new ArrayList<AppItem>();
                List<ResolveInfo> appList = new ArrayList<ResolveInfo>();

                List<PackageInfo> packages = mPackageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES|PackageManager.GET_SERVICES);
                Intent mainIntent = new Intent();
                if (mMode == MODE_APP) {
                    mainIntent.setAction(Intent.ACTION_MAIN);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                } else if (mMode == MODE_SHORTCUT) {
                    mainIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
                }
                for (PackageInfo pi : packages) {
                    if (this.isCancelled()) break;
                    mainIntent.setPackage(pi.packageName);
                    List<ResolveInfo> activityList = mPackageManager.queryIntentActivities(mainIntent, 0);
                    for (ResolveInfo ri : activityList) {
                        appList.add(ri);
                    }
                }

                Collections.sort(appList, new ResolveInfo.DisplayNameComparator(mPackageManager));
//                if (mNullItemEnabled) {
//                    itemList.add(mMode == MODE_SHORTCUT ?
//                            new ShortcutItem(c, "Shortcuts", null) :
//                            new AppItem(c, "所有应用", null));
//                }
                for (ResolveInfo ri : appList) {
                    if (this.isCancelled()) break;
                    String appName = ri.loadLabel(mPackageManager).toString();
                    AppItem ai = new AppItem(c, appName, ri);
                    itemList.add(ai);
                }

                return itemList;
            }

            @Override
            protected void onPostExecute(final ArrayList<AppItem> result) {
                mProgressBar.setVisibility(View.GONE);
                mSearch.setVisibility(View.VISIBLE);
                sIconPickerAdapter = new IconListAdapter(c,mDefaultPackageNames);
                sIconPickerAdapter.setAppItems(result);
                LinearLayoutManager layoutManager = new LinearLayoutManager(c);
                mListView.setLayoutManager(layoutManager);
                mListView.setAdapter(sIconPickerAdapter);

                mListView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }
}
