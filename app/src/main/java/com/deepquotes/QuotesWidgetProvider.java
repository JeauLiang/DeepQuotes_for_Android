package com.deepquotes;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class QuotesWidgetProvider extends AppWidgetProvider {

    //窗口小部件点击时调用
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Toast.makeText(context,"你点击了控件",Toast.LENGTH_SHORT).show();
    }

    //窗口小部件更新时时调用
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Toast.makeText(context,"你更新了控件",Toast.LENGTH_SHORT).show();
    }

    //窗口小部件被删除时调用
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Toast.makeText(context,"你删除了控件",Toast.LENGTH_SHORT).show();
    }

    //该窗口小部件第一次添加到桌面时调用该方法
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Toast.makeText(context,"你添加了了控件",Toast.LENGTH_SHORT).show();
    }

    //最后一个该窗口小部件删除时调用该方法
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Toast.makeText(context,"你删除了最后一个控件",Toast.LENGTH_SHORT).show();
    }


    //当小部件大小改变时调用
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Toast.makeText(context,"你改变了控件大小",Toast.LENGTH_SHORT).show();
    }


    //当小部件从备份恢复时调用该方法
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Toast.makeText(context,"你恢复控件",Toast.LENGTH_SHORT).show();
    }
}
