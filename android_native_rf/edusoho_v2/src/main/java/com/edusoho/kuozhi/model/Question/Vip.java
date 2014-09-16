package com.edusoho.kuozhi.model.Question;

import java.io.Serializable;

/**
 * Created by hby on 14-9-15.
 */
public class Vip implements Serializable {
    public int id;
    public int userId;
    public int levelId;
    public String deadline;
    public String boughtType;
    public String boughtTime;
    public int boughtDuration;
    public String boughtUnit;
    public float boughtAmount;
    public int orderId;
    public int operatorId;
    public String createdTime;
}
