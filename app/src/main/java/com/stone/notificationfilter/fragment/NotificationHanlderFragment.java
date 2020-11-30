package com.stone.notificationfilter.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerAdapter;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
//import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItemFileStorage;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerMMKVAdapter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationHanlderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationHanlderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private final static  String TAG ="FiliterActivity";
    //    private NotificationFilterDao notificationFilterDao =null;
//    private String filiter_path = "";
    private final ArrayList<NotificationHandlerItem> notificationMatcherArrayList = new ArrayList<>();
    private NotificationHandlerAdapter notificationHandlerAdapter=null;

    public NotificationHanlderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationHanlderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationHanlderFragment newInstance(String param1, String param2) {
        NotificationHanlderFragment fragment = new NotificationHanlderFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification_hanlder, container, false);;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        Bundle bundle = new Bundle();
//        bundle.putString("amount", amount);
//        view.findViewById(R.id.filiters_list).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_notificationHanlderFragment_to_addNotificationHandlerFragment));

        //    private static ArrayAdapter<String> adapter = null;
        RecyclerView mRecyclerView = view.findViewById(R.id.filiters_list);

        notificationHandlerAdapter = new NotificationHandlerAdapter(notificationMatcherArrayList);
        notificationHandlerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putLong("NotificationHandlerID", notificationMatcherArrayList.get(position).ID);
                Navigation.findNavController(view).navigate(R.id.action_notificationHanlderFragment_to_addNotificationHandlerFragment,bundle);


//                Intent intent = new Intent(getActivity(), AddFiliterActivity.class);
//                intent.putExtra("notificationHandlerID", notificationMatcherArrayList.get(position).ID);
//                startActivity(intent);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(notificationHandlerAdapter);

        Bundle bundle = new Bundle();
        bundle.putLong("NotificationHandlerID", -1);

        view.findViewById(R.id.new_filiter).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_notificationHanlderFragment_to_addNotificationHandlerFragment,bundle));
    }

    @SuppressLint("HandlerLeak")
    private Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){

            super.handleMessage(msg);

            if(notificationMatcherArrayList ==null || notificationMatcherArrayList.size()==0){
                view.findViewById(R.id.no_filiter).setVisibility(View.VISIBLE);
            }else
            {
                view.findViewById(R.id.no_filiter).setVisibility(View.GONE);
            }
            notificationHandlerAdapter.notifyDataSetChanged();
            pd.dismiss();
        }
    };

    @Override
    public void onResume() {
        Log.e(TAG,"get Data");

        processThread();
        super.onResume();
    }

    //声明变量
    private Button b1;
    private ProgressDialog pd;

    private void processThread(){
        //构建一个下载进度条
        pd= ProgressDialog.show(getActivity(), "加载", "加载中…");
        new Thread(() -> {


            ArrayList<NotificationHandlerItem> notificationHandlerItems= new NotificationHandlerMMKVAdapter(getContext(),true).getAllAsArrayList();

            notificationMatcherArrayList.clear();
            if (notificationHandlerItems.size() !=0){
                notificationMatcherArrayList.addAll(notificationHandlerItems);
            }
            handler.sendEmptyMessage(0);
        }).start();
    }
}