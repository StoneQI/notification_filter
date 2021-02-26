package com.stone.notificationfilter.notificationhandler;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationRuleItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AddNotificationRulePatterView {

    public static HashMap<Integer, NotificationRuleItem.NotificationPatterItem> notificationPatterItemHashMap;

    public static HashMap<Integer,View> viewHashMap;
    private static Context context;
    private static LinearLayout linearLayout;

    public AddNotificationRulePatterView(Context context, LinearLayout linearLayout, HashSet<NotificationRuleItem.NotificationPatterItem> notificationPatterItems) {
        AddNotificationRulePatterView.context = context;
        AddNotificationRulePatterView.linearLayout =linearLayout;
        notificationPatterItemHashMap =new HashMap<>();
        viewHashMap = new HashMap<>();


        if (notificationPatterItems == null || notificationPatterItems.size()==0){
            addPatterView();
        }else{
            for (NotificationRuleItem.NotificationPatterItem notificationPatterItem:notificationPatterItems)
                addPatterView(notificationPatterItem);
        }

    }

    private void addPatterView(){
        NotificationRuleItem.NotificationPatterItem notificationPatterItem  =new NotificationRuleItem.NotificationPatterItem();
        int hascode = notificationPatterItem.hashCode();
        notificationPatterItemHashMap.put(hascode,notificationPatterItem);
        NotificationPatterView notificationPatterView = new NotificationPatterView(context,hascode);
        linearLayout.addView(viewHashMap.get(hascode));
    }

    public HashSet<NotificationRuleItem.NotificationPatterItem> getNotificationPatterItems(){
        return new HashSet(notificationPatterItemHashMap.values());
    }
    private void addPatterView(NotificationRuleItem.NotificationPatterItem notificationPatterItem){
        int hascode = notificationPatterItem.hashCode();
        notificationPatterItemHashMap.put(hascode,notificationPatterItem);
        NotificationPatterView notificationPatterView = new NotificationPatterView(context,hascode);
        linearLayout.addView(viewHashMap.get(hascode));
    }
    private static void removePatterView(int viewID){
        notificationPatterItemHashMap.remove(viewID);
        linearLayout.removeView(viewHashMap.get(viewID));
        viewHashMap.remove(viewID);
    }

    class NotificationPatterView{
        private int viewID;
        private String patterMode;

        NotificationPatterView(Context context, int viewID) {
            this.viewID = viewID;
            initView(context);
        }

        void initView(final Context context){
            View view = View.inflate(context, R.layout.notification_handler_patter_item, null);
            viewHashMap.put(viewID,view);

            Spinner patterItemView = (Spinner) view.findViewById(R.id.patter_item);
            if (!TextUtils.isEmpty(notificationPatterItemHashMap.get(viewID).PatternItem)){
                List<String> strings = Arrays.asList(context.getResources().getStringArray(R.array.notification_patter_item_value));
                patterItemView.setSelection(strings.indexOf(notificationPatterItemHashMap.get(viewID).PatternItem),true);
            }
            patterItemView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    notificationPatterItemHashMap.get(viewID).PatternItem = context.getResources().getStringArray(R.array.notification_patter_item_value)[position];

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    notificationPatterItemHashMap.get(viewID).PatternItem  = context.getResources().getStringArray(R.array.notification_patter_item_value)[0];
                }

            });

            Spinner patterModeView = (Spinner) view.findViewById(R.id.patter_mode);

            if (!TextUtils.isEmpty(notificationPatterItemHashMap.get(viewID).PatternMode)){
                List<String> strings = Arrays.asList(context.getResources().getStringArray(R.array.notification_patter_mode_value));
                patterModeView.setSelection(strings.indexOf(notificationPatterItemHashMap.get(viewID).PatternMode),true);
            }
            patterModeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    notificationPatterItemHashMap.get(viewID).PatternMode = context.getResources().getStringArray(R.array.notification_patter_mode_value)[position];

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    notificationPatterItemHashMap.get(viewID).PatternMode = context.getResources().getStringArray(R.array.notification_patter_mode_value)[0];
                }

            });

            final TextInputEditText patterContentView = (TextInputEditText)view.findViewById(R.id.patter_content);
            if (!TextUtils.isEmpty(notificationPatterItemHashMap.get(viewID).PatternRule)){
                patterContentView.setText(notificationPatterItemHashMap.get(viewID).PatternRule);
            }

            patterContentView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    notificationPatterItemHashMap.get(viewID).PatternRule = patterContentView.getText().toString();
                }
            });

            final SwitchMaterial isRequireView = (SwitchMaterial) view.findViewById(R.id.is_require);

            isRequireView.setChecked(notificationPatterItemHashMap.get(viewID).isRequire);

            isRequireView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        notificationPatterItemHashMap.get(viewID).isRequire =true;
                    }else {
                        notificationPatterItemHashMap.get(viewID).isRequire = false;
                    }
                }

            });

            MaterialButton removeMaterialButton = (MaterialButton)view.findViewById(R.id.patter_detele);
            removeMaterialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePatterView(viewID);
                }
            });
            MaterialButton addMaterialButton = (MaterialButton)view.findViewById(R.id.patter_add_next);
            addMaterialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPatterView();
                }
            });
        }

    }
}
