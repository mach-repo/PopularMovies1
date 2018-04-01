package com.example.android.popularmovies1.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by merouane on 18/01/2018.
 */

public class MovieContract {

    private MovieContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies1";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public final static String TABLE_NAME = "movie";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_IMAGE_URL ="image_url";

        public final static String COLUMN_TITLE = "title";

        public final static String COLUMN_OVERVIEW = "overview";

        public final static String COLUMN_USER_RATING = "user_rating";

        public final static String COLUMN_RELEASE_DATE = "release_date";
    }
}

