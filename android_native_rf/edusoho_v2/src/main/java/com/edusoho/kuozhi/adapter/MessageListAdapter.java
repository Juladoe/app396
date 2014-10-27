package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.model.Notify;

import org.xml.sax.XMLReader;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-16.
 */
public class MessageListAdapter extends ListBaseAdapter<Notify>
{

    public MessageListAdapter(
            Context context,  int resource)
    {
        super(context, resource);
    }

    @Override
    public void addItems(ArrayList<Notify> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int index) {
        return mList.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        if (view == null) {
            view = inflater.inflate(mResource, null);
        }

        TextView textView = (TextView) view;
        Notify notify = mList.get(index);

        String text = notify.message;
        textView.setText(Html.fromHtml(text));

        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new NoLineClickSpan(text);
        spannableString.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    private class NoLineClickSpan extends ClickableSpan {
        String text;

        public NoLineClickSpan(String text) {
            super();
            this.text = text;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            Log.d(null, "message->onClick");
            //processHyperLinkClick(text);
        }
    }

}