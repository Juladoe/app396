package com.edusoho.kuozhi.v3.model.bal;

/**
 * Created by Melomelon on 2015/9/10.
 */
public class DiscussionGroup {

    public DiscussionGroup(String groupName) {
        this.groupName = groupName;
    }

    public String groupName;

    private String sortLetter;

    public String getSortLetters() {
        return sortLetter;
    }

    public void setSortLetters(String letter) {
        this.sortLetter = letter;
    }
}
