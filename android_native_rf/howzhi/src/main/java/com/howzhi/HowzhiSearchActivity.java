package com.howzhi;

import android.view.View;

import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.ui.common.SearchActivity;
import com.edusoho.kuozhi.ui.course.CourseInfoActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.CommentPopupDialog;
import com.edusoho.listener.NormalCallback;

/**
 * Created by howzhi on 14-7-8.
 */
public class HowzhiSearchActivity extends SearchActivity {

    @Override
    protected void initParams() {
        this.mPageLimit = 1;
        this.mPage = 1;
    }

    @Override
    public StringBuffer getSearchUrl(int start, String searchStr)
    {
        StringBuffer param = new StringBuffer(Const.SEARCH);
        param.append("?page=").append(start);
        param.append("&q=").append(searchStr);
        return param;
    }
}
