package com.edusoho.listener;

import com.edusoho.kowzhi.model.School;
import com.edusoho.kowzhi.ui.BaseActivity;
import com.edusoho.kowzhi.ui.SchCourseActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SchoolListClickListener implements OnItemClickListener{

	private BaseActivity mContext;
	
	public SchoolListClickListener(BaseActivity activity)
	{
		mContext = activity;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int index, long arg3) {
        if (beforeClick(parent, index)) {
            return;
        }
		setCurrentSchool((School)parent.getItemAtPosition(index));
		Intent courseIntent = new Intent(mContext,
				SchCourseActivity.class);
		mContext.startActivity(courseIntent);
		afterClick(parent, index);
	}

    /**
     * if true not next
     * @param parent
     * @param index
     * @return
     */
    public boolean beforeClick(AdapterView<?> parent,int index){
        return false;
    }
	public void afterClick(AdapterView<?> parent,int index){}

	private void setCurrentSchool(School sch)
	{
		SharedPreferences sp = mContext.getSharedPreferences("defaultSchool", mContext.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("name", sch.name);
		edit.putString("url", sch.url);
		edit.putString("logo", sch.logo);
		edit.commit();
		
		mContext.saveCurrentSchool(sch);
	}
}
