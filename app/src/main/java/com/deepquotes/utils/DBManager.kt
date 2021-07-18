package com.deepquotes.utils

import android.content.Context
import com.deepquotes.utils.Constant.DATABASE_NAME
import com.deepquotes.utils.Constant.DATABASE_VERSION

object DBManager {
    private var mQuotesSQLHelper: QuotesSQLHelper? = null
    fun getInstance(context: Context?): QuotesSQLHelper? {
        if (mQuotesSQLHelper == null) {
            mQuotesSQLHelper = QuotesSQLHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
            return mQuotesSQLHelper
        }
        return mQuotesSQLHelper
    }
}