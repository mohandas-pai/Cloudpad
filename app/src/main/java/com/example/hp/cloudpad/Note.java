package com.example.hp.cloudpad;

/**
 * Created by hp on 30-09-2017.
 */

public class Note {
    private String title,desc;

    public Note(){

    }

    public Note(String title, String desc) {
        this.title = title;
        this.desc = desc;
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
}
