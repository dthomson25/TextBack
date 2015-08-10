package com.dthomson.textback;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dthomson on 8/6/2015.
 */
public class TextMessage {
    private String address;
    private String lastText;
    private String threadId;
    private String pictureID;
    private String picture_Data;

    public TextMessage(String address, String lastText, String threadId, String pictureID, String picture_Data) {
        this.address = address;
        this.lastText = lastText;
        this.threadId = threadId;
        this.pictureID = pictureID;
        this.picture_Data = picture_Data;
    }

    public TextMessage(String address, String lastText) {
        this.address = address;
        this.lastText = lastText;
        this.threadId = null;
        this.pictureID = null;
        this.picture_Data = null;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastText() {
        return lastText;
    }

    public void setLastText(String lastText) {
        this.lastText = lastText;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getPictureID() {
        return pictureID;
    }

    public void setPictureID(String pictureID) {
        this.pictureID = pictureID;
    }

    public String getPicture_Data() {
        return picture_Data;
    }

    public void setPicture_Data(String picture_Data) {
        this.picture_Data = picture_Data;
    }
}
