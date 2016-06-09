package com.rspn.plainnotetaker;

import android.content.Context;
import android.widget.TextView;

import com.rspn.plainnotetaker.database.NoteDataSource;
import com.rspn.plainnotetaker.model.Note;

public class MainTextWatcher extends EditTextWatcher {

    public MainTextWatcher(Note note, TextView editText, Context context, NoteDataSource dataSource) {
        super(note, editText, context, dataSource);
    }

    @Override
    protected void updateField(CharSequence charSequence) {
        note.setText(charSequence.toString());
    }
}
