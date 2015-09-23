package com.edusoho.test.article;

import android.test.UiThreadTest;

import com.edusoho.kuozhi.v3.ui.fragment.article.ArticleFragment;
import com.edusoho.test.base.BaseFragmentTestCase;

/**
 * Created by howzhi on 15/9/9.
 */
public class ArticleFragmentTest extends BaseFragmentTestCase<ArticleFragment> {

    public ArticleFragmentTest()
    {
        super(ArticleFragment.class);
    }

    @UiThreadTest
    public void testInitView() {
        ArticleFragment articleFragment = getFragment();
        assertNotNull(articleFragment);
    }
}
