package com.cs360.project2.data;
/*
 * SQLiteOpenHelper defining two persistent tables:
 *  - users(username TEXT PRIMARY KEY, password TEXT NOT NULL)
 *  - inventory(name TEXT PRIMARY KEY, quantity INTEGER NOT NULL DEFAULT 0, last_updated TEXT)
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "cs360_app.db";
    private static final int DB_VERSION = 1;

    // Tables & columns
    public static final String TBL_USERS = "users";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    public static final String TBL_INVENTORY = "inventory";
    // unique key
    public static final String COL_ITEM_NAME = "name";
    public static final String COL_QTY = "quantity";
    public static final String COL_UPDATED = "last_updated";

    public DatabaseHelper(Context ctx) { super(ctx, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        // Create USERS table.
        db.execSQL("CREATE TABLE " + TBL_USERS + " (" +
                COL_USERNAME + " TEXT PRIMARY KEY," +
                COL_PASSWORD + " TEXT NOT NULL)");

        // Create INVENTORY table.
        db.execSQL("CREATE TABLE " + TBL_INVENTORY + " (" +
                COL_ITEM_NAME + " TEXT PRIMARY KEY," +
                COL_QTY + " INTEGER NOT NULL DEFAULT 0," +
                COL_UPDATED + " TEXT)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_INVENTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_USERS);
        onCreate(db);
    }
}
