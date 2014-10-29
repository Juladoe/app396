package com.howzhi;

import com.edusoho.kuozhi.ui.common.SearchActivity;
import com.edusoho.kuozhi.util.Const;

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
