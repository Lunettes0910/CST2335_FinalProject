package com.example.cst2335_finalproject;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** This class implements a SQLiteOpenHelper opens the dictionary's history database
 * @author Minh Tran
 * @version 1.0
 * @since April 16, 2018
 * @see android.database.sqlite.SQLiteOpenHelper
 */
public class MWDictHistoryDatabaseOpenHelper extends SQLiteOpenHelper {
    /** Default database name */
    public static final String DATABASE_NAME = "HistoryDatabase";

    /** Default database version number */
    public static final int VERSION_NUM = 1;

    /** Default table name in the database */
    public static final String TABLE_NAME = "SearchedWords";

    /** Default column name for the searched words */
    public static final String COL_WORD = "Word";

    /** Default column name for the searched words' syllables */
    public static final String COL_SYLLABLES = "Syllables";

    /** Default column name for the searched words' pronunciation */
    public static final String COL_PRONUNCIATION = "Pronunciation";

    /** Default column name for the searched words' type (i.e. noun, verb, etc.) */
    public static final String COL_WORD_TYPE = "WordType";

    /** Default column name for the searched words' definition */
    public static final String COL_DEFINITION = "Definition";

    /** Default constructor that starts the database */
    public MWDictHistoryDatabaseOpenHelper(Activity ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /** This method create a new table for history database.
     * @param db The database to store the table
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_WORD + " TEXT, "
                + COL_SYLLABLES + " TEXT, "
                + COL_PRONUNCIATION + " TEXT, "
                + COL_WORD_TYPE + " TEXT, "
                + COL_DEFINITION + " TEXT)");
    }

    /** This method upgrades the database.
     * @param db The database to be upgraded
     * @param oldVersion The database's old version number
     * @param newVersion The required new version number (greater than oldVersion)
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    /** This method upgrades the database.
     * @param db The database to be downgraded
     * @param oldVersion The database's old version number
     * @param newVersion The required new version number (less than oldVersion)
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }
}