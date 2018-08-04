package com.gianlucadp.coinstracker.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gianlucadp.coinstracker.R;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.supportClasses.IconsManager;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;


public class IconsArrayAdapter extends BaseAdapter {
    private Context context;
    private CommunityMaterial.Icon[] iconIds;
    private LayoutInflater inflater;
    private TransactionGroup.GroupType groupType;

    public IconsArrayAdapter(Context applicationContext, TransactionGroup.GroupType type) {
        this.context = applicationContext;
        this.iconIds = IconsManager.getAvailableIcons();
        inflater = (LayoutInflater.from(applicationContext));
        this.groupType = type;
    }

    @Override
    public int getCount() {
        return iconIds.length;
    }

    @Override
    public CommunityMaterial.Icon getItem(int i) {
        return iconIds[i];
    }

    @Override
    public long getItemId(int i) {
        return iconIds[i].ordinal();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = inflater.inflate(R.layout.item_icon_list, viewGroup,false);
        ImageView icon =  row.findViewById(R.id.im_array_icon);
        Drawable iconDrawable = IconsManager.createNewIcon(context,iconIds[i], IconsManager.setColorBasedOnType(context,groupType),24);
        icon.setImageDrawable(iconDrawable);
        return row;
    }
}