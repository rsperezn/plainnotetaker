package com.rspn.plainnotetaker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoteItemDBOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_NOTES = "note";
    public static final String COLUMN_NOTE_ID = "noteId";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TEXT = "noteText";
    public static final String COLUMN_DISPLAY_POSITION = "displayOrder";
    public static final String COLUMN_COLOR = "color";

    private static final String DATABASE_NAME = "note_db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTES + "("
            + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TITLE + " TEXT, "
            + COLUMN_TEXT + " TEXT, "
            + COLUMN_DISPLAY_POSITION + " INTEGER, "
            + COLUMN_COLOR + " TEXT"
            + " );";

    public NoteItemDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(NoteItemDBOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

}
