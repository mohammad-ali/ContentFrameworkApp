package com.kaspian.book;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DataBase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "book.db";
    private static final int DATABASE_VERSION = App.DataBase_Version;
    SQLiteDatabase db;
    SQLiteQueryBuilder qb;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(DATABASE_VERSION);
        db = getReadableDatabase();
        qb = new SQLiteQueryBuilder();
    }

    public String getTitle(int table,int id){

        String tab = "s" + table;
        Cursor c = db.query(tab, null, null,
                null, null, null,null);
        c.moveToPosition(id-1);
        String ss = c.getString(1);
        if (ss == null){
            ss = " ";
        }
        c.close();
        return ss;
    }

    public String getContent(int table,int id){
        String tab = "s" + table;
        Cursor c = db.query(tab, null, null,
                null, null, null,null);
        c.moveToPosition(id-1);
        String ss = c.getString(2);
        if (ss == null){
            ss = " ";
        }
        c.close();
        return ss;
    }

    public void close(){
        db.close();
    }


    public int countTables() {
        int count = 0;
        String SQL_GET_ALL_TABLES = "SELECT * FROM sqlite_master WHERE type='table'";
        Cursor cursor = getReadableDatabase()
                .rawQuery(SQL_GET_ALL_TABLES, null);
        while(cursor.moveToNext()) {

            count++;
            getReadableDatabase().close();

        }
        cursor.close();
        return count-2;
    }

    public int countRows(String tn) {
        db = this.getReadableDatabase();
        int cnt  = (int) DatabaseUtils.queryNumEntries(db, tn);
        return cnt;
    }



}
