package com.stone.notificationfilter.fragment;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.dummy.DummyContent.DummyItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationRuleItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NotificationRuleListRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRuleListRecyclerViewAdapter.ViewHolder> {

    private final List<NotificationRuleItem> mValues;
    private String mNotificationRuleName ="";

    public NotificationRuleListRecyclerViewAdapter(List<NotificationRuleItem> items,String notificationRuleName) {
        mValues = items;
        this.mNotificationRuleName = notificationRuleName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notification_rule_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText("12");
        holder.mContentView.setText(mValues.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateRuleShow(mValues.get(position).ID,mNotificationRuleName,v);
            }
        });
    }

    private void navigateRuleShow(long notificationRuleID,String value,View v) {
        Bundle bundle = new Bundle();
        bundle.putString(UpdateNotificationRuleDialogFragment.ARG_NOTIFICATION_RULE_NAME, value);
        bundle.putLong(UpdateNotificationRuleDialogFragment.ARG_NOTIFICATION_RULE_ID, notificationRuleID);
//        UpdateNotificationRuleDialogFragment.newInstance(notificationRuleID,value);
//            Navigation.createNavigateOnClickListener(R.id.action_notificationHanlderFragment_to_addNotificationHandlerFragment,bundle);
        Navigation.findNavController(v).navigate(R.id.action_notificationRuleListFragment_to_updateNotificationRuleDialogFragment,bundle);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public NotificationRuleItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}