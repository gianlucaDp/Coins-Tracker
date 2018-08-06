package com.gianlucadp.coinstracker.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class Transaction implements Parcelable {
    private String fromGroup;
    private String toGroup;
    private float value;
    private long timestamp;
    private String notes;
    private boolean isExpense;
    @Exclude
    private String firebaseId;

    public Transaction(){}

    public Transaction(String fromGroup, String toGroup, float value, long timestamp, String notes, boolean isExpense){
        this.fromGroup = fromGroup;
        this.toGroup = toGroup;
        this.value = value;
        this.timestamp = timestamp;
        this.notes = notes;
        this.isExpense = isExpense;

    }



    public String getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
    }

    public String getToGroup() {
        return toGroup;
    }

    public void setToGroup(String toGroup) {
        this.toGroup = toGroup;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public void setExpense(boolean expense) {
        isExpense = expense;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fromGroup);
        dest.writeString(this.toGroup);
        dest.writeFloat(this.value);
        dest.writeLong(this.timestamp);
        dest.writeString(this.notes);
        dest.writeByte(this.isExpense ? (byte) 1 : (byte) 0);
        dest.writeString(this.firebaseId);
    }

    protected Transaction(Parcel in) {
        this.fromGroup = in.readString();
        this.toGroup = in.readString();
        this.value = in.readFloat();
        this.timestamp = in.readLong();
        this.notes = in.readString();
        this.isExpense = in.readByte() != 0;
        this.firebaseId = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
