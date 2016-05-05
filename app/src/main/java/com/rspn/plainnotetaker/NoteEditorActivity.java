package com.rspn.plainnotetaker;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.rspn.plainnotetaker.data.NoteItem;
import com.rspn.plainnotetaker.data.NotesDataSource;

import java.util.Calendar;

public class NoteEditorActivity extends Activity {

    private NoteItem note;
    private NotesDataSource dataSource;
    private TextView edited_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            ActionBar supportActionBar = getActionBar();
            if (supportActionBar != null) {
                getActionBar().setDisplayShowHomeEnabled(true);
                getActionBar().setLogo(R.drawable.ic_launcher);
                getActionBar().setDisplayUseLogoEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        dataSource = new NotesDataSource(this);
        edited_tv = (TextView) findViewById(R.id.textView_editedTime);
        Intent intent = this.getIntent();
        note = new NoteItem();
        note.setKey(intent.getStringExtra("key"));
        note.setText(intent.getStringExtra("text"));

        EditText editText = (EditText) findViewById(R.id.noteText);

        editText.setText(note.getText());
        editText.setSelection(note.getText().length());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                note.setText(charSequence.toString());
                dataSource.update(note);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Calendar calendar = Calendar.getInstance();
                edited_tv.setText(getString(
                        R.string.autosaved,
                        calendar.get(Calendar.HOUR),
                        calendar.get(Calendar.MINUTE),
                        calendar.get(Calendar.AM_PM) == 1 ? "PM" : "AM"));
            }
        });
    }

    private void saveAndFinish() {
        EditText et = (EditText) findViewById(R.id.noteText);
        String noteText = et.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("key", note.getKey());
        intent.putExtra("text", noteText);
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveAndFinish();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        saveAndFinish();
    }

}
