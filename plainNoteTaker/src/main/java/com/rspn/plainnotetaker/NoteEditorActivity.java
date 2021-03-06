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

import com.rspn.plainnotetaker.database.NoteDataSource;
import com.rspn.plainnotetaker.model.Note;

import java.util.Calendar;

public class NoteEditorActivity extends AppCompatActivity {

    private Note note;
    private NoteDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        Intent intent = this.getIntent();
        note = new Note();
        note.setId(intent.getLongExtra("id", 0L));
        note.setText(intent.getStringExtra("text"));
        dataSource = new NoteDataSource(this);
        dataSource.open();
        TextView autosaved_tv = (TextView) findViewById(R.id.textView_editedTime);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setCustomView(R.layout.note_editor_actionbar);
            EditText noteTitle_edit = (EditText) supportActionBar.getCustomView().findViewById(R.id.noteTitle);
            noteTitle_edit.addTextChangedListener(new TitleTextWatcher(note, autosaved_tv, this, dataSource));

            supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EditText editText = (EditText) findViewById(R.id.noteText);

        editText.setText(note.getText());
        editText.setSelection(note.getText().length());
        editText.addTextChangedListener(new MainTextWatcher(note, autosaved_tv, this, dataSource));
    }

    private void saveAndFinish() {
        setResult(RESULT_OK);
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
