package com.deepquotes.utils;

import android.content.Context;

import static com.deepquotes.utils.Constant.*;

public class DBManager {

    private static QuotesSQLHelper mQuotesSQLHelper;

    public static QuotesSQLHelper getInstance(Context context){
        if (mQuotesSQLHelper==null){
            mQuotesSQLHelper = new QuotesSQLHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
            return mQuotesSQLHelper;
        }
        return mQuotesSQLHelper;
    }

}
