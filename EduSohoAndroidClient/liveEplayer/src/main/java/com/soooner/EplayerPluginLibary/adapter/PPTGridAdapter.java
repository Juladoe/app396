package com.soooner.EplayerPluginLibary.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.soooner.EplayerPluginLibary.EplayerPluginPadActivity;
import com.soooner.EplayerPluginLibary.R;
import com.soooner.EplayerPluginLibary.util.LogUtil;
import com.soooner.source.common.util.FileUtils;
import com.soooner.source.common.util.ImageUtil;
import com.soooner.source.common.util.StorageUtil;
import com.soooner.source.common.util.StringUtils;
import com.soooner.source.entity.PicUrl;
import com.soooner.source.entity.SessionData.DrawPadInfo;
import com.soooner.source.system.BlackBoardLoader;
import com.soooner.source.system.DocumentItem;
import com.soooner.source.system.DocumentMap;
import com.soooner.source.system.ImageLoader;
import com.soooner.widget.ImageViewExt;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoxu2014 on 14-11-6.
 */
public class PPTGridAdapter extends BaseAdapter {
    EplayerPluginPadActivity activity;
    GridView gridView;
    int currentPostion;
    int currentClick;
    View selectView = null;
    FinalBitmap fb;
    List<DocumentItem> list = new ArrayList<DocumentItem>();
    int pptId=-1;
    double BASE_SCREEN_WIDTH_SCALE=0;
    int gridview_item_fl_width,gridview_item_fl_height,gridview_item_tv_height;
    Resources res;

    public int getCurrentPostion() {
        return currentPostion;
    }

    private void setCurrentPostion(int currentPostion) {
        this.currentPostion = currentPostion;

    }

    private void setList(List<DocumentItem> picUrlslist) {
        if (null == picUrlslist) {
            list = new ArrayList<DocumentItem>();
        } else {
            this.list = picUrlslist;
        }


    }

    public void fillData(DrawPadInfo drawPadInfo) {
        if(drawPadInfo.userSwitch)
            return;

        setCurrentPostion(drawPadInfo.page-1);

        if (this.pptId != drawPadInfo.pptId) {
            this.pptId = drawPadInfo.pptId;

            this.loadAsync(drawPadInfo);

        } else {
            if (this.list == null || this.list.size()== 0) {
                this.loadAsync(drawPadInfo);
            } else {

                this.itemSelect(drawPadInfo.page-1);
            }
        }
        gridView.setSelection(drawPadInfo.page-1);

    }

    private void loadAsync(final DrawPadInfo drawPadInfo){
        BlackBoardLoader.loadList(drawPadInfo, new BlackBoardLoader.CallBack() {

            @Override
            public void onComplete(String pictureLocalUrl, int pptId, int page) {

            }

            @Override
            public void onCompleteList(final List<DocumentItem> list, int pptId) {
                activity.runOnUiThread(new Runnable() {

                                           @Override
                                           public void run() {
                                               PPTGridAdapter.this.setList(list);
//                                               PPTGridAdapter.this.notifyDataSetChanged();
                                               PPTGridAdapter.this.itemSelect(drawPadInfo.page-1);
                                               gridView.setSelection(drawPadInfo.page-1);
                                           }
                                       }

                );
            }

            @Override
            public void onLoadingFailed(int pptId, int page) {

            }

            @Override
            public void onAssetIsNull(int pptId, int page) {

            }
        });
    }

