package com.rspn.plainnotetaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.rspn.plainnotetaker.data.NoteItem;

import java.util.ArrayList;
import java.util.List;

import static com.rspn.plainnotetaker.database.NoteItemDBOpenHelper.COLUMN_COLOR;
import static com.rspn.plainnotetaker.database.NoteItemDBOpenHelper.COLUMN_DISPLAY_POSITION;
import static com.rspn.plainnotetaker.database.NoteItemDBOpenHelper.COLUMN_NOTE_ID;
import static com.rspn.plainnotetaker.database.NoteItemDBOpenHelper.COLUMN_TEXT;
import static com.rspn.plainnotetaker.database.NoteItemDBOpenHelper.COLUMN_TITLE;
import static com.rspn.plainnotetaker.database.NoteItemDBOpenHelper.TABLE_NOTES;

public class NoteItemDataSource {

    private final NoteItemDBOpenHelper dbHelper;
    private final String[] allColumns = {
            COLUMN_NOTE_ID,
            COLUMN_TITLE,
            COLUMN_TEXT,
            COLUMN_DISPLAY_POSITION,
            COLUMN_COLOR,
    };
    private SQLiteDatabase database;

    public NoteItemDataSource(Context context) {
        dbHelper = new NoteItemDBOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private void createNote(NoteItem noteItem) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_ID, noteItem.getId());
        values.put(COLUMN_TITLE, "sample title");
        values.put(COLUMN_TEXT, noteItem.getText());
        values.put(COLUMN_DISPLAY_POSITION, getDisplayPosition());
        values.put(COLUMN_COLOR, "#OFFFFF");

        long insertId = database.insert(
                TABLE_NOTES,
                null,
                values);
        Cursor cursor = database.query(
                TABLE_NOTES,
                allColumns,
                COLUMN_NOTE_ID + " = " + insertId,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        cursor.close();
    }

    private long getDisplayPosition() {
        long newPosition = isEmpty() ? 0 : DatabaseUtils.queryNumEntries(database, TABLE_NOTES);
        return newPosition;
    }

    public void deleteNoteItem(long noteId) {
        database.delete(
                TABLE_NOTES,
                COLUMN_NOTE_ID + " = " + noteId,
                null);
    }

    public List<NoteItem> getAllNoteItems() {
        List<NoteItem> noteItems = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NOTES,
                allColumns,
                null,
                null,
                null,
                null,
                COLUMN_DISPLAY_POSITION + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NoteItem noteItem = cursorToNoteItem(cursor);
            noteItems.add(noteItem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return noteItems;
    }

    private NoteItem cursorToNoteItem(Cursor cursor) {
        NoteItem noteItem = new NoteItem();
        noteItem.setId(cursor.getLong(0));
        noteItem.setText(cursor.getString(2));
        noteItem.setDisplayPosition(cursor.getInt(3));
        return noteItem;
    }

    public void createOrUpdate(NoteItem note) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TEXT, note.getText()); //These Fields should be your String values of actual column names
        int rowsAffected = database.update(TABLE_NOTES, cv, COLUMN_NOTE_ID + " = " + note.getId(), null);
        if (rowsAffected == 0) {
            createNote(note);
        }
    }

    public boolean isEmpty() {
        return DatabaseUtils.queryNumEntries(database, TABLE_NOTES) == 0L;
    }

    public NoteItem getNoteTextById(long noteId) {
        NoteItem noteItem = null;
        String selectQuery = "SELECT * FROM " + TABLE_NOTES
                + " WHERE " + COLUMN_NOTE_ID + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(noteId)});
        if (cursor.moveToFirst()) {
            noteItem = cursorToNoteItem(cursor);
        }
        cursor.close();
        return noteItem;
    }

    public NoteItem getNoteByDisplayPosition(int displayPosition) {
        NoteItem noteItem = null;
        String selectQuery = "SELECT * FROM " + TABLE_NOTES
                + " WHERE " + COLUMN_DISPLAY_POSITION + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(displayPosition)});
        if (cursor.moveToFirst()) {
            noteItem = cursorToNoteItem(cursor);
        }
        cursor.close();
        return noteItem;
    }

    public void updateNoteDisplayPosition(NoteItem currentNoteItem, int fromPosition, int toPosition) {
        updateDraggedNoteDisplayPosition(currentNoteItem.getDisplayPosition(), toPosition);
        updateRemainingNoteDisplayPosition(currentNoteItem.getId(), fromPosition, toPosition);
    }

    private void updateDraggedNoteDisplayPosition(int currentDisplayPosition, int newDisplayPosition) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DISPLAY_POSITION, newDisplayPosition);
        database.update(TABLE_NOTES, cv,
                COLUMN_DISPLAY_POSITION + " = " + currentDisplayPosition, null);
    }

    private void updateRemainingNoteDisplayPosition(long excludedId, int fromPosition, int toPosition) {
        long deltaDisplayPosition;
        List<NoteItem> noteItemsToUpdate = new ArrayList<>();
        String selectQuery;
        if (fromPosition < toPosition) {
            selectQuery = "SELECT * FROM " + TABLE_NOTES
                    + " WHERE " + COLUMN_NOTE_ID + " <> ?"
                    + " AND " + COLUMN_DISPLAY_POSITION + " <= ?"
                    + " AND " + COLUMN_DISPLAY_POSITION + " > ?";
            deltaDisplayPosition = -1L;
        } else {
            selectQuery = "SELECT * FROM " + TABLE_NOTES
                    + " WHERE " + COLUMN_NOTE_ID + " <> ?"
                    + " AND " + COLUMN_DISPLAY_POSITION + " >= ?"
                    + " AND " + COLUMN_DISPLAY_POSITION + " < ?";
            deltaDisplayPosition = 1L;
        }

        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(excludedId), String.valueOf(toPosition), String.valueOf(fromPosition)});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NoteItem noteItem = cursorToNoteItem(cursor);
            noteItemsToUpdate.add(noteItem);
            cursor.moveToNext();
        }
        cursor.close();

        ContentValues contentValues;
        for (NoteItem noteItem : noteItemsToUpdate) {
            contentValues = new ContentValues();
            long newPosition = noteItem.getDisplayPosition() + deltaDisplayPosition;
            contentValues.put(COLUMN_DISPLAY_POSITION, newPosition);
            database.update(TABLE_NOTES, contentValues, COLUMN_NOTE_ID + " = " + noteItem.getId(), null);
        }
    }
}


