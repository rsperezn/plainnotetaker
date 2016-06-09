package com.rspn.plainnotetaker.uihelper;

import com.rspn.plainnotetaker.database.NoteDataSource;
import com.rspn.plainnotetaker.model.Note;
import com.woxthebox.draglistview.DragListView;

public class NoteDragListListenerAdapter implements DragListView.DragListListener {
    private Note currentNote;
    private NoteDataSource dataSource;

    public NoteDragListListenerAdapter(NoteDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onItemDragStarted(int position) {
        currentNote = dataSource.getNoteByDisplayPosition(position);
    }

    @Override
    public void onItemDragging(int itemPosition, float x, float y) {
    }

    @Override
    public void onItemDragEnded(int fromPosition, int toPosition) {
        if (fromPosition != toPosition) {
            dataSource.updateNoteDisplayPosition(currentNote, fromPosition, toPosition);
        }
    }
}