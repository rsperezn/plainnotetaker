package com.rspn.plainnotetaker.data;

import android.annotation.SuppressLint;

import java.util.Date;

public class NoteItem {

    private long key;
    private String text;

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @SuppressLint("SimpleDateFormat")
    public static NoteItem newInstance() {
        NoteItem note = new NoteItem();
        note.setKey(new Date().getTime());
        note.setText("");
        return note;

    }

    @Override
    public String toString() {
        return this.getText();
    }

}
