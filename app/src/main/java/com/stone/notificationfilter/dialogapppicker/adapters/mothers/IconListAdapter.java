package com.stone.notificationfilter.dialogapppicker.adapters.mothers;

/*
 * Copyright (C) 2013 Peter Gregus for GravityBox Project (C3C076@xda)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.dialogapppicker.IIconListAdapterItem;
import com.stone.notificationfilter.dialogapppicker.objects.AppItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private List<AppItem> mData = null;
    private List<AppItem> mCopyData = null;
    private List<AppItem> mFilteredData = null;
    private Set<String> selectedPackages;
    private android.widget.Filter mFilter;



    static  class  ViewHolder extends RecyclerView.ViewHolder{
        public ImageView icon;
        public TextView label;
        public TextView packageName;
        public RelativeLayout relativeLayout;
        public MaterialCheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.iconlistItem);
              icon = itemView.findViewById(R.id.icon);
              label= itemView.findViewById(R.id.label);
            packageName= itemView.findViewById(R.id.packageName);
             checkBox = itemView.findViewById(R.id.checkbox);

        }
    }
    @SuppressLint("ResourceType")
    public IconListAdapter(Context context, Set<String> select) {
        super();
        selectedPackages = select;
//        mContext = context;
//        mData = new ArrayList<AppItem>(objects);
//        mFilteredData = new ArrayList<AppItem>(objects);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_package,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final AppItem current = mData.get(position);
        Drawable drawable =current.getIconLeft();
        holder.icon.setImageDrawable(current.getIconLeft());
        holder.label.setText(current.getAppName());
        holder.packageName.setText(current.getPackageName());
        holder.checkBox.setChecked(selectedPackages.contains(current.getPackageName()));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPackages.contains(current.getPackageName())) {
                    holder.checkBox.setChecked(false);
                    selectedPackages.remove(current.getPackageName());
                } else {
                    selectedPackages.add(current.getPackageName());
                    holder.checkBox.setChecked(true);
                }
            }
        }); {

//            notifyItemChanged(filteredCache.indexOf(current))
        }

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setAppItems(List<AppItem> data) {
        //复制数据
        mData = new ArrayList<>();
        this.mData.addAll(data);
        this.mCopyData = data;
        this.notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //初始化过滤结果对象
                FilterResults results = new FilterResults();
                //假如搜索为空的时候，将复制的数据添加到原始数据，用于继续过滤操作
                if (results.values == null) {
                    mData.clear();
                    mData.addAll(mCopyData);
                }
                //关键字为空的时候，搜索结果为复制的结果
                if (constraint == null || constraint.length() == 0) {
                    results.values = mCopyData;
                    results.count = mCopyData.size();
                } else {
                    String prefixString = constraint.toString();
                    final int count = mData.size();
                    //用于存放暂时的过滤结果
                    final ArrayList<AppItem> newValues = new ArrayList<AppItem>();
                    for (int i = 0; i < count; i++) {
                        final AppItem value = mData.get(i);
                        String appName = value.getAppName().toLowerCase();

                        // First match against the whole ,non-splitted value，假如含有关键字的时候，添加
                        if (appName.contains(prefixString.toLowerCase())) {
                            newValues.add(value);
                        } else {
                            //过来空字符开头
                            final String[] words = appName.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].contains(prefixString)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;//过滤结果
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mData.clear();//清除原始数据
                mData.addAll((List<AppItem>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    setAppItems(mCopyData);
                }
            }
        };
    }
}