package com.example.hassan.movies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.HashMap;

public class MoviesProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.example.hassan.movies.MoviesProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/favouriteMovies";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String NAME = "name";

    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;

    static final int MOVIE = 1;
    static final int MOVIE_ID = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "favouriteMovies", MOVIE);
        uriMatcher.addURI(PROVIDER_NAME, "favouriteMovies/#", MOVIE_ID);
    }


    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Movies";
    static final String STUDENTS_TABLE_NAME = "favouriteMovies";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + STUDENTS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY, " +
                    " name TEXT NOT NULL);";


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);


        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = db.insert(STUDENTS_TABLE_NAME, "", values);


        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(STUDENTS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case MOVIE:
                qb.setProjectionMap(STUDENTS_PROJECTION_MAP);
                break;

            case MOVIE_ID:
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == "") {

            sortOrder = NAME;
        }

        Cursor c = qb.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                count = db.delete(STUDENTS_TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE_ID:


                count = db.delete(STUDENTS_TABLE_NAME, _ID + " = " + selectionArgs +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {

            case MOVIE:
                return "vnd.android.cursor.dir/vnd.example.favouriteMovies";

            case MOVIE_ID:
                return "vnd.android.cursor.item/vnd.example.favouriteMovies";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}