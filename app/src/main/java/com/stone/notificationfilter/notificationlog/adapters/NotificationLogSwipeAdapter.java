package com.stone.notificationfilter.notificationlog.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseDraggableModule;
import com.chad.library.adapter.base.module.DraggableModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.notificationlog.objects.NotificationLogItem;
//import com.github.Cy
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//public class NotificationLogSwipeAdapter {
//}
public class NotificationLogSwipeAdapter extends BaseQuickAdapter<NotificationLogItem, BaseViewHolder> implements DraggableModule {

//    List<NotificationLogItem> data =null;

//    static class ChildViewHolder extends BaseViewHolder {
////        TextView itemName;
//        public ChildViewHolder(@NonNull View itemView) {
//            super(itemView);
////            itemName = (TextView)itemView.findViewById(android.R.id.text1);
//        }
//    }

    public NotificationLogSwipeAdapter(ArrayList<NotificationLogItem> data) {
        super(R.layout.list_child_item, data);
////        this.data =data;
        BaseDraggableModule baseDraggableModule =  getDraggableModule();
//        baseDraggableModule.setDragEnabled(true);
        baseDraggableModule.setSwipeEnabled(true);

    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, NotificationLogItem notificationLogItem) {
        baseViewHolder.setText(R.id.log_app_name, notificationLogItem.mAppName);
        baseViewHolder.setText(R.id.log_title, notificationLogItem.mTitle);
        baseViewHolder.setText(R.id.log_content, notificationLogItem.mContent);
        baseViewHolder.setText(R.id.log_post_time, notificationLogItem.mPostTime);
        ImageView imageView = (ImageView)baseViewHolder.getView(R.id.log_image_icon);
        imageView.setImageDrawable(notificationLogItem.getAppIcon());
    }

}