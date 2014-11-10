package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.model.LessonItem;

import java.util.HashMap;
import java.util.LinkedHashMap;

import views.PinnedSectionListView;

/**
 * Created by howzhi on 14-7-31.
 */
public class LessonPinnedAdapter extends CourseLessonListAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

    public LessonPinnedAdapter(
            Context context, LinkedHashMap<String, LessonItem> lessons, HashMap<Integer, String> userLearns, int resource) {
        super(context, lessons, userLearns, resource);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getItemViewType(int position) {
        LessonItem item = getItem(position);
        LessonItem.ItemType itemType = LessonItem.ItemType.cover(item.itemType);
        return itemType == LessonItem.ItemType.CHAPTER ? 1 : 0;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        System.out.println("itemType->"+viewType);
        return viewType == 1;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        return super.getView(index, view, vg);
    }
}
