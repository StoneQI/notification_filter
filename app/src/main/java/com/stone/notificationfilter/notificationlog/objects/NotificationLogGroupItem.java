package com.stone.notificationfilter.notificationlog.objects;

import java.util.List;

public class NotificationLogGroupItem {
    public long id;
    public List<NotificationLogItem> children;
    public String groupName;

    public NotificationLogGroupItem(long id, List<NotificationLogItem> children, String groupName) {
        this.id = id;
        this.children = children;
        this.groupName = groupName;
    }

    public long getGroupId() {
        return id;
    }
}
