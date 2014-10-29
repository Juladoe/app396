package com.edusoho.kuozhi.ui.note;

import android.util.Log;

import com.edusoho.kuozhi.model.Note.CollectNode;
import com.edusoho.kuozhi.model.Note.HttpDatas;
import com.edusoho.kuozhi.model.Note.NoteListData;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by onewoman on 14-10-10.
 */
//从网络读取数据，并组织成collectNode格式，返回
public class FilterHttpData {
    private ArrayList<HttpDatas> requestdatas;
    private ArrayList<CollectNode> collect;
    private List<String> imagepath;
    private List<String> coursename;
    private List<Integer> courseId;
    private List<Integer> total;
    private List<ArrayList<Integer>> lessonId;
    private List<ArrayList<String>> content;
    private List<ArrayList<String>> coursetitle;
    private List<ArrayList<String>> course;

    public FilterHttpData(ArrayList<HttpDatas> requestdatas) {
        this.requestdatas = requestdatas;
        initParam();
    }

    private void initParam() {
        setTitle();
        setTotal();
    }

    public void setTitle() {
        String titletemp = "";
        coursename = new ArrayList<String>();
        imagepath = new ArrayList<String>();
        courseId = new ArrayList<Integer>();
        for (int i = 0, j = 1; i < requestdatas.size(); i++) {
            titletemp = requestdatas.get(i).title;
            if (coursename == null || (!coursename.contains(titletemp))) {
                coursename.add(titletemp);
                imagepath.add(requestdatas.get(i).largePicture);
                courseId.add(requestdatas.get(i).courseId);
            }
        }
    }

    public void setTotal()
    {
        total = new ArrayList<Integer>();
        content = new ArrayList<ArrayList<String>>();
        coursetitle = new ArrayList<ArrayList<String>>();
        course = new ArrayList<ArrayList<String>>();
        lessonId = new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<courseId.size();i++)
        {
            int k = 0;
            ArrayList<String> contenttemp = new ArrayList<String>();
            ArrayList<String> coursetitletemp = new ArrayList<String>();
            ArrayList<String> coursetemp = new ArrayList<String>();
            ArrayList<Integer> lessontemp = new ArrayList<Integer>();
            for(int j=0;j<requestdatas.size();j++)
            {
                if(requestdatas.get(j).courseId == courseId.get(i))
                {
                    k++;
                    contenttemp.add(requestdatas.get(j).content);
                    coursetitletemp.add(requestdatas.get(j).lessonName);
                    coursetemp.add(requestdatas.get(j).number);
                    lessontemp.add(requestdatas.get(j).lessonId);
                }
            }
            total.add(k);
            content.add(contenttemp);
            coursetitle.add(coursetitletemp);
            course.add(coursetemp);
            lessonId.add(lessontemp);
        }
    }

    public ArrayList<CollectNode> getCollect() {
        collect = new ArrayList<CollectNode>();
        collect.clear();
        for (int i=0;i < coursename.size();i++) {
            CollectNode coursedata = new CollectNode(imagepath.get(i), coursename.get(i), total.get(i),courseId.get(i));
            collect.add(coursedata);
        }
        return collect;
    }

    public NoteListData getNoteListData(int id)
    {
        ArrayList<String> contentstr;
        ArrayList<String> coursetitlestr;
        ArrayList<String> coursestr;
        ArrayList<Integer> lessonid;
        for(int i=0;i<courseId.size();i++)
        {
            if(courseId.get(i) == id)
            {
                contentstr = content.get(i);
                coursetitlestr = coursetitle.get(i);
                coursestr = course.get(i);
                lessonid = lessonId.get(i);
                System.out.println("lessonid"+lessonid.toString());
                Log.i(null,contentstr.size()+"");
                return new NoteListData(coursestr,coursetitlestr,contentstr,lessonid);
            }
        }
        return null;
    }
}
