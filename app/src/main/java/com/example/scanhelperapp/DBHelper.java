package com.example.scanhelperapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

// DB Helper Class
// TODO: Add methods to change/remove rows
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "items.db";
    private static final String TABLE_NAME = "items";
    private static final String COL_NAME = "name";
    private static final String COL_BARCODE = "barcode";
    private static final String COL_QUANTITY = "quantity";

    private static final int VERSION = 1;

    private static final String CREATE_QUERY = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER)",
            TABLE_NAME, BaseColumns._ID, COL_NAME, COL_BARCODE, COL_QUANTITY);

    private static final String DELETE_QUERY = String.format(
            "DROP TABLE IF EXISTS %s",
            TABLE_NAME);

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_QUERY);
        onCreate(db);
    }

    public long add(Item item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_BARCODE, item.getBarcode());
        values.put(COL_QUANTITY, item.getQuantity());

        return db.insert(TABLE_NAME, null, values);
    }

    public ArrayList<Item> selectByCode(String barcode) {
        SQLiteDatabase db = getWritableDatabase();
        final String query = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_NAME, COL_BARCODE);
        Cursor cursor = db.rawQuery(query, new String[] { barcode });
        ArrayList<Item> items = new ArrayList<Item>();
        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndex(COL_NAME));
            String itemBarcode = cursor.getString(cursor.getColumnIndex(COL_BARCODE));
            int itemQuantity = cursor.getInt(cursor.getColumnIndex(COL_QUANTITY));
            items.add(new Item(itemName, itemBarcode, itemQuantity));
        }
        cursor.close();
        return items;
    }

    public ArrayList<Item> selectAll() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Item> items = new ArrayList<Item>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            String barcode = cursor.getString(cursor.getColumnIndex(COL_BARCODE));
            int quantity = cursor.getInt(cursor.getColumnIndex(COL_QUANTITY));
            items.add(new Item(name, barcode, quantity));
        }
        cursor.close();
        return items;
    }
}
