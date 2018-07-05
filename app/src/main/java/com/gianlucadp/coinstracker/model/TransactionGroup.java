package com.gianlucadp.coinstracker.model;


import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import java.util.ArrayList;
import java.util.List;

public class TransactionGroup {
public enum GroupType{REVENUE,DEPOSIT,EXPENSE}
private GroupType type;
private String name;
private CommunityMaterial.Icon imageId;
private float initialValue = 0;
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


}
