package com.example.android.popularmovies1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies1.data.MovieContract.MovieEntry;

/**
 * Created by merouane on 18/01/2018.
 */

public class MovieDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MOVIES_TABLE =  "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("
                + MovieEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieEntry.COLUMN_IMAGE_URL + " TEXT, "
                + MovieEntry.COLUMN_TITLE + " TEXT, "
                + MovieEntry.COLUMN_OVERVIEW + " TEXT, "
                + MovieEntry.COLUMN_USER_RATING + " TEXT, "
                + MovieEntry.COLUMN_RELEASE_DATE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // won't do anything here
    }
}