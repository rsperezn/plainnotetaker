package com.rspn.plainnotetaker;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.rspn.plainnotetaker.database.NoteDataSource;
import com.rspn.plainnotetaker.model.Note;

import java.util.Calendar;

public abstract class EditTextWatcher implements TextWatcher {

    protected Note note;
    private TextView editText;
    private Context context;
    private NoteDataSource dataSource;

    public EditTextWatcher(Note note, TextView editText, Context context, NoteDataSource dataSource) {
        this.editText = editText;
        this.context = context;
        this.dataSource = dataSource;
        this.note = note;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        updateField(charSequence);
        dataSource.createOrUpdate(note);
    }

    @Override
    public void afterTextChanged(Editable s) {
        Calendar calendar = Calendar.getInstance();
        editText.setText(getString(calendar));
    }

    private String getString(Calendar calendar) {
        int minute = calendar.get(Calendar.MINUTE);
        String formattedMinutes = minute > 9 ? Integer.toString(minute) : "0" + Integer.toString(minute);
        return context.getString(
                R.string.autosaved,
                calendar.get(Calendar.HOUR_OF_DAY),
                formattedMinutes);
    }

    protected abstract void updateField(CharSequence charSequence);

}


