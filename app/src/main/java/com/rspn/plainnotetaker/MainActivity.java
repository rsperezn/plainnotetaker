package com.rspn.plainnotetaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rspn.plainnotetaker.data.NoteItem;
import com.rspn.plainnotetaker.data.NotesDataSource;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
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
    private DragListView dragListView;
    private ArrayList<Pair<Long, NoteItem>> mItemArray;
    private ItemAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dragListView = (DragListView) findViewById(R.id.drag_list_view);
        dragListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {
                Toast.makeText(dragListView.getContext(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {

            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    Toast.makeText(dragListView.getContext(), "End - position: " + toPosition, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dragListView.setLayoutManager(new LinearLayoutManager(this));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.image, false);
        dragListView.setAdapter(listAdapter,true);
        dragListView.setCanDragHorizontally(false);
//        listView = (ListView) findViewById(R.id.listView_allNotes);
//        listView.setOnItemClickListener(this);
//        registerForContextMenu(listView);
        gettingStarted_tv = (TextView) findViewById(R.id.textView_gettingStarted);
        dataSource = new NotesDataSource(this);
        FloatingActionButton plus_fb = (FloatingActionButton) findViewById(R.id.fab);
        plus_fb.setOnClickListener(this);

        dataSource = new NotesDataSource(this);
        setActionBarLogo();
        refreshDisplay();
        setupListRecyclerView();
    }

    private void setActionBarLogo() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
    }

    private void setupListRecyclerView() {
        dragListView.setLayoutManager(new LinearLayoutManager(this));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.image, false);
        dragListView.setAdapter(listAdapter, true);
        dragListView.setCanDragHorizontally(false);
        dragListView.setCustomDragItem(new MyDragItem(this, R.layout.list_item));
    }

    private void refreshDisplay() {
        if (!dataSource.isEmpty()) {
            gettingStarted_tv.setVisibility(View.GONE);
        }
        notesList = dataSource.findAll();
        mItemArray = new ArrayList<>();

        for (NoteItem noteItem : notesList) {
            mItemArray.add(new Pair<>(noteItem.getKey(),noteItem));
        }
        listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.image, false);
        dragListView.setAdapter(listAdapter, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            NoteItem note = new NoteItem();
            note.setKey(data.getLongExtra("key",0L));
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
        NoteItem note = NoteItem.newInstance();
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("key", note.getKey());
        intent.putExtra("text", note.getText());
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NoteItem note = notesList.get(position);
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("key", note.getKey());
        intent.putExtra("text", note.getText());
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }

    public static abstract class DragListListenerAdapter implements DragListView.DragListListener {
        @Override
        public void onItemDragStarted(int position) {
        }

        @Override
        public void onItemDragging(int itemPosition, float x, float y) {
        }

        @Override
        public void onItemDragEnded(int fromPosition, int toPosition) {
        }
    }

    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.list_item_background));
        }
    }
}
