package library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.edusoho.kuozhi.commonlib.R;


/**
 * Created by howzhi on 14-9-16.
 */
public class PullToRefreshFragment extends PullToRefreshBase<FrameLayout> {

    public PullToRefreshFragment(Context context) {
        super(context);
    }

    public PullToRefreshFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        View scrollViewChild = mRefreshableView.getChildAt(0);
        if (null != scrollViewChild) {
            return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
        }
        return false;
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected FrameLayout createRefreshableView(Context context, AttributeSet attrs) {
        FrameLayout frameLayout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            frameLayout = new InternalFrameLayoutSDK9(context, attrs);
        } else {
            frameLayout = new FrameLayout(context, attrs);
        }

        frameLayout.setId(R.id.scrollview);
        return frameLayout;
    }

    @TargetApi(9)
    final class InternalFrameLayoutSDK9 extends FrameLayout {

        public InternalFrameLayoutSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshFragment.this, deltaX, scrollX, deltaY, scrollY,
                    getScrollRange(), isTouchEvent);

            return returnValue;
        }

        /**
         * Taken from the AOSP ScrollView source
         */
        private int getScrollRange() {
            int scrollRange = 0;
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                scrollRange = Math.max(0, child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
            }
            return scrollRange;
        }
    }
}
