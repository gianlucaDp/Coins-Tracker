package com.gianlucadp.coinstracker.supportClasses;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class IconsManager {

    private static CommunityMaterial.Icon[] availableIcons = {
            CommunityMaterial.Icon.cmd_star,
            CommunityMaterial.Icon.cmd_home,
            CommunityMaterial.Icon.cmd_phone,
            CommunityMaterial.Icon.cmd_airplane,
            CommunityMaterial.Icon.cmd_baby_buggy,
            CommunityMaterial.Icon.cmd_bank,
            CommunityMaterial.Icon.cmd_basket,
            CommunityMaterial.Icon.cmd_cart,
            CommunityMaterial.Icon.cmd_gift,
            CommunityMaterial.Icon.cmd_cake,
            CommunityMaterial.Icon.cmd_car,
            CommunityMaterial.Icon.cmd_bus,
            CommunityMaterial.Icon.cmd_cat,
            CommunityMaterial.Icon.cmd_coffee,
            CommunityMaterial.Icon.cmd_creation,
            CommunityMaterial.Icon.cmd_emoticon,
            CommunityMaterial.Icon.cmd_heart,
            CommunityMaterial.Icon.cmd_smoking,
            CommunityMaterial.Icon.cmd_account,

    };

    public static CommunityMaterial.Icon[] getAvailableIcons(){
        return availableIcons;
    }
    public static Drawable createNewIcon(Context context, CommunityMaterial.Icon iconId, int color, int size ){
        return  new IconicsDrawable(context)
                .icon(iconId)
                .color(color)
                .sizeDp(size);
    }



}
