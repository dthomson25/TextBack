package com.dthomson.textback;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dthomson on 8/6/2015.
 */
public class TextMessage implements Parcelable {
    private String person;
    private String conversation;

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.person,
                this.conversation});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TextMessage createFromParcel(Parcel in) {
            return new TextMessage(in);
        }

        public TextMessage[] newArray(int size) {
            return new TextMessage[size];
        }
    };


    public TextMessage(String person, String conversation) {
        this.person = person;
        this.conversation = conversation;
    }

    public TextMessage(Parcel in) {
        String[] data = new String[2];

        in.readStringArray(data);
        this.person = data[0];
        this.conversation = data[1];
    }

    public String getPerson() {
        return this.person;
    }
    public String getConversation() {
        return this.conversation;
    }
}
