package com.edusoho.kuozhi.view;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

import com.edusoho.kuozhi.R;

/**
 * Created by howzhi on 14-8-14.
 */
public class EdusohoAutoCompleteTextView extends AutoCompleteTextView {

    private KeyDownCallback mKeyDownCallback;

    public EdusohoAutoCompleteTextView(Context context)
    {
        super(context);
    }

    public EdusohoAutoCompleteTextView(Context context, android.util.AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setKeyDownCallback(KeyDownCallback callback)
    {
        this.mKeyDownCallback = callback;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mKeyDownCallback != null) {
            mKeyDownCallback.invoke(text.length());
        }
    }

    public static interface KeyDownCallback
    {
        public void invoke(int length);
    }
}
