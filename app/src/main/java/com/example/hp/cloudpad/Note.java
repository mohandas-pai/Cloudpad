package com.example.hp.cloudpad;

/**
 * Created by hp on 30-09-2017.
 */

public class Note {
    private String title,desc,datetime;

    public Note(){

    }

    public Note(String title, String desc, String datetime) {
        this.title = title;
        this.desc = desc;
        this.datetime=datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
