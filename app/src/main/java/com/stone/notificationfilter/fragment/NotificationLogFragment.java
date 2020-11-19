package com.stone.notificationfilter.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemDao;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemDataBase;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemEntity;
import com.stone.notificationfilter.notificationlog.adapters.NotificationLogSwipeAdapter;
import com.stone.notificationfilter.notificationlog.objects.NotificationLogItem;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationLogFragment extends Fragment {

    private final static  String TAG ="CheckNotificActivity";
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private RecyclerView mRecyclerView;
    private List<NotificationItemEntity> notificationItemEntities;
    private ProgressDialog pd;


    private NotificationItemDao notificationItemDao;

    private NotificationLogSwipeAdapter notificationLogSwipeAdapter;

    private ArrayList<NotificationLogItem> notificationLogItemList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  int INITADAPTER =0;
    private  int DETELEDATA =1;
    private View view;

    @SuppressLint("HandlerLeak")
    private Handler handler =new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (msg.what == INITADAPTER){
                pd.dismiss();
                if(notificationItemEntities ==null || notificationItemEntities.size()==0){
                    view.findViewById(R.id.no_log_data).setVisibility(View.VISIBLE);
                    return;
                }else
                {
                    mRecyclerView.removeAllViews();
                    view.findViewById(R.id.no_log_data).setVisibility(View.GONE);
                }
                notificationLogItemList = new ArrayList<>();
                for (NotificationItemEntity notificationItemEntity: notificationItemEntities) {
                    NotificationLogItem notificationLogItem = new NotificationLogItem();
                    notificationLogItem.id = notificationItemEntity.ID;
                    notificationLogItem.mPackageName = notificationItemEntity.packageName;
                    notificationLogItem.mAppName = notificationItemEntity.appName;
                    notificationLogItem.mContent = notificationItemEntity.content;
                    notificationLogItem.mPostTime = TimeUtil.getStrTime(notificationItemEntity.postTime);
                    notificationLogItem.mTitle = notificationItemEntity.title;
                    Drawable appicon = PackageUtil.getAppIconFromPackname(getContext(),notificationItemEntity.packageName);
                    if (appicon ==null)
                    {
                        appicon = getActivity().getDrawable(R.drawable.ic_launcher_foreground);
                    }
                    notificationLogItem.setAppIcon(appicon);
                    notificationLogItemList.add(notificationLogItem);
                }
                OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
                    private long datele_id = -1;
                    @Override
                    public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                        Log.e(TAG,String.valueOf(pos));
                        if(notificationItemDao ==null){
                            NotificationItemDataBase db =NotificationItemDataBase.getInstance(getContext());
                            notificationItemDao = db.NotificationItemDao();
                        }
                        datele_id = notificationLogItemList.get(pos).id;

                    }
                    @Override
                    public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}
                    @Override
                    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, final int pos) {
                        new Thread(){
                            public void run(){
                                //在新线程里执行长耗时方法
                                if( datele_id != -1)
                                {
                                    notificationItemDao.deleteByID(datele_id);
                                }

                            }
                        }.start();


                    }

                    @Override
                    public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

                    }
                };
                OnItemDragListener onItemDragListener = new OnItemDragListener() {
                    @Override
                    public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos){}
                    @Override
                    public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {}
                    @Override
                    public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {}
                };
                notificationLogSwipeAdapter = new NotificationLogSwipeAdapter(notificationLogItemList);
                notificationLogSwipeAdapter.getDraggableModule().setOnItemSwipeListener(onItemSwipeListener);
                notificationLogSwipeAdapter.getDraggableModule().setOnItemDragListener(onItemDragListener);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(notificationLogSwipeAdapter);
            }

            if (msg.what == DETELEDATA){
                pd.dismiss();
                notificationLogSwipeAdapter.notifyDataSetChanged();
                view.findViewById(R.id.no_log_data).setVisibility(View.VISIBLE);
            }


        }
    };


    public NotificationLogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationLogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationLogFragment newInstance(String param1, String param2) {
        NotificationLogFragment fragment = new NotificationLogFragment();
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
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification_log, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(notificationItemDao ==null){
            NotificationItemDataBase db =NotificationItemDataBase.getInstance(getContext());
            notificationItemDao = db.NotificationItemDao();
        }

        pd= ProgressDialog.show(getActivity(), "加载数据", "Loading…");
        new Thread(){
            public void run(){
                notificationItemEntities = notificationItemDao.loadAllDESC();
                handler.sendEmptyMessage(INITADAPTER);
            }
        }.start();


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.check_log_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity().getMenuInflater().inflate(R.menu.check_log_menu, menu);
//        return true;
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("dayang","选择列表项时执行------------");
        //对菜单项点击内容进行设置
        int id = item.getItemId();
        if (id == R.id.delete_all_notification) {
            if(notificationItemDao ==null){
                NotificationItemDataBase db =NotificationItemDataBase.getInstance(getContext());
                notificationItemDao = db.NotificationItemDao();
            }

            pd= ProgressDialog.show(getActivity(), "删除数据", "Loading…");
            new Thread(){
                public void run(){
                    notificationItemDao.deleteAll();
                    if (notificationItemEntities != null)
                        notificationItemEntities.clear();
                    if( notificationLogItemList != null)
                        notificationLogItemList.clear();
                    handler.sendEmptyMessage(DETELEDATA);
                }
            }.start();
        }
        return super.onOptionsItemSelected(item);
    }
}