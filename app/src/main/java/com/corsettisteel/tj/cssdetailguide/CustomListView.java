package com.corsettisteel.tj.cssdetailguide;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJ on 2/27/2018.
 */

public class CustomListView extends ListView {

    private CustomAdapter adapter;
    private int itemsToShow;

    public CustomListView(Context context, int itemsToShow, Bundle savedInstanceState) {
        super(context);
        this.itemsToShow = itemsToShow;

        adapter = new CustomAdapter(context, R.layout.list_item, new ArrayList());
        setAdapter(adapter);
    }

    public void setItems(ArrayList<Component> items) {
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
        adapter.addEmpties(itemsToShow / 2);
    }

    public void setItemsToShow(int itemsToShow) {
        this.itemsToShow = itemsToShow;
    }

    public int getMiddleCellPosition() {
        return (getFirstVisiblePosition() + getLastVisiblePosition()) / 2;
    }


    private class CustomAdapter extends ArrayAdapter<Component> {

        private List<Component> items;
        private int cellHeight = 0;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            items = objects;
        }


        public void setCellHeight(int height) {
            cellHeight = height;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView textView = null;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, null);
                textView = (TextView) convertView.findViewById(R.id.item_text);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }

            if (textView != null) {
                textView.setText(items.get(position).getShape());
                //textView.setText(position + "");
                if (textView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                    p.height = cellHeight;
                    textView.setLayoutParams(p);
                    textView.requestLayout();
                }
            }
            return convertView;
        }

        public void addEmpties(int size) {
            for (int i = 0; i < size; i++) {
                items.add(new Component());
                items.add(0, new Component());
            }
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public int getCount() {
            return items.size();
        }

    }

}
