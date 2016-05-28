package com.rspn.plainnotetaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.rspn.plainnotetaker.data.NoteItem;
import com.rspn.plainnotetaker.database.NoteItemDataSource;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int EDITOR_ACTIVITY_REQUEST = 1001;
    private TextView gettingStarted_tv;
    private DragListView dragListView;
    private ArrayList<Pair<Long, NoteItem>> mItemArray;
    private NoteItemDataSource noteItemDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dragListView = (DragListView) findViewById(R.id.drag_list_view);
        dragListView.setDragListListener(new NoteItemDragListListenerAdapter());

        dragListView.setLayoutManager(new LinearLayoutManager(this));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.image, this);
        dragListView.setAdapter(listAdapter, true);
        dragListView.setCanDragHorizontally(false);
        gettingStarted_tv = (TextView) findViewById(R.id.textView_gettingStarted);
        FloatingActionButton plus_fb = (FloatingActionButton) findViewById(R.id.fab);
        plus_fb.setOnClickListener(this);

        noteItemDataSource = new NoteItemDataSource(this);
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
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.image, this);
        dragListView.setAdapter(listAdapter, true);
        dragListView.setCanDragHorizontally(false);
        dragListView.setCustomDragItem(new MyDragItem(this, R.layout.list_item));
    }

    public void refreshDisplay() {
        noteItemDataSource.open();
        if (!noteItemDataSource.isEmpty()) {
            gettingStarted_tv.setVisibility(View.GONE);
        }
        List<NoteItem> notesList = noteItemDataSource.getAllNoteItems();
        mItemArray = new ArrayList<>();

        for (NoteItem noteItem : notesList) {
            mItemArray.add(new Pair<>(noteItem.getId(), noteItem));
        }
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.image, this);
        dragListView.setAdapter(listAdapter, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            NoteItem note = new NoteItem();
            note.setId(data.getLongExtra("id", 0L));
            note.setText(data.getStringExtra("text"));
            refreshDisplay();
        }
    }

    @Override
    public void onClick(View v) {
        NoteItem note = NoteItem.newInstance();
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("id", note.getId());
        intent.putExtra("text", note.getText());
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }

    @Override
    protected void onResume() {
        noteItemDataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        noteItemDataSource.close();
        super.onPause();
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

    public class NoteItemDragListListenerAdapter implements DragListView.DragListListener {
        private NoteItem currentNoteItem;

        @Override
        public void onItemDragStarted(int position) {
            currentNoteItem = noteItemDataSource.getNoteByDisplayPosition(position);
        }

        @Override
        public void onItemDragging(int itemPosition, float x, float y) {
        }

        @Override
        public void onItemDragEnded(int fromPosition, int toPosition) {
            if (fromPosition != toPosition) {
                noteItemDataSource.updateNoteDisplayPosition(currentNoteItem, fromPosition, toPosition);
            }
        }
    }
}
