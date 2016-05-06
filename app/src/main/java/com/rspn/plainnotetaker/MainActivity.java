package com.rspn.plainnotetaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rspn.plainnotetaker.data.NoteItem;
import com.rspn.plainnotetaker.data.NotesDataSource;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int EDITOR_ACTIVITY_REQUEST = 1001;
    private static final int MENU_DELETE_ID = 1002;
    private static final int MENU_SHARE_NOTE = 1003;
    private int currentNoteId;
    private NotesDataSource dataSource;
    private List<NoteItem> notesList;
    private TextView gettingStarted_tv;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView_allNotes);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
        gettingStarted_tv = (TextView) findViewById(R.id.textView_gettingStarted);
        dataSource = new NotesDataSource(this);
        FloatingActionButton plus_fb = (FloatingActionButton) findViewById(R.id.fab);
        plus_fb.setOnClickListener(this);

        dataSource = new NotesDataSource(this);
        refreshDisplay();
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
    }

    private void refreshDisplay() {
        notesList = dataSource.findAll();
        ArrayAdapter<NoteItem> adapter =
                new ArrayAdapter<>(this, R.layout.list_item_layout, notesList);
        listView.setAdapter(adapter);
        if (!notesList.isEmpty()) {
            gettingStarted_tv.setVisibility(View.GONE);
        }
    }

    private void createNote() {
        NoteItem note = NoteItem.getNew();
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("key", note.getKey());
        intent.putExtra("text", note.getText());
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            NoteItem note = new NoteItem();
            note.setKey(data.getStringExtra("key"));
            note.setText(data.getStringExtra("text"));
            dataSource.update(note);
            refreshDisplay();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        currentNoteId = (int) info.id;
        menu.add(0, MENU_DELETE_ID, 0, "Delete");
        menu.add(1, MENU_SHARE_NOTE, 1, "Share Note");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        NoteItem note = notesList.get(currentNoteId);

        if (item.getItemId() == MENU_DELETE_ID) {
            dataSource.remove(note);
            refreshDisplay();
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, note.getText());
            startActivity(intent);
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        createNote();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NoteItem note = notesList.get(position);
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("key", note.getKey());
        intent.putExtra("text", note.getText());
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }
}
