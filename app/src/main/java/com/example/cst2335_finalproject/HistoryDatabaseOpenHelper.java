package com.example.cst2335_finalproject;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HistoryDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "HistoryDatabase";
    public static final int VERSION_NUM = 1;
    public static final String TABLE_NAME = "Searched Words";
    public static final String COL_WORD = "Word";
    public static final String COL_SYLLABLES = "Syllables";
    public static final String COL_PRONUNCIATION = "Pronunciation";
    public static final String COL_WORD_TYPE = "Word type";
    public static final String COL_DEFINITION = "Definition";

    public HistoryDatabaseOpenHelper(Activity ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_WORD + " TEXT, "
                + COL_SYLLABLES + " TEXT, "
                + COL_PRONUNCIATION + " TEXT, "
                + COL_WORD_TYPE + " TEXT, "
                + COL_DEFINITION + " TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }
}