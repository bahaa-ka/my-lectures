package com.kingofnothing.mylectures;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class GridViewItemAdapter extends ArrayAdapter<GridViewItem> {
    ArrayList<GridViewItem> gridViewItems;


    public GridViewItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<GridViewItem> objects) {
        super(context, resource, objects);
        this.gridViewItems = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View GridViewItem = convertView;
        if(GridViewItem == null){
            GridViewItem = LayoutInflater.from(getContext()).inflate(R.layout.layout_content_card,parent,false);
        }

        GridViewItem currentGridViewItem = getItem(position);

        TextView tagName = (TextView) GridViewItem.findViewById(R.id.item_name);
        tagName.setText(currentGridViewItem.getTagName());

        ImageView tagImage = (ImageView) GridViewItem.findViewById(R.id.item_image);
        tagImage.setImageResource(currentGridViewItem.getTagImageResourceID());

        return GridViewItem;
    }

    @Override
    public int getCount() {
        return gridViewItems.size();
    }

    @Override
    public GridViewItem getItem(int position) {
        return gridViewItems.get(position);
    }

    public void updateList(ArrayList<GridViewItem> newList){
        gridViewItems = new ArrayList<GridViewItem>();
        gridViewItems.addAll(newList);
        notifyDataSetChanged();
    }
}