    public PPTGridAdapter(EplayerPluginPadActivity context, int currentPostion,GridView gridView,double BASE_SCREEN_WIDTH_SCALE) {
        this.activity = context;
        this.gridView=gridView;
        setCurrentPostion(currentPostion);
        fb=FinalBitmap.create(this.activity);
        this.BASE_SCREEN_WIDTH_SCALE=BASE_SCREEN_WIDTH_SCALE;
        res=context.getResources();
        gridview_item_fl_width = (int) res.getDimension(R.dimen.gridview_item_fl_width);
        gridview_item_fl_height = (int) res.getDimension(R.dimen.gridview_item_fl_height);
        gridview_item_tv_height = (int) res.getDimension(R.dimen.gridview_item_tv_height);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int postion, View convertView, ViewGroup viewGroup) {
        LogUtil.d("Pptgridadapter","postion :"+postion+" convertView:"+convertView);
        View view;
        ViewHolder viewHolder;
        if (null == convertView) {
            view = View.inflate(activity, R.layout.gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_num = (TextView) view.findViewById(R.id.tv_num);
            viewHolder.img_pptbg = (ImageViewExt) view.findViewById(R.id.img_pptbg);
            viewHolder.tv_pptbg_select = (TextView) view.findViewById(R.id.tv_pptbg_select);
            viewHolder.rl_item_all= (RelativeLayout) view.findViewById(R.id.rl_item_all);
            viewHolder.fl_ppt_bg= (FrameLayout) view.findViewById(R.id.fl_ppt_bg);

            AbsListView.LayoutParams lp=new AbsListView.LayoutParams((int) (gridview_item_fl_width*BASE_SCREEN_WIDTH_SCALE),(int) ((gridview_item_fl_height+gridview_item_tv_height)*BASE_SCREEN_WIDTH_SCALE));
            viewHolder.rl_item_all.setLayoutParams(lp);

            RelativeLayout.LayoutParams  fl_ppt_bgLayoutParams= (RelativeLayout.LayoutParams) viewHolder.fl_ppt_bg.getLayoutParams();
            fl_ppt_bgLayoutParams.width= (int) (gridview_item_fl_width*BASE_SCREEN_WIDTH_SCALE);
            fl_ppt_bgLayoutParams.height= (int) (gridview_item_fl_height*BASE_SCREEN_WIDTH_SCALE);
            viewHolder.tv_num.getLayoutParams().height= (int) (gridview_item_tv_height*BASE_SCREEN_WIDTH_SCALE);


            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
            if(viewHolder.position==postion&&viewHolder.pptId==pptId){
                if (currentPostion == postion) {
                    viewHolder.tv_pptbg_select.setActivated(false);
                    viewHolder.tv_pptbg_select.setSelected(true);
                }else{
                    viewHolder.tv_pptbg_select.setSelected(false);
                    viewHolder.tv_pptbg_select.setActivated(false);
                }

                return view;
            }
        }
        viewHolder.img_pptbg.token=System.currentTimeMillis();

        viewHolder.position = postion;
        viewHolder.pptId = pptId;
        initview(viewHolder);

        DocumentItem documentItem = (DocumentItem) getItem(postion);

        PicUrl picUrl = documentItem.coverThumb();
        String picUri = picUrl.getLocalPicPath();

        if (FileUtils.isExist(picUri)) {
            DocumentMap.setPath(documentItem,true,picUri);

//            BlackBoardLoader.PPTPathMap.put(picUrl.getPptid_page(),picUri);
            reloadImageView(viewHolder.img_pptbg, picUri);
        } else {
            ImageLoader.load(picUrl, viewHolder.img_pptbg, activity, null);
        }
//        viewHolder.tv_pptbg_select.setOnClickListener(onClickListener);
        viewHolder.tv_num.setText((postion + 1) + "");
        viewHolder.tv_pptbg_select.setTag(postion);

        viewHolder.tv_pptbg_select.setBackgroundResource(R.drawable.grid_pptbg_select);

        if (currentPostion == postion) {
            viewHolder.tv_pptbg_select.setActivated(false);
            viewHolder.tv_pptbg_select.setSelected(true);
//            LogUtil.d("LineGridView2","setSelected:"+view);
//            viewHolder.tv_pptbg_select.setBackgroundResource(R.drawable.gridview_item_fl_bg);
        }else{
            viewHolder.tv_pptbg_select.setSelected(false);
            viewHolder.tv_pptbg_select.setActivated(false);
//            LogUtil.d("LineGridView2","none:"+view);
//            viewHolder.tv_pptbg_select.setBackgroundResource(android.R.color.transparent);
        }

        return view;
    }

    public void itemClick(int position){


        if(this.currentClick==position){
            return;
        }
        this.currentClick = position;

//        this.currentPostion =position;

        for (int i=0;i<this.gridView.getChildCount();i++){
            View childView = this.gridView.getChildAt(i);
            ViewHolder viewHolder = (ViewHolder) childView.getTag();

            if(!viewHolder.tv_pptbg_select.isSelected())
                viewHolder.tv_pptbg_select.setActivated(viewHolder.position==position);

        }

        DocumentItem documentItem = (DocumentItem) getItem(position);

        PicUrl picUrl = documentItem.coverThumb();
        String imgPath = picUrl.getLocalPicPath();

        if(!StringUtils.isValid(imgPath)){
            return;
        }


        activity.changeDrawImageViewBg(position,true);
    }
    public void itemSelect(int position){

        if(this.currentClick==position){
            return;
        }
        this.currentClick = position;

        this.currentPostion =position;
        for (int i=0;i<this.gridView.getChildCount();i++){
            View childView = this.gridView.getChildAt(i);
            ViewHolder viewHolder = (ViewHolder) childView.getTag();
            viewHolder.tv_pptbg_select.setActivated(false);
            viewHolder.tv_pptbg_select.setSelected(viewHolder.position == currentPostion);

        }

        DocumentItem picUrl = (DocumentItem) getItem(this.currentPostion);
        String imgPath=picUrl.thumbUrl;
        if(!StringUtils.isValid(imgPath)){
            return;
        }


        activity.changeDrawImageViewBg(this.currentPostion,false);
    }
//
//    View.OnClickListener onClickListener=  new View.OnClickListener() {
//
//        @Override
//        public void onClick(View view) {
//            int selectPostion = (Integer) view.getTag();
//
//            PicUrl picUrl = (PicUrl) getItem(selectPostion);
//            String imgPath=BlackBoardLoader.PPTPathMap.get(picUrl.getPptid_page());
//            if(!StringUtils.isValid(imgPath)){
//                return;
//            }
//
//
//            activity.changeDrawImageViewBg(selectPostion);
//
//            view.setActivated(true);
////
////
////            if (view == selectView) {
////                return;
////            }
////
////
////            if (null != selectView) {
////                int preView=(Integer)selectView.getTag();
////                view.setActivated(false);
////                if(currentPostion == (preView + 1)){
////                    view.setSelected(true);
//////                    view.setBackgroundResource(R.drawable.gridview_item_fl_bg);
////                }else{
////                    view.setSelected(false);
//////                    selectView.setBackgroundResource(android.R.color.transparent);
////                }
////
////            }
////            if(currentPostion == (selectPostion + 1)){
////                view.setActivated(false);
////                view.setSelected(true);
//////                view.setBackgroundResource(R.drawable.gridview_item_fl_bg);
////            }else{
//////                view.setBackgroundResource(R.drawable.gridview_item_fl_bg2);
////                view.setSelected(false);
////                view.setActivated(true);
////            }
////
////            selectView = view;
//
//
//        }
//    };

    private void reloadImageView(ImageView childAt, String filePath) {

//        Bitmap bitmap =fb.display(childAt,filePath); ;//ImageUtil.readLocalImage(filePath);
//        childAt.setImageBitmap(bitmap);
        fb.display(childAt,filePath);
    }

    private void initview(ViewHolder viewHolder) {
        viewHolder.img_pptbg.setBackgroundResource(R.drawable.grid_item_bg);
        viewHolder.img_pptbg.setImageBitmap(null);
        viewHolder.tv_pptbg_select.setTag(null);
    }


    public static class ViewHolder {
        RelativeLayout rl_item_all;
        FrameLayout fl_ppt_bg ;
        TextView tv_num;
        ImageViewExt img_pptbg;
        TextView tv_pptbg_select;

        int position;
        int pptId;
    }
}
