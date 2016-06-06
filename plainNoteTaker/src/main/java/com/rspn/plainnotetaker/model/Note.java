package com.rspn.plainnotetaker.model;

import java.util.Date;

public class Note {

    private long id;
    private String text;
    private int displayOrder;

    public static Note newInstance() {
        Note note = new Note();
        note.setId(new Date().getTime());
        note.setText("");
        return note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
