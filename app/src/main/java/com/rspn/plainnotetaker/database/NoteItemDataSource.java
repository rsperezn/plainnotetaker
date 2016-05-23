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

public class NoteItemDataSource {

    private final NoteItemDBOpenHelper dbHelper;
    private final String[] allColumns = {
            NoteItemDBOpenHelper.COLUMN_NOTE_ID,
            NoteItemDBOpenHelper.COLUMN_TITLE,
            NoteItemDBOpenHelper.COLUMN_TEXT,
            NoteItemDBOpenHelper.COLUMN_DISPLAY_ORDER,
            NoteItemDBOpenHelper.COLUMN_COLOR,
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
        values.put(NoteItemDBOpenHelper.COLUMN_NOTE_ID, noteItem.getId());
        values.put(NoteItemDBOpenHelper.COLUMN_TITLE, "sample title");
        values.put(NoteItemDBOpenHelper.COLUMN_TEXT, noteItem.getText());
        values.put(NoteItemDBOpenHelper.COLUMN_DISPLAY_ORDER, 0L);
        values.put(NoteItemDBOpenHelper.COLUMN_COLOR, "#OFFFFF");

        long insertId = database.insert(
                NoteItemDBOpenHelper.TABLE_NOTES,
                null,
                values);
        Cursor cursor = database.query(
                NoteItemDBOpenHelper.TABLE_NOTES,
                allColumns,
                NoteItemDBOpenHelper.COLUMN_NOTE_ID + " = " + insertId,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        cursor.close();
    }

    public void deleteNoteItem(long noteId) {
        database.delete(
                NoteItemDBOpenHelper.TABLE_NOTES,
                NoteItemDBOpenHelper.COLUMN_NOTE_ID + " = " + noteId,
                null);
    }

    public List<NoteItem> getAllNoteItems() {
        List<NoteItem> noteItems = new ArrayList<>();

        Cursor cursor = database.query(
                NoteItemDBOpenHelper.TABLE_NOTES,
                allColumns,
                null,
                null,
                null,
                null,
                null);

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
        noteItem.setKey(cursor.getLong(0));
        noteItem.setText(cursor.getString(2));
        return noteItem;
    }

    public void createOrUpdate(NoteItem note) {
        ContentValues cv = new ContentValues();
        cv.put(NoteItemDBOpenHelper.COLUMN_TEXT, note.getText()); //These Fields should be your String values of actual column names
        int rowsAffected = database.update(NoteItemDBOpenHelper.TABLE_NOTES, cv, "noteId=" + note.getId(), null);
        if (rowsAffected == 0) {
            createNote(note);
        }
    }

    public boolean isEmpty() {
        return DatabaseUtils.queryNumEntries(database, NoteItemDBOpenHelper.TABLE_NOTES) == 0L;
    }

    public NoteItem getNoteItemText(long noteId) {
        NoteItem noteItem = null;
        String selectQuery = "SELECT * FROM " + NoteItemDBOpenHelper.TABLE_NOTES
                + " WHERE " + NoteItemDBOpenHelper.COLUMN_NOTE_ID + " =?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(noteId)});
        if (cursor.moveToFirst()) {
            noteItem = cursorToNoteItem(cursor);
        }
        cursor.close();
        return noteItem;
    }
}


