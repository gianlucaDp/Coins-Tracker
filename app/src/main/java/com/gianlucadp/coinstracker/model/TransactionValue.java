package com.gianlucadp.coinstracker.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class TransactionValue implements Parcelable {
    private String transactionId;
    private float value;
    private boolean isExpense;
    @Exclude
    private String transactionValueFirebaseId;


    public TransactionValue(){}

    public TransactionValue(String transactionId, float value, boolean isExpense){
        this.transactionId = transactionId;
        this.isExpense = isExpense;
        this.value = value;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public float getValue() {
        return value;
    }

    @Exclude
    public float getRealValue(){
        if (this.isExpense){
            return -value;
        }else{
            return value;
        }

    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getTransactionValueFirebaseId() {
        return transactionValueFirebaseId;
    }

    public void setTransactionValueFirebaseId(String transactionValueFirebaseId) {
        this.transactionValueFirebaseId = transactionValueFirebaseId;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public void setExpense(boolean expense) {
        isExpense = expense;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.transactionId);
        dest.writeFloat(this.value);
        dest.writeByte(this.isExpense ? (byte) 1 : (byte) 0);
        dest.writeString(this.transactionValueFirebaseId);
    }

    protected TransactionValue(Parcel in) {
        this.transactionId = in.readString();
        this.value = in.readFloat();
        this.isExpense = in.readByte() != 0;
        this.transactionValueFirebaseId = in.readString();
    }

    public static final Parcelable.Creator<TransactionValue> CREATOR = new Parcelable.Creator<TransactionValue>() {
        @Override
        public TransactionValue createFromParcel(Parcel source) {
            return new TransactionValue(source);
        }

        @Override
        public TransactionValue[] newArray(int size) {
            return new TransactionValue[size];
        }
    };
}
