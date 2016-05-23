package com.rspn.plainnotetaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.rspn.plainnotetaker.data.NoteItem;
import com.rspn.plainnotetaker.database.NoteItemDataSource;

import java.util.Calendar;

public class NoteEditorActivity extends AppCompatActivity {

    private NoteItem note;
    private NoteItemDataSource dataSource;
    private TextView edited_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        dataSource = new NoteItemDataSource(this);
        dataSource.open();
        edited_tv = (TextView) findViewById(R.id.textView_editedTime);
        Intent intent = this.getIntent();
        note = new NoteItem();
        note.setKey(intent.getLongExtra("key", 0L));
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

                edited_tv.setText(getString(calendar));
            }
        });
    }

    private String getString(Calendar calendar) {
        int minute = calendar.get(Calendar.MINUTE);
        String formattedMinutes = minute > 9 ? Integer.toString(minute) : "0" + Integer.toString(minute);
        return getString(
                R.string.autosaved,
                calendar.get(Calendar.HOUR),
                formattedMinutes,
                calendar.get(Calendar.AM_PM) == 1 ? "PM" : "AM");
    }

    private void saveAndFinish() {
        EditText et = (EditText) findViewById(R.id.noteText);
        String noteText = et.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("key", note.getId());
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

    @Override
    protected void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}
