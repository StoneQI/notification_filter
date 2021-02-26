package com.stone.notificationfilter.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.dialogapppicker.DialogAppPicker;
import com.stone.notificationfilter.notificationhandler.AddNotificationPatterView;
import com.stone.notificationfilter.notificationhandler.AddNotificationRulePatterView;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerMMKVAdapter;
import com.stone.notificationfilter.notificationhandler.databases.NotificationRuleItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationRuleMMKVAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     UpdateNotificationRuleDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class UpdateNotificationRuleDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private final static  String TAG ="UpdateNotificationRuleDialogFragment";
//    private  long notificationHandlerItemID = -1;
    private NotificationRuleMMKVAdapter notificationRuleMMKVAdapter =null;
    private NotificationRuleItem notificationFiliter =null;
    private HashSet<String> packageNames = null;
    private boolean isUpdate = false;
    private AddNotificationRulePatterView addNotificationPatterView;
    private View view;

    private static NotificationRuleItem notificationHandlerItem;

    public static HashMap<Integer, NotificationRuleItem.NotificationPatterItem> notificationPatterItemHashMap;

    private static boolean isOnNotificationReplace=false;

    public static final String ARG_NOTIFICATION_RULE_NAME = "NotificationRuleName";
    private static String mNotificationRuleName;

    public static final String ARG_NOTIFICATION_RULE_ID = "NotificationRuleID";
    private static  long  mNotificationRuleID = -1;

    // TODO: Customize parameters
    public static UpdateNotificationRuleDialogFragment newInstance(long notificationRuleID,String notificationRuleName ) {
        final UpdateNotificationRuleDialogFragment fragment = new UpdateNotificationRuleDialogFragment();
        final Bundle args = new Bundle();
//        args.putLong(ARG_NOTIFICATION_RULE_ID, notificationRuleID);
//        args.putString(ARG_NOTIFICATION_RULE_NAME, notificationRuleName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        Bundle bundle = getArguments();
        if (bundle != null) {
            mNotificationRuleID = bundle.getLong(ARG_NOTIFICATION_RULE_ID,-1);
            mNotificationRuleName = getArguments().getString(ARG_NOTIFICATION_RULE_NAME);
        }

        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mNotificationRuleID = getArguments().getLong(ARG_NOTIFICATION_RULE_ID,-1);
//            mNotificationRuleName = getArguments().getString(ARG_NOTIFICATION_RULE_NAME);
//        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        if (savedInstanceState!=null){
//            mNotificationRuleID = savedInstanceState.getLong(ARG_NOTIFICATION_RULE_ID,-1);
//            mNotificationRuleName = savedInstanceState.getString(ARG_NOTIFICATION_RULE_NAME,"");
//        }
        view = inflater.inflate(R.layout.fragment_update_notification_rule_dialog_bottom, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

//        super.onViewCreated(view, savedInstanceState);
//        if (savedInstanceState!=null){
//            mNotificationRuleID = savedInstanceState.getLong(ARG_NOTIFICATION_RULE_ID,-1);
//            mNotificationRuleName = savedInstanceState.getString(ARG_NOTIFICATION_RULE_NAME,"");
//        }


        if (notificationRuleMMKVAdapter ==null){
            notificationRuleMMKVAdapter = new NotificationRuleMMKVAdapter(getContext(),mNotificationRuleName,true);
        }
//        final EditText filter_id =(EditText)view.findViewById(R.id.filter_ID);
        final EditText filter_name =(EditText)view.findViewById(R.id.filter_name);
        final EditText filter_tiitle_extra =(EditText)view.findViewById(R.id.filter_tiitle_extra);
        final EditText filter_title_replace =(EditText)view.findViewById(R.id.filter_title_replace);
        final EditText filter_content_extra =(EditText)view.findViewById(R.id.filter_content_extra);
        final EditText filter_content_replace =(EditText)view.findViewById(R.id.filter_content_replace);
        final Button update_notification = (Button)view.findViewById(R.id.update_notification);
        final Button  detele_notification = (Button)view.findViewById(R.id.delete_notification);

        final LinearLayout notification_patter_view = (LinearLayout)view.findViewById(R.id.notification_patter_view);

//        final Switch isBreak = view.findViewById(R.id.isBreak);
//        final Spinner actioner =(Spinner)view.findViewById(R.id.actioner);
        final Spinner sceen_status_on =(Spinner)view.findViewById(R.id.sceen_status_on);


        if(mNotificationRuleID != -1){
            notificationFiliter = notificationRuleMMKVAdapter.get(String.valueOf(mNotificationRuleID));
            isUpdate =true;
//            filter_id.setText(String.valueOf(notificationFiliter.orderID));
            filter_name.setText(notificationFiliter.name);
            addNotificationPatterView = new AddNotificationRulePatterView(getContext(),notification_patter_view,notificationFiliter.notificationPatterItems);
//            filter_tiitle_extra.setText(notificationFiliter.titleFiliter);
//            filter_title_replace.setText(notificationFiliter.titleFiliterReplace);
//            filter_content_extra.setText(notificationFiliter.contextFiliter);
//            filter_content_replace.setText(notificationFiliter.contextFiliterReplace);
            packageNames  = (notificationFiliter.packageNames);
//            isBreak.setChecked(notificationFiliter.breakDown);
//            actioner.setSelection(NotificationHandlerItem.getActionerIndex(notificationFiliter.actioner),true);
            sceen_status_on.setSelection(notificationFiliter.sceen_status_on,true);

        }else {
            update_notification.setText("添加");
            isUpdate = false;
            notificationFiliter = new NotificationRuleItem();
            notificationFiliter.ID = System.currentTimeMillis();
//            mNotificationRuleID = notificationFiliter.ID;
            packageNames = new HashSet<String>();
            addNotificationPatterView = new AddNotificationRulePatterView(getContext(),notification_patter_view,notificationFiliter.notificationPatterItems);

//            notificationFiliter.orderID = NotificationFilterManager.getInstance().getNextID(AddFiliterActivity.this);
//            filter_id.setText(String.valueOf(notificationFiliter.orderID));
        }


        update_notification.setOnClickListener(v -> {

            if (filter_name.getText().toString().length()==0){
                new AlertDialog.Builder(getActivity())
                        .setTitle("提醒")
                        .setMessage("规则ID和规则名称不能为空")
                        .setPositiveButton("关闭", null)
                        .show();
                return;
            }
            v.setEnabled(false);

//                notificationFiliter.orderID = Integer.parseInt(filter_id.getText().toString());
            notificationFiliter.orderID = 0;

            notificationFiliter.name = filter_name.getText().toString();
            notificationFiliter.notificationPatterItems = addNotificationPatterView.getNotificationPatterItems();
            notificationFiliter.titleFiliter =filter_tiitle_extra.getText().toString();
            notificationFiliter.titleFiliterReplace =filter_title_replace.getText().toString();
            notificationFiliter.contextFiliter =filter_content_extra.getText().toString();
            notificationFiliter.contextFiliterReplace =filter_content_replace.getText().toString();
//                notificationFiliter.breakDown = isBreak.isChecked();
//                notificationFiliter.actioner = NotificationHandlerItem.getActionerValue(actioner.getSelectedItemPosition());
            notificationFiliter.sceen_status_on = sceen_status_on.getSelectedItemPosition();
            if (packageNames.size() !=0){
                notificationFiliter.packageNames =  packageNames;
            }
            addOrUpdateNotificationFilter();

            v.setEnabled(true);


        });

        detele_notification.setOnClickListener(v -> deleteNotificationFilter());


        view.findViewById(R.id.appPicker).setOnClickListener(v -> {
            Log.e(TAG,"appPicker Clicked");
            DialogAppPicker mDialog = new DialogAppPicker(getActivity(),packageNames);
            mDialog.getDialog()
                    .create()
                    .show();
            // Don't forget this also
//                @Override
//                public void onActivityResult(int requestCode, int resultCode, Intent data) {
////                    super.onActivityResult(requestCode, resultCode, data);
//                    mDialog.onActivityResult(requestCode, resultCode, data);
//                }
        });
        Log.e(TAG,"start");
        Log.e(TAG, String.valueOf(mNotificationRuleID));
        Log.e(TAG,":"+mNotificationRuleName);


//        view.findViewById(R.id.notification_replace_input_switch).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isOnNotificationReplace){
//                    isOnNotificationReplace = false;
//                    view.findViewById(R.id.notification_replace_input).setVisibility(View.GONE);
//                }
//                else {
//                    isOnNotificationReplace =true;
//                    view.findViewById(R.id.notification_replace_input).setVisibility(View.VISIBLE);
//                }
//
//            }
//        });

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                new AlertDialog.Builder(getActivity())
                        .setTitle("提醒")
                        .setMessage("操作成功")
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                popBackStack();
                            }
                        })
                        .setPositiveButton("继续操作", null)
                        .show();
            }
            Log.e(TAG,"------------> msg.what = " + msg.what);
        }
    };

    private void popBackStack() {
        dismiss();
//        new Handler(Looper.getMainLooper()).post(()->{
//            NavController navController = Navigation.findNavController(view);
////        navController.popBackStack();
////        navController.navigateUp();
//            if (!navController.popBackStack()){
//                getActivity().finish();
//            }
//        });

    }

    @Override
    public void onResume() {
        if (notificationRuleMMKVAdapter ==null){
            notificationRuleMMKVAdapter = new NotificationRuleMMKVAdapter(getContext(),mNotificationRuleName,true);
        }
        super.onResume();
    }

    private void deleteNotificationFilter() {
        if(!isUpdate) {
            new AlertDialog.Builder(getActivity())
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
                    notificationRuleMMKVAdapter.remove(String.valueOf(notificationFiliter.ID));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                notificationFilterDao.deleteOne(notificationFiliter);
//                Toast.makeText(AddFiliterActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                popBackStack();
            }
        }).start();

    }

    private void addOrUpdateNotificationFilter() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    notificationRuleMMKVAdapter.store(String.valueOf(notificationFiliter.ID),notificationFiliter);
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