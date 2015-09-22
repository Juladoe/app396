package com.edusoho.kuozhi.v3.model.bal;

/**
 * Created by Melomelon on 2015/9/10.
 */
public class DiscussionGroup {

    public String type;
    public String id;
    public String title;
    public String picture;

    private String sortLetter;

    public String getSortLetters() {
        return sortLetter;
    }

    public void setSortLetters(String letter) {
        this.sortLetter = letter;
    }
}
