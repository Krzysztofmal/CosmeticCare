package com.jkero.blackhawk.testaparatuocr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "ingredients.db";

    private static final String TABLE_NAME = "ingredient";

    private static final String COLUMN_ID = "id_ingredient";


    SQLiteDatabase database;

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addIngredient(Integer id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == 1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        Cursor data = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }

    public Integer deleteIngredient(Integer id) {
        return database.delete(TABLE_NAME, "id_ingredient=?", new String[]{id.toString()});
    }


}
