package com.kingofnothing.mylectures;

public class GridViewItem {
    private String tagName;
    private String tagHref;
    private int tagImageResourceID;

    public GridViewItem(String tagName, String tagHref, int tagImageResourceID) {
        this.tagName = tagName;
        this.tagHref = tagHref;
        this.tagImageResourceID = tagImageResourceID;
    }
    public GridViewItem() {

    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagHref() {
        return tagHref;
    }

    public void setTagHref(String tagHref) {
        this.tagHref = tagHref;
    }

    public int getTagImageResourceID() {
        return tagImageResourceID;
    }

    public void setTagImageResourceID(int tagImageResourceID) {
        this.tagImageResourceID = tagImageResourceID;
    }
}
