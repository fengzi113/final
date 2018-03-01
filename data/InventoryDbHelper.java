package com.example.fengzi113.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.TABLE_NAME;

/**
 * Created by fengzi113 on 2018/2/26.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "InventoryDbHelper";
    private Context mContext;
    private static final String DATEBASE_NAME = "shelter.db";
    private static final int DATEBASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATEBASE_NAME, null, DATEBASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + "(" +
                InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL," +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_LEFT + " INTEGER NOT NULL DEFAULT 0," +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE + " INTEGER NOT NULL DEFAULT 0," +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SALE + " INTEGER NOT NULL DEFAULT 0," +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE + " TEXT NOT NULL" +");";
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "======== onUpgrade ========");
        mContext.deleteDatabase(TABLE_NAME);
        onCreate(db);
    }
}
