package com.cs360.project2.data;
/*
 * Small DAO for user + inventory CRUD backed by SQLite.
 * Activities call into this class; it owns the DatabaseHelper instance.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cs360.project2.model.InventoryItem;

import java.util.ArrayList;
import java.util.List;


public class AppRepository {

    private final DatabaseHelper helper;

    public AppRepository(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
    }

    /* ---------- Users ---------- */

    /** Creates a user; returns false if username already exists or insert fails. */
    public boolean createUser(String username, String password) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_USERNAME, username.trim());
        cv.put(DatabaseHelper.COL_PASSWORD, password);
        long row = db.insert(DatabaseHelper.TBL_USERS, null, cv);
        return row != -1;
    }

    /** Validates username/password against DB. */
    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.query(
                DatabaseHelper.TBL_USERS,
                new String[]{DatabaseHelper.COL_USERNAME},
                DatabaseHelper.COL_USERNAME + "=? AND " + DatabaseHelper.COL_PASSWORD + "=?",
                new String[]{username.trim(), password},
                null, null, null)) {
            return c.moveToFirst();
        }
    }

    /* ---------- Inventory ---------- */

    /** Returns all items ordered by name (case-insensitive). */
    public List<InventoryItem> getAllItems() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<InventoryItem> out = new ArrayList<>();
        try (Cursor c = db.query(
                DatabaseHelper.TBL_INVENTORY,
                null, null, null, null, null,
                DatabaseHelper.COL_ITEM_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_NAME));
                int qty     = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_QTY));
                String date = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_UPDATED));
                out.add(new InventoryItem(name, qty, date));
            }
        }
        return out;
    }

    /** Adds a new item; returns false if name already exists or insert fails. */
    public boolean addItem(String name, int qty, String updated) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_ITEM_NAME, name.trim());
        cv.put(DatabaseHelper.COL_QTY, qty);
        cv.put(DatabaseHelper.COL_UPDATED, updated);
        long row = db.insert(DatabaseHelper.TBL_INVENTORY, null, cv);
        return row != -1;
    }

    /** Updates quantity + date for an item by name. */
    public void updateQtyByName(String name, int qty, String updated) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_QTY, qty);
        cv.put(DatabaseHelper.COL_UPDATED, updated);
        // We don't care about the affected-row count here (UI already updated optimistically).
        db.update(
                DatabaseHelper.TBL_INVENTORY,
                cv,
                DatabaseHelper.COL_ITEM_NAME + "=?",
                new String[]{name.trim()}
        );
    }

    /** Deletes an item by name. */
    public void deleteByName(String name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(
                DatabaseHelper.TBL_INVENTORY,
                DatabaseHelper.COL_ITEM_NAME + "=?",
                new String[]{name.trim()}
        );
    }
}
