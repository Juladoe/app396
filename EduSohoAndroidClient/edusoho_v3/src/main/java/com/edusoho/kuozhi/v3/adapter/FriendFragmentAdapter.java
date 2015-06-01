package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.List;

/**
 * Created by Melomelon on 2015/5/26.
 */
public class FriendFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnClickListener{

    public static final int ITEM_TYPE_SERCH = 0;
    public static final int ITEM_TYPE_ADD_FRIEND = 1;
    public static final int ITEM_TYPE_NOTECE = 2;
    public static final int ITEM_TYPE_FRIEND = 3;

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case ITEM_TYPE_SERCH:
                return new searchItemHolder(mLayoutInflater.inflate(R.layout.item_type_search,parent,false));
            case ITEM_TYPE_ADD_FRIEND:
                return new addFriendItemHolder(mLayoutInflater.inflate(R.layout.item_type_add_friend,parent,false));
            case ITEM_TYPE_NOTECE:
                return new noticeItemHolder(mLayoutInflater.inflate(R.layout.item_type_notice,parent,false));
            case ITEM_TYPE_FRIEND:
                return new friendItemHolder(mLayoutInflater.inflate(R.layout.item_type_friend,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof noticeItemHolder && position == 6){
            ((noticeItemHolder)holder).textView.setText("好友");
        }

        if(holder instanceof addFriendItemHolder){
            switch (position){
                case 2:
                    ((addFriendItemHolder)holder).textView.setText("添加课程好友");
                    break;
                case 3:
                    ((addFriendItemHolder)holder).textView.setText("添加班级好友");
                    break;
            }

        }

        //TODO
        holder.itemView.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        //TODO for test
        return 50;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return ITEM_TYPE_SERCH;
        }else if (0 < position && position < 4){
            return ITEM_TYPE_ADD_FRIEND;
        }else if(position == 4 || position == 6){
            return ITEM_TYPE_NOTECE;
        }else {
            return ITEM_TYPE_FRIEND;
        }
    }

    public FriendFragmentAdapter(Context mContext) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onClick(View v) {
        //TODO
        switch (v.getId()){

        }

    }

    public class searchItemHolder extends RecyclerView.ViewHolder {

        public searchItemHolder(View itemView) {
            super(itemView);
        }
    }

    public class addFriendItemHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        public addFriendItemHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.icon);
            textView = (TextView) itemView.findViewById(R.id.tv_add_contact);
        }
    }

    public class noticeItemHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public noticeItemHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.notice);
        }
    }

    public class friendItemHolder extends RecyclerView.ViewHolder {
        public friendItemHolder(View itemView) {
            super(itemView);
        }
    }
}
