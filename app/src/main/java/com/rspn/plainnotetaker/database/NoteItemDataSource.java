package com.rspn.plainnotetaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.rspn.plainnotetaker.data.Note;

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

    private void createNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_ID, note.getId());
        values.put(COLUMN_TITLE, "sample title");
        values.put(COLUMN_TEXT, note.getText());
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

    public List<Note> getAllNoteItems() {
        List<Note> notes = new ArrayList<>();

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
            Note note = cursorToNoteItem(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return notes;
    }

    private Note cursorToNoteItem(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setText(cursor.getString(2));
        note.setDisplayPosition(cursor.getInt(3));
        return note;
    }

    public void createOrUpdate(Note note) {
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

    public Note getNoteTextById(long noteId) {
        Note note = null;
        String selectQuery = "SELECT * FROM " + TABLE_NOTES
                + " WHERE " + COLUMN_NOTE_ID + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(noteId)});
        if (cursor.moveToFirst()) {
            note = cursorToNoteItem(cursor);
        }
        cursor.close();
        return note;
    }

    public Note getNoteByDisplayPosition(int displayPosition) {
        Note note = null;
        String selectQuery = "SELECT * FROM " + TABLE_NOTES
                + " WHERE " + COLUMN_DISPLAY_POSITION + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(displayPosition)});
        if (cursor.moveToFirst()) {
            note = cursorToNoteItem(cursor);
        }
        cursor.close();
        return note;
    }

    public void updateNoteDisplayPosition(Note currentNote, int fromPosition, int toPosition) {
        updateDraggedNoteDisplayPosition(currentNote.getDisplayPosition(), toPosition);
        updateRemainingNoteDisplayPosition(currentNote.getId(), fromPosition, toPosition);
    }

    private void updateDraggedNoteDisplayPosition(int currentDisplayPosition, int newDisplayPosition) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DISPLAY_POSITION, newDisplayPosition);
        database.update(TABLE_NOTES, cv,
                COLUMN_DISPLAY_POSITION + " = " + currentDisplayPosition, null);
    }

    private void updateRemainingNoteDisplayPosition(long excludedId, int fromPosition, int toPosition) {
        long deltaDisplayPosition;
        List<Note> noteItemsToUpdate = new ArrayList<>();
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
            Note note = cursorToNoteItem(cursor);
            noteItemsToUpdate.add(note);
            cursor.moveToNext();
        }
        cursor.close();

        ContentValues contentValues;
        for (Note note : noteItemsToUpdate) {
            contentValues = new ContentValues();
            long newPosition = note.getDisplayPosition() + deltaDisplayPosition;
            contentValues.put(COLUMN_DISPLAY_POSITION, newPosition);
            database.update(TABLE_NOTES, contentValues, COLUMN_NOTE_ID + " = " + note.getId(), null);
        }
    }
}


