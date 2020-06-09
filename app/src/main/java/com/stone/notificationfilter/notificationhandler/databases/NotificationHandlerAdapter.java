package com.stone.notificationfilter.notificationhandler.databases;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseDraggableModule;
import com.chad.library.adapter.base.module.DraggableModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.notificationlog.objects.NotificationLogItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//import com.github.Cy

//public class NotificationLogSwipeAdapter {
//}
public class NotificationHandlerAdapter extends BaseQuickAdapter<NotificationHandlerItem, BaseViewHolder> {

//    List<NotificationLogItem> data =null;

//    static class ChildViewHolder extends BaseViewHolder {
////        TextView itemName;
//        public ChildViewHolder(@NonNull View itemView) {
//            super(itemView);
////            itemName = (TextView)itemView.findViewById(android.R.id.text1);
//        }
//    }

    public NotificationHandlerAdapter(ArrayList<NotificationHandlerItem> data) {
        super(R.layout.notification_handler_list_item, data);
////        this.data =data;
//        BaseDraggableModule baseDraggableModule =  getDraggableModule();
////        baseDraggableModule.setDragEnabled(true);
//        baseDraggableModule.setSwipeEnabled(true);

    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, NotificationHandlerItem notificationLogItem) {
        baseViewHolder.setText(R.id.notifi_handler_name, notificationLogItem.name);
//        baseViewHolder.setText(R.id.log_title, notificationLogItem.mTitle);
//        baseViewHolder.setText(R.id.log_content, notificationLogItem.mContent);
//        baseViewHolder.setText(R.id.log_post_time, notificationLogItem.mPostTime);
//        ImageView imageView = (ImageView)baseViewHolder.getView(R.id.log_image_icon);
//        imageView.setImageDrawable(notificationLogItem.getAppIcon());
    }

}