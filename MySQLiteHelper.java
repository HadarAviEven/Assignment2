package com.hadar.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "Users";
    public static final String COL_NAME = "name";
    public static final String COL_GENDER = "gender";
    public static final String COL_STREET = "street";
    public static final String COL_COUNTRY = "country";
    public static final String COL_POSTCODE = "postcode";

    public MySQLiteHelper(Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                COL_NAME + " TEXT," + " " +
                COL_GENDER + " TEXT," + " " +
                COL_STREET + " TEXT," + " " +
                COL_COUNTRY + " TEXT," + " " +
                COL_POSTCODE + " TEXT " + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(Person p) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_NAME, p.getName());
        contentValues.put(COL_GENDER, p.getGender());
        contentValues.put(COL_STREET, p.getStreet());
        contentValues.put(COL_COUNTRY, p.getCountry());
        contentValues.put(COL_POSTCODE, p.getPostcode());

        if (db.insert(TABLE_NAME, null, contentValues) != -1) {
            return true;
        }
        return false;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public Boolean deleteData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (db.delete(TABLE_NAME, "NAME = ?", new String[]{name}) != -1) {
            return true;
        }
        return false;
    }

    public Boolean deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        if (db.delete(TABLE_NAME, "1", null) != -1) {
            return true;
        }
        return false;
    }

    public boolean updateData(String name, String gender, String street, String country, String postcode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_NAME, name);
        contentValues.put(COL_GENDER, gender);
        contentValues.put(COL_STREET, street);
        contentValues.put(COL_COUNTRY, country);
        contentValues.put(COL_POSTCODE, postcode);

        if (db.update(TABLE_NAME, contentValues, "NAME = ?", new String[]{name}) != -1) {
            return true;
        }
        return false;
    }
}
