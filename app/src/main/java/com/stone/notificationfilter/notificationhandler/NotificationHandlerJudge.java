package com.stone.notificationfilter.notificationhandler;

import android.text.TextUtils;

import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class NotificationHandlerJudge {
    private static NotificationInfo notificationInfo;
    private static NotificationHandlerItem notificationHandlerItem;

    public NotificationHandlerJudge(NotificationInfo notificationInfo, NotificationHandlerItem notificationHandlerItem) {
        this.notificationInfo = notificationInfo;
        this.notificationHandlerItem = notificationHandlerItem;
    }

    public  boolean isMatch(){
        if (!isPackageMatch()){
            return false;
        }
        if(!isSceenOriMatch()){
            return false;
        }
        if (!isPatterItemMatch()){
            return false;
        }

        return true;
    }

    private  boolean isSceenOriMatch(){
        if (notificationHandlerItem.sceen_status_on ==0 ){
            return true;
        }
        return notificationHandlerItem.sceen_status_on == notificationInfo.sceenStatus;

    }
    private  boolean isPackageMatch(){
        if (notificationHandlerItem.packageNames ==null || notificationHandlerItem.packageNames.size()==0){
            return true;
        }
        return notificationHandlerItem.packageNames.contains(notificationInfo.packageName);
    }

    private  boolean isPatterItemMatch(){
        if (notificationHandlerItem.notificationPatterItems ==null || notificationHandlerItem.notificationPatterItems.size()==0){
            return true;
        }
        boolean isHasOneMatch = false;
        for (NotificationHandlerItem.NotificationPatterItem notificationPatterItem : notificationHandlerItem.notificationPatterItems){
            boolean isMatchItem = false;
            String desContent = notificationInfo.getAttribute(notificationPatterItem.PatternItem);

            if (TextUtils.isEmpty(notificationPatterItem.PatternRule)){
                return true;
            }
            if(TextUtils.isEmpty(desContent)){
                    return false;
            }

            switch (notificationPatterItem.PatternMode){
                case "contain": isMatchItem = isContainMatchItem(notificationPatterItem.PatternRule,desContent);break;
                case "nocontain": isMatchItem = isNoContainMatchItem(notificationPatterItem.PatternRule,desContent);break;
                case "regex": isMatchItem = isRegexMatchItem(notificationPatterItem.PatternRule,desContent);break;
            }
            if (notificationPatterItem.isRequire && !isMatchItem){
                return false;
            }
            if (isMatchItem){
                isHasOneMatch = true;
            }
        }
        return isHasOneMatch;
    }

    private  boolean isNoContainMatchItem(String patternRule, String desContent) {
        return !desContent.contains(patternRule);
    }

    private  boolean isRegexMatchItem(String patternRule, String desContent) {
        return Pattern.matches(patternRule,desContent);
    }

    private  boolean isContainMatchItem(String patternRule, String desContent) {
        return desContent.contains(patternRule);
    }

    public   String titleReplace(String content) {
        if (TextUtils.isEmpty(content)) return content;
        String new_title = content;
        if (!TextUtils.isEmpty(notificationHandlerItem.titleFiliter)) {
            if (!TextUtils.isEmpty(notificationHandlerItem.titleFiliterReplace)) {
                try {
                    new_title = new_title.replaceAll(notificationHandlerItem.titleFiliter, notificationHandlerItem.titleFiliterReplace);
                } catch (PatternSyntaxException e) {
                    new_title = "";
                    e.printStackTrace();
                }
            } else {
                Pattern p = Pattern.compile(notificationHandlerItem.titleFiliter);
                Matcher m = p.matcher(notificationInfo.getTitle());
                while (m.find()) {
//                        Log.e(TAG,m.group());
                    new_title = new_title + m.group();
                }
            }
        }
        return new_title;

    }
    public  String contentReplace(String content){
        if (TextUtils.isEmpty(content)) return content;
        String new_Content = content;
        if(!TextUtils.isEmpty(notificationHandlerItem.contextFiliter)){
            if (!TextUtils.isEmpty(notificationHandlerItem.contextFiliterReplace )){
                try {
                    new_Content.replaceAll(notificationHandlerItem.contextFiliter, notificationHandlerItem.contextFiliterReplace);
                }catch (PatternSyntaxException e){
                    new_Content ="";
                    e.printStackTrace();
                }
            }else {
                Pattern p = Pattern.compile(notificationHandlerItem.contextFiliter);
                Matcher m = p.matcher(notificationInfo.getContent());
                while (m.find()) {
//                        Log.e(TAG, m.group());
                    new_Content = new_Content + m.group();
                }
            }
        }
        return  new_Content;
    }
}
