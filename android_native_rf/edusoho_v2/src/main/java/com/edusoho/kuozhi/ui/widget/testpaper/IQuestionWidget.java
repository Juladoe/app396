package com.edusoho.kuozhi.ui.widget.testpaper;

import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

/**
 * Created by howzhi on 14-9-29.
 */
public interface IQuestionWidget {

    public void setData(QuestionTypeSeq questionSeq , int index);
}
