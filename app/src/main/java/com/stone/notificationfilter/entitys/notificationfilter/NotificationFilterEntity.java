package com.stone.notificationfilter.entitys.notificationfilter;

//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("order_id")})
public class NotificationFilterEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int ID;
//    @Index()

    @Ignore
    protected NotificationFilterEntity(Parcel in) {
        ID = in.readInt();
        orderID = in.readInt();
        name = in.readString();
        titlePattern = in.readString();
        contextPatter = in.readString();
        packageNames = in.readString();
        titleFiliter = in.readString();
        titleFiliterReplace = in.readString();
        contextFiliter = in.readString();
        contextFiliterReplace = in.readString();
        byte tmpBreakDown = in.readByte();
        breakDown = tmpBreakDown == 0 ? null : tmpBreakDown == 1;
        actioner = in.readInt();
    }

    public static final Creator<NotificationFilterEntity> CREATOR = new Creator<NotificationFilterEntity>() {
        @Override
        public NotificationFilterEntity createFromParcel(Parcel in) {
            return new NotificationFilterEntity(in);
        }

        @Override
        public NotificationFilterEntity[] newArray(int size) {
            return new NotificationFilterEntity[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @ColumnInfo(name = "order_id")
    public  int orderID;
    public  String name="";
    public String titlePattern="";
    public String contextPatter="";
    public String packageNames ="";
    public String titleFiliter="";
    public String titleFiliterReplace="";
    public String contextFiliter="";
    public String contextFiliterReplace="";
    public Boolean breakDown =false;
    public int actioner = 0;

    public NotificationFilterEntity() {
    }

    @Ignore
    public NotificationFilterEntity(int uid) {
        this.orderID = uid;
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }
    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(orderID);
        dest.writeString(name);
        dest.writeString(titlePattern);
        dest.writeString(contextPatter);
        dest.writeString(packageNames);
        dest.writeString(titleFiliter);
        dest.writeString(titleFiliterReplace);
        dest.writeString(contextFiliter);
        dest.writeString(contextFiliterReplace);
        dest.writeByte((byte) (breakDown == null ? 0 : breakDown ? 1 : 2));
        dest.writeInt(actioner);
    }
}
