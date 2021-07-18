package com.deepquotes.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class QuotesSQLHelper(context: Context?, name: String?, factory: CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
//        Log.i(TAG, "------------onCreate-------------")
        val createSql = "create table " + Constant.TABLE_NAME +
                "(" + Constant._ID + " Integer primary key autoincrement," + Constant._TEXT + " varchar(10)," +
                Constant._AUTHOR + " varchar(10))"
        sqLiteDatabase.execSQL(createSql)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
//        Log.i(TAG, "------------onUpgrade-------------")
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
//        Log.i(TAG, "------------onOpen-------------")
    }

    companion object {
        private const val TAG = "QuotesSQLHelper"
    }
}