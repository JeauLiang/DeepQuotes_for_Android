package com.deepquotes.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class QuotesSQLHelper extends SQLiteOpenHelper {


    private static final String TAG = "QuotesSQLHelper";

    public QuotesSQLHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "------------onCreate-------------");

        String createSql = "create table "+ Constant.TABLE_NAME +
                "("+ Constant._ID + " Integer primary key autoincrement,"+ Constant._TEXT + " varchar(10)," +
                Constant._AUTHOR + " varchar(10))";

        sqLiteDatabase.execSQL(createSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i(TAG, "------------onUpgrade-------------");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.i(TAG, "------------onOpen-------------");
    }
}
