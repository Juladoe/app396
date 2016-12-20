package com.edusoho.kuozhi.v3.entity.lesson;

import com.edusoho.kuozhi.v3.model.bal.Classroom;

import java.util.ArrayList;

/**
 * Created by DF on 2016/12/18.
 */
public class ClassCatalogue {

    public ArrayList<Classroom> list;
    public ArrayList<Classroom> getList(){return list;}
    public void setList(ArrayList<Classroom> list){
        this.list = list;
    }
}
