package com.edusoho.kowzhi.ui;

import java.util.ArrayList;
import java.util.Map;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kowzhi.R;
import com.edusoho.kowzhi.adapter.IndexPagerAdapter;
import com.edusoho.kowzhi.adapter.MySchoolListAdapter;
import com.edusoho.kowzhi.entity.CarouselItem;
import com.edusoho.kowzhi.entity.RecommendSchoolItem;
import com.edusoho.kowzhi.util.Const;
import com.edusoho.kowzhi.util.SqliteUtil.QueryCallBack;
import com.edusoho.listener.SchoolListClickListener;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author howzhi
 *
 */
public class IndexPageActivity extends BaseActivity {

    /** */
    private ViewPager image_viewpager;
    private GridView my_sch_listview;
    private LinearLayout cancelBtnLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_page);
        initView();
    }

    /**
     *
     */
    private void initView() {
        cancelBtnLayout = (LinearLayout)findViewById(R.id.cancelBtnLayout);
        image_viewpager = (ViewPager) findViewById(R.id.image_viewpager);
        my_sch_listview = (GridView) findViewById(R.id.my_sch_listview);

        loadViewPager();
    }

    private MySchoolListAdapter mslAdapter;
    /**
     * 载入收藏网校列表
     */
    private void loadMySchoolList() {
        ArrayList<RecommendSchoolItem> rchList = loadRecommendSch();
        rchList.add(RecommendSchoolItem.createAddItem());

        mslAdapter = new MySchoolListAdapter(mContext,
                rchList, R.layout.my_sch_list_item);
        my_sch_listview.setAdapter(mslAdapter);

        my_sch_listview.setOnItemClickListener(new SchoolListClickListener(this) {
            @Override
            public boolean beforeClick(AdapterView<?> parent, int index) {
                RecommendSchoolItem item = (RecommendSchoolItem)parent.getItemAtPosition(index);
                if (item.type == RecommendSchoolItem.ADDITEM) {
                    Intent netSchoolIntent = new Intent();
                    netSchoolIntent.setClass(mContext, NetSchoolActivity.class);
                    startActivity(netSchoolIntent);
                    return true;
                }
                return false;
            }
        });

        my_sch_listview.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int arg2, long arg3) {
                changeCancelBtnStatus(true);
                mslAdapter.setDelMode(true);
                return true;
            }
        });
    }

    /**
     *
     * @param isShow
     */
    private void changeCancelBtnStatus(boolean isShow)
    {
        Animation anim;
        if (isShow) {
            if (cancelBtnLayout.getVisibility() == View.GONE) {
                System.out.println("show");
                cancelBtnLayout.setVisibility(View.VISIBLE);
                cancelBtnLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeCancelBtnStatus(false);
                        mslAdapter.setDelMode(false);
                    }
                });
            }
            anim = AnimationUtils.loadAnimation(mContext, R.anim.hide_cancelbtn);
        } else {
            anim = AnimationUtils.loadAnimation(mContext, R.anim.show_cancelbtn);
        }

        anim.setFillBefore(false);
        anim.setFillAfter(true);
        cancelBtnLayout.startAnimation(anim);
    }

    /**
     *
     * @param newList
     */
    private void saveCarouselToLocal(ArrayList<CarouselItem> newList) {
        ContentValues cv = new ContentValues();
        app.sqliteUtil.execSQL(Const.CLEAR_DB);
        for (CarouselItem item : newList) {
            cv.clear();
            cv.put("title", item.title);
            cv.put("action", item.action);
            cv.put("image", item.image);
            app.sqliteUtil.insert(Const.T_CAROUSEL, cv);
        }
    }

    /**
     *
     * @return
     */
    private ArrayList<CarouselItem> queryCarouselFromLocal() {
        final ArrayList<CarouselItem> list = new ArrayList<CarouselItem>();
        app.sqliteUtil.query(new QueryCallBack() {
            @Override
            public void query(Cursor cursor) {
                CarouselItem item = new CarouselItem();
                item.title = cursor.getString(cursor.getColumnIndex("title"));
                item.image = cursor.getString(cursor.getColumnIndex("image"));
                item.action = cursor.getString(cursor.getColumnIndex("action"));
                list.add(item);
            }
        }, "select * from edusoho_carousel", new String[] {});
        return list;
    }

    private ArrayList<CarouselItem> currentCarouselList;

    /**
     *
     * @param oldList
     */
    private void initViewPager(ArrayList<CarouselItem> oldList) {
        ArrayList<View> viewList = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        View pager = null;
        if (oldList.isEmpty()) {
            pager = inflater.inflate(R.layout.pager_view, null);
            AQuery query = new AQuery(pager);
            query.id(R.id.pager_image).image(R.drawable.img1);
            viewList.add(pager);
        } else {
            for (CarouselItem item : oldList) {
                pager = inflater.inflate(R.layout.pager_view, null);
                AQuery query = new AQuery(pager);
                query.id(R.id.pager_image).image(item.image, Const.memCacheNo,
                        Const.fileCacheYes, 0, R.drawable.carousel_default);
                viewList.add(pager);
            }
        }

        image_viewpager.removeAllViews();
        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.viewpager_index_layout);
        IndexPagerAdapter ipAdapter = new IndexPagerAdapter(mContext, viewList, inflater, indexLayout) {
            @Override
            public void onClick(View v) {
                int index = (Integer) v.getTag();
                CarouselItem item = currentCarouselList.get(index);
                Toast.makeText(mContext, item.image, Toast.LENGTH_LONG).show();
            }
        };

        image_viewpager.setAdapter(ipAdapter);
        image_viewpager.setOnPageChangeListener(ipAdapter);

        currentCarouselList = oldList;
    }

    private void loadViewPager() {
        final ArrayList<CarouselItem> carouseList = queryCarouselFromLocal();
        initViewPager(carouseList);
        // get new carousel
        app.query.ajax(app.host + Const.CAROUSEL, String.class,
                new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String str,
                                         AjaxStatus status) {
                        super.callback(url, str, status);
                        if (status.getCode() == 200) {
                            ArrayList<CarouselItem> list = app.gson.fromJson(
                                    str,
                                    new TypeToken<ArrayList<CarouselItem>>() {
                                    }.getType());
                            if (list != null) {
                                saveCarouselToLocal(list);
                                initViewPager(list);
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMySchoolList();
    }

    private ArrayList<RecommendSchoolItem> loadRecommendSch() {
        SharedPreferences sp = getSharedPreferences("recommend_school",
                MODE_PRIVATE);
        Map<String, ?> rchMap = sp.getAll();
        ArrayList<RecommendSchoolItem> list = new ArrayList<RecommendSchoolItem>();
        for (Object value : rchMap.values()) {
            RecommendSchoolItem item = app.gson.fromJson(
                    value.toString(), new TypeToken<RecommendSchoolItem>(){}.getType());
            list.add(item);
        }

        return list;
    }
}
