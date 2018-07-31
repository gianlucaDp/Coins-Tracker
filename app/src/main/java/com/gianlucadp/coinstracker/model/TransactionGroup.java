package com.gianlucadp.coinstracker.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import java.util.ArrayList;
import java.util.List;

public class TransactionGroup implements Parcelable {
public enum GroupType{REVENUE,DEPOSIT,EXPENSE}
private GroupType type;
private String name;
private CommunityMaterial.Icon imageId;
private float initialValue = 0;
private List<TransactionValue> transactionsValue = new ArrayList<>();

@Exclude
private String firebaseId;


public TransactionGroup(){};

public TransactionGroup(GroupType type,String name, CommunityMaterial.Icon imageId){
    this.type = type;
    this.name = name;
    this.imageId = imageId;
}

    public TransactionGroup(GroupType type,String name, CommunityMaterial.Icon imageId, float initialValue){
    this(type,name,imageId);
    this.initialValue = initialValue;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommunityMaterial.Icon getImageId() {
        return imageId;
    }

    public void setImageId(CommunityMaterial.Icon imageId) {
        this.imageId = imageId;
    }

    public float getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(float initialValue) {
        this.initialValue = initialValue;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public List<TransactionValue> getTransactionsValue() {
        return transactionsValue;
    }

    public void setTransactionsValue(List<TransactionValue> transactionsValue) {
        this.transactionsValue = transactionsValue;
    }

    public void addTransactionValue(TransactionValue transactionValue){
        this.transactionsValue.add(transactionValue);
    }

    public float getValue(){
        float value = 0;


        if (type== TransactionGroup.GroupType.DEPOSIT) {
            value  += initialValue;
        }

        if (transactionsValue!=null && transactionsValue.size()>0){

            for (TransactionValue transaction: transactionsValue) {
                value+=transaction.getRealValue();
            }
        }
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.name);
        dest.writeInt(this.imageId == null ? -1 : this.imageId.ordinal());
        dest.writeFloat(this.initialValue);
        dest.writeList(this.transactionsValue);
        dest.writeString(this.firebaseId);
    }

    protected TransactionGroup(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : GroupType.values()[tmpType];
        this.name = in.readString();
        int tmpImageId = in.readInt();
        this.imageId = tmpImageId == -1 ? null : CommunityMaterial.Icon.values()[tmpImageId];
        this.initialValue = in.readFloat();
        this.transactionsValue = new ArrayList<TransactionValue>();
        in.readList(this.transactionsValue, TransactionValue.class.getClassLoader());
        this.firebaseId = in.readString();
    }

    public static final Creator<TransactionGroup> CREATOR = new Creator<TransactionGroup>() {
        @Override
        public TransactionGroup createFromParcel(Parcel source) {
            return new TransactionGroup(source);
        }

        @Override
        public TransactionGroup[] newArray(int size) {
            return new TransactionGroup[size];
        }
    };
}
