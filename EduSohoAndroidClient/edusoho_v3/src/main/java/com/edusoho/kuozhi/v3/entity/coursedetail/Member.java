package com.edusoho.kuozhi.v3.entity.coursedetail;

import java.io.Serializable;

/**
 * Created by Zhang on 2016/12/13.
 */

public class Member implements Serializable {
    private String id;
    private String courseId;
    private String classroomId;
    private String joinedType;
    private String userId;
    private String orderId;
    private String deadline;
    private String levelId;
    private String learnedNum;
    private String credit;
    private String noteNum;
    private String noteLastUpdateTime;
    private String isLearned;
    private String finishedTime;
    private String seq;
    private String remark;
    private String isVisible;
    private String role;
    private String locked;
    private String deadlineNotified;
    private String createdTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getJoinedType() {
        return joinedType;
    }

    public void setJoinedType(String joinedType) {
        this.joinedType = joinedType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getLearnedNum() {
        return learnedNum;
    }

    public void setLearnedNum(String learnedNum) {
        this.learnedNum = learnedNum;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getNoteNum() {
        return noteNum;
    }

    public void setNoteNum(String noteNum) {
        this.noteNum = noteNum;
    }

    public String getNoteLastUpdateTime() {
        return noteLastUpdateTime;
    }

    public void setNoteLastUpdateTime(String noteLastUpdateTime) {
        this.noteLastUpdateTime = noteLastUpdateTime;
    }

    public String getIsLearned() {
        return isLearned;
    }

    public void setIsLearned(String isLearned) {
        this.isLearned = isLearned;
    }

    public String getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(String finishedTime) {
        this.finishedTime = finishedTime;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getDeadlineNotified() {
        return deadlineNotified;
    }

    public void setDeadlineNotified(String deadlineNotified) {
        this.deadlineNotified = deadlineNotified;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

}
