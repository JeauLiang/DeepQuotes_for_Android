package com.deepquotes

import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.widget.RemoteViews
import com.deepquotes.ScrollingActivity
import com.deepquotes.services.UpdateService
import com.deepquotes.utils.Constant
import com.deepquotes.utils.DBManager
import com.deepquotes.utils.QuotesSQLHelper

class QuotesWidgetProvider : AppWidgetProvider() {
    private lateinit var appConfigSP: SharedPreferences
    private lateinit var mQuotesSQLHelper: QuotesSQLHelper
    private lateinit var mDatabase: SQLiteDatabase

    //窗口小部件更新时时调用
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        mQuotesSQLHelper = DBManager.getInstance(context) as QuotesSQLHelper
        mDatabase = mQuotesSQLHelper.getReadableDatabase()
        if (!::appConfigSP.isInitialized) appConfigSP = context.getSharedPreferences("appConfig", Context.MODE_PRIVATE)
        val N = appWidgetIds.size
        for (i in 0 until N) {
            val appWidgetId = appWidgetIds[i]
            val views = RemoteViews(context.packageName, R.layout.quotes_layout)
            views.setTextColor(R.id.quotes_textview, Color.WHITE)
            var quote: String? = null
            try {
                val maxId = getMaxDbId(mDatabase)
                val queryMaxIdItem = "select * from " + Constant.TABLE_NAME + " where " + Constant._ID + "=" + maxId
                val cursor = mDatabase.rawQuery(queryMaxIdItem, null)
                cursor.moveToFirst()
                quote = cursor.getString(cursor.getColumnIndex(Constant._TEXT))
                cursor.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //更新Widgets
            if (quote == null && quote == "") quote = "欲买桂花同载酒，终不似，少年游"
            views.setTextViewText(R.id.quotes_textview, quote)
            views.setTextViewTextSize(R.id.quotes_textview, TypedValue.COMPLEX_UNIT_SP, appConfigSP!!.getInt("字体大小:", 20).toFloat())
            val openIntent = Intent(context, ScrollingActivity::class.java)
            //            openIntent.putExtra("quote",quote);
            val openPendingIntent = PendingIntent.getActivity(context, 0, openIntent, 0)
            views.setOnClickPendingIntent(R.id.quotes_textview, openPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getMaxDbId(Database: SQLiteDatabase?): Int {   //获取表中最后一条数据的id
        val querySQL = "select max(" + Constant._ID + ") from " + Constant.TABLE_NAME
        val cursor = Database!!.rawQuery(querySQL, null)
        cursor.moveToFirst()
        val maxId = cursor.getInt(0)
        //        Log.i(TAG, "getMaxDbId:cursor ->"+maxId);
        cursor.close()
        return maxId
    }

    //窗口小部件被删除时调用
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        mDatabase.close()
        //        Toast.makeText(context,"你删除了控件",Toast.LENGTH_SHORT).show();
    }

    //该窗口小部件第一次添加到桌面时调用该方法
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        appConfigSP = context.getSharedPreferences("appConfig", Context.MODE_PRIVATE)
        val duration = appConfigSP.getInt("当前刷新间隔(分钟):", 15)
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(context, UpdateService::class.java)
        val jobInfo = JobInfo.Builder(12345, componentName)
                .setPeriodic(duration * 60 * 1000.toLong())
                .setPersisted(true)
                .build()
        jobScheduler.schedule(jobInfo)


//        Toast.makeText(context,"你添加了了控件",Toast.LENGTH_SHORT).show();
//        Log.i("TAG", "onEnabled: ");
//        Intent startTimerIntent = new Intent(context, TimerService.class);
////        startTimerIntent.putExtra("refreshTime",sharedPreferences.getInt("当前刷新间隔(分钟):",10));
//        context.startService(startTimerIntent);
    }

    //最后一个该窗口小部件删除时调用该方法
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        //        Toast.makeText(context,"你删除了最后一个控件",Toast.LENGTH_SHORT).show();
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(12345)
        //        context.stopService(new Intent(context, TimerService.class));

//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        manager.cancel(createPendingIntent(context));
    }

    //当小部件大小改变时调用
    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        //        Toast.makeText(context,"你改变了控件大小",Toast.LENGTH_SHORT).show();
    }

    //当小部件从备份恢复时调用该方法
    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        //        Toast.makeText(context,"你恢复控件",Toast.LENGTH_SHORT).show();
    }
}