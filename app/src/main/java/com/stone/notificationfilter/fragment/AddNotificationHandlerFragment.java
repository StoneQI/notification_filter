package com.stone.notificationfilter.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import android.widget.Switch;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.dialogapppicker.DialogAppPicker;
import com.stone.notificationfilter.notificationhandler.AddNotificationPatterView;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItemFileStorage;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerMMKVAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNotificationHandlerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNotificationHandlerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final static  String TAG ="AddNotiHandlerFragment";
    private  long notificationHandlerItemID = -1;
    private NotificationHandlerMMKVAdapter notificationHandlerItemMMKVAdapter =null;
    private NotificationHandlerItem notificationFiliter =null;
    private HashSet<String> packageNames = null;
    private boolean isUpdate = false;
    private AddNotificationPatterView addNotificationPatterView;
    private View view;

    private static NotificationHandlerItem notificationHandlerItem;

    public static HashMap<Integer,NotificationHandlerItem.NotificationPatterItem> notificationPatterItemHashMap;

    private static boolean isOnNotificationReplace=false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddNotificationHandlerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNotificationHandlerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNotificationHandlerFragment newInstance(String param1, String param2) {
        AddNotificationHandlerFragment fragment = new AddNotificationHandlerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_notification_handler, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if (notificationHandlerItemMMKVAdapter ==null){
            notificationHandlerItemMMKVAdapter = new NotificationHandlerMMKVAdapter(getContext(),true);
        }
        final EditText filter_id =(EditText)view.findViewById(R.id.filter_ID);
        final EditText filter_name =(EditText)view.findViewById(R.id.filter_name);
        final EditText filter_tiitle_extra =(EditText)view.findViewById(R.id.filter_tiitle_extra);
        final EditText filter_title_replace =(EditText)view.findViewById(R.id.filter_title_replace);
        final EditText filter_content_extra =(EditText)view.findViewById(R.id.filter_content_extra);
        final EditText filter_content_replace =(EditText)view.findViewById(R.id.filter_content_replace);
        final Button update_notification = (Button)view.findViewById(R.id.update_notification);
        final Button  detele_notification = (Button)view.findViewById(R.id.delete_notification);

        final LinearLayout notification_patter_view = (LinearLayout)view.findViewById(R.id.notification_patter_view);

        final Switch isBreak = view.findViewById(R.id.isBreak);
        final Spinner actioner =(Spinner)view.findViewById(R.id.actioner);
        final Spinner sceen_status_on =(Spinner)view.findViewById(R.id.sceen_status_on);


        notificationHandlerItemID = getArguments().getLong("NotificationHandlerID");

        if(notificationHandlerItemID != -1){
            notificationFiliter = notificationHandlerItemMMKVAdapter.get(String.valueOf(notificationHandlerItemID));
            isUpdate =true;
            filter_id.setText(String.valueOf(notificationFiliter.orderID));
            filter_name.setText(notificationFiliter.name);
            addNotificationPatterView = new AddNotificationPatterView(getContext(),notification_patter_view,notificationFiliter.notificationPatterItems);
            filter_tiitle_extra.setText(notificationFiliter.titleFiliter);
            filter_title_replace.setText(notificationFiliter.titleFiliterReplace);
            filter_content_extra.setText(notificationFiliter.contextFiliter);
            filter_content_replace.setText(notificationFiliter.contextFiliterReplace);
            packageNames  = (notificationFiliter.packageNames);
            isBreak.setChecked(notificationFiliter.breakDown);
            actioner.setSelection(NotificationHandlerItem.getActionerIndex(notificationFiliter.actioner),true);
            sceen_status_on.setSelection(notificationFiliter.sceen_status_on,true);

        }else {
            update_notification.setText("添加");
            notificationFiliter = new NotificationHandlerItem();
            notificationFiliter.ID = System.currentTimeMillis();
            notificationHandlerItemID = notificationFiliter.ID;
            packageNames = new HashSet<String>();
            addNotificationPatterView = new AddNotificationPatterView(getContext(),notification_patter_view,notificationFiliter.notificationPatterItems);

//            notificationFiliter.orderID = NotificationFilterManager.getInstance().getNextID(AddFiliterActivity.this);
//            filter_id.setText(String.valueOf(notificationFiliter.orderID));
        }


        update_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filter_id.getText().toString().length() ==0 || filter_name.getText().toString().length()==0){
                    new AlertDialog.Builder(getActivity())
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
                notificationFiliter.actioner = NotificationHandlerItem.getActionerValue(actioner.getSelectedItemPosition());
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


        view.findViewById(R.id.appPicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        Log.e(TAG,"start");


        view.findViewById(R.id.notification_replace_input_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnNotificationReplace){
                    isOnNotificationReplace = false;
                    view.findViewById(R.id.notification_replace_input).setVisibility(View.GONE);
                }
                else {
                    isOnNotificationReplace =true;
                    view.findViewById(R.id.notification_replace_input).setVisibility(View.VISIBLE);
                }

            }
        });

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
        new Handler(Looper.getMainLooper()).post(()->{
            NavController navController = Navigation.findNavController(view);
//        navController.popBackStack();
//        navController.navigateUp();
            if (!navController.popBackStack()){
                getActivity().finish();
            }
        });

    }

    @Override
    public void onResume() {
        if (notificationHandlerItemMMKVAdapter ==null){
            notificationHandlerItemMMKVAdapter = new NotificationHandlerMMKVAdapter(getContext(),true);
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
                    notificationHandlerItemMMKVAdapter.remove(String.valueOf(notificationHandlerItemID));
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
                    notificationHandlerItemMMKVAdapter.store(String.valueOf(notificationHandlerItemID),notificationFiliter);
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