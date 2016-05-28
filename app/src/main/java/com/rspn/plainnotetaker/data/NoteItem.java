package com.rspn.plainnotetaker.data;

import android.annotation.SuppressLint;

import java.util.Date;

public class NoteItem {

    private long id;
    private String text;
    private int displayPosition;

    public static NoteItem newInstance() {
        NoteItem note = new NoteItem();
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

    public int getDisplayPosition() {
        return displayPosition;
    }

    public void setDisplayPosition(int displayPosition) {
        this.displayPosition = displayPosition;
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
