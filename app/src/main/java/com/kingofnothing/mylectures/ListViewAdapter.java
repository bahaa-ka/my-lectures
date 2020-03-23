package com.kingofnothing.mylectures;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<GridViewItem> {
    ArrayList<GridViewItem> listViewItems;


    public ListViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<GridViewItem> objects) {
        super(context, resource, objects);
        this.listViewItems = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View GridViewItem = convertView;
        if(GridViewItem == null){
            GridViewItem = LayoutInflater.from(getContext()).inflate(R.layout.layout_card,parent,false);
        }

        GridViewItem currentGridViewItem = getItem(position);

        TextView tagName = (TextView) GridViewItem.findViewById(R.id.card_title);
        tagName.setText(currentGridViewItem.getTagName());

        ImageView tagImage = (ImageView) GridViewItem.findViewById(R.id.card_image);
        tagImage.setImageResource(currentGridViewItem.getTagImageResourceID());

        return GridViewItem;
    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public GridViewItem getItem(int position) {
        return listViewItems.get(position);
    }



    public void updateList(ArrayList<GridViewItem> newList){
        listViewItems = new ArrayList<GridViewItem>();
        listViewItems.addAll(newList);
        notifyDataSetChanged();
    }
}
