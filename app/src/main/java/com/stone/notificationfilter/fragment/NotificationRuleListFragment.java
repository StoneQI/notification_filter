package com.stone.notificationfilter.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stone.notificationfilter.MainActivity;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.dummy.DummyContent;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerMMKVAdapter;
import com.stone.notificationfilter.notificationhandler.databases.NotificationRuleMMKVAdapter;

/**
 * A fragment representing a list of Items.
 */
public class NotificationRuleListFragment extends Fragment {


    private final static  String TAG ="NotificationRuleListFragment";
    public static final String ARG_NOTIFICATION_RULE_NAME = "NotificationRuleName";
    private static String mNotificationRuleName ="";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificationRuleListFragment() {
    }



    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NotificationRuleListFragment newInstance(String notificationRuleName) {
        NotificationRuleListFragment fragment = new NotificationRuleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOTIFICATION_RULE_NAME,notificationRuleName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().setNavigationVisibility(false);
//        MainActivity mainActivity = (MainActivity)getActivity();
//        if(mainActivity!= null){
//            mainActivity.BottomNavigationViewHide(false);
//        }
        if (getArguments() != null) {

            mNotificationRuleName = getArguments().getString(ARG_NOTIFICATION_RULE_NAME);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        super.onCreateView(inflater,savedInstanceState);
        if (savedInstanceState!=null){
            mNotificationRuleName = savedInstanceState.getString(ARG_NOTIFICATION_RULE_NAME);

        }
//        mNotificationRuleName = getArguments().getString(ARG_NOTIFICATION_RULE_NAME);
        View view = inflater.inflate(R.layout.fragment_notification_rule_item_list, container, false);
//        Log.i(ARG_NOTIFICATION_RULE_NAME,":"+mNotificationRuleName);
        // Set the adapter
        RecyclerView recyclerView = view.findViewById(R.id.notification_rule_list);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.new_filiter_rule);

        floatingActionButton.setOnClickListener(v -> {
            navigateRuleShow(-1,mNotificationRuleName);
        });
//            Navigation.createNavigateOnClickListener(R.id.action_notificationHanlderFragment_to_addNotificationHandlerFragment,bundle);
        Log.e(ARG_NOTIFICATION_RULE_NAME,mNotificationRuleName);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new NotificationRuleListRecyclerViewAdapter(new NotificationRuleMMKVAdapter(getContext(),mNotificationRuleName,true).getAllAsArrayList(),mNotificationRuleName));


            //        notificationHandlerAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                Bundle bundle = new Bundle();
//                bundle.putLong("NotificationHandlerID", notificationMatcherArrayList.get(position).ID);
//                Navigation.findNavController(view).navigate(R.id.action_notificationHanlderFragment_to_addNotificationHandlerFragment,bundle);
//
//
////                Intent intent = new Intent(getActivity(), AddFiliterActivity.class);
////                intent.putExtra("notificationHandlerID", notificationMatcherArrayList.get(position).ID);
////                startActivity(intent);
//            }
//        });
        return view;
    }

    private void navigateRuleShow(long notificationRuleID,String value) {
        Bundle bundle = new Bundle();
        bundle.putString(UpdateNotificationRuleDialogFragment.ARG_NOTIFICATION_RULE_NAME, value);
        bundle.putLong(UpdateNotificationRuleDialogFragment.ARG_NOTIFICATION_RULE_ID, notificationRuleID);
//        UpdateNotificationRuleDialogFragment.newInstance(notificationRuleID,value);
//            Navigation.createNavigateOnClickListener(R.id.action_notificationHanlderFragment_to_addNotificationHandlerFragment,bundle);
        Navigation.findNavController(getView()).navigate(R.id.action_notificationRuleListFragment_to_updateNotificationRuleDialogFragment,bundle);
    }
}