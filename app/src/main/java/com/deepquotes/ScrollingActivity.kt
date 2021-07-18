package com.deepquotes

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deepquotes.ScrollingActivity
import com.deepquotes.services.TimerService
import com.deepquotes.services.UpdateService
import com.deepquotes.utils.Constant
import com.deepquotes.utils.DBManager
import com.deepquotes.utils.QuotesSQLHelper
import com.dingmouren.colorpicker.ColorPickerDialog
import com.dingmouren.colorpicker.OnColorPickerListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class ScrollingActivity : AppCompatActivity() {
    private lateinit var mSQLHelper: QuotesSQLHelper
    private lateinit var mDataBase: SQLiteDatabase
    private lateinit var appConfigSP: SharedPreferences
    private lateinit var appConfigSPEditor: SharedPreferences.Editor
    private var mDrawerLayout: DrawerLayout? = null
    private lateinit var headlineTextView: TextView
    private var mColorPickerDialog: ColorPickerDialog? = null
    private var remoteViews: RemoteViews? = null
    private lateinit var appWidgetManager: AppWidgetManager
//    private var componentName: ComponentName? = null
    private var broadcast: myBroadcast? = null
    private var intentFilter: IntentFilter? = null
    private var clipboardManager: ClipboardManager? = null
    private val myList: MutableList<String?> = LinkedList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        mSQLHelper = DBManager.getInstance(this) as QuotesSQLHelper
        mDataBase = mSQLHelper.getReadableDatabase()
        appConfigSP = getSharedPreferences("appConfig", Context.MODE_PRIVATE)
        appConfigSPEditor = appConfigSP.edit()
        intentFilter = IntentFilter()
        intentFilter!!.addAction("com.deepquotes.broadcast.updateTextView")
        broadcast = myBroadcast()
        registerReceiver(broadcast, intentFilter)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val isEnableHitokoto = findViewById<Switch>(R.id.is_enable_hitokoto_sw)
        val hitokotoType = findViewById<TextView>(R.id.hitokoto_type)
        headlineTextView = findViewById(R.id.headline_text_view)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        remoteViews = RemoteViews(applicationContext.packageName, R.layout.quotes_layout)
        appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        var componentName = ComponentName(applicationContext, QuotesWidgetProvider::class.java)
        dayOrNightMode()
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val deepQuote = headlineTextView.text
            // 创建普通字符型ClipData
            val mClipData = ClipData.newPlainText("deepQuote", "$deepQuote——来自「相顾无言」")
            // 将ClipData内容放到系统剪贴板里。
            clipboardManager!!.setPrimaryClip(mClipData)
            Toast.makeText(this@ScrollingActivity, "复制成功!", Toast.LENGTH_SHORT).show()
            //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        if (appConfigSP.getBoolean("isEnableHitokoto", false)) {
//            hitokotoType.setClickable(false);
//            hitokotoType.setTextColor(Color.GRAY);
            isEnableHitokoto.isChecked = true
        } else {
//            hitokotoType.setClickable(true);
//            hitokotoType.setTextColor(Color.BLACK);
            isEnableHitokoto.isChecked = false
            hitokotoType.isClickable = false
            hitokotoType.setTextColor(Color.GRAY)
        }
        isEnableHitokoto.setOnCheckedChangeListener { compoundButton, isCheck ->
            if (isCheck) {
                if (isEnableHitokoto.isChecked) {
                    hitokotoType.isClickable = true
                    hitokotoType.setTextColor(Color.BLACK)
                    appConfigSPEditor.putBoolean("isEnableHitokoto", true)
                    appConfigSPEditor.apply()
                }
            } else {
                hitokotoType.isClickable = false
                hitokotoType.setTextColor(Color.GRAY)
                appConfigSPEditor.putBoolean("isEnableHitokoto", false)
                appConfigSPEditor.apply()
            }
        }
        mColorPickerDialog = ColorPickerDialog(this,
                appConfigSP.getInt("fontColor", Color.WHITE),
                false,
                object : OnColorPickerListener {
                    override fun onColorCancel(dialog: ColorPickerDialog) {}
                    override fun onColorChange(dialog: ColorPickerDialog, color: Int) {}
                    override fun onColorConfirm(dialog: ColorPickerDialog, color: Int) {
//                        Log.w("color",String.valueOf(color));
                        headlineTextView.setTextColor(color)
                        appConfigSPEditor.putInt("fontColor", color)
                        appConfigSPEditor.apply()
                        remoteViews!!.setTextColor(R.id.quotes_textview, color)
                        appWidgetManager.updateAppWidget(componentName, remoteViews)
                    }
                }
        )
    }

    override fun onStart() {
        super.onStart()

        //获取最新条目的信息
        var text: String? = null
        try {
            val maxId = maxDbId
            val queryMaxIdItem = "select * from " + Constant.TABLE_NAME + " where " + Constant._ID + "=" + maxId
            val cursor = mDataBase.rawQuery(queryMaxIdItem, null)
            cursor.moveToFirst()
            text = cursor.getString(cursor.getColumnIndex(Constant._TEXT))
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //更新主界面
        if (text != null && text != "") headlineTextView.text = text else headlineTextView.text = DEFAULT_DEEPQUOTE
        headlineTextView.setTextColor(appConfigSP.getInt("fontColor", Color.WHITE))
        headlineTextView.textSize = appConfigSP.getInt("字体大小:", 20).toFloat()
        updateHistoryQuotes()
    }

    fun onTextViewClick(view: View) {
        when (view.id) {
            R.id.update_now_tv -> {
                val intent = Intent(this@ScrollingActivity, TimerService::class.java)
                startService(intent)
            }
            R.id.refresh_time_tv -> selectRefreshTime()
            R.id.hitokoto_type -> selectHitokotoType()
            R.id.font_color_tv -> selectFontColor()
            R.id.font_size_tv -> selectFontSize()
            R.id.history_tv -> showHistory()
            R.id.feedback_tv -> feedBack()
        }
    }

    private fun updateHistoryQuotes() {    //只展示最新的100条数据
        myList.clear()
        val maxNum = maxDbId

        //获取最新的 MAX_HISTORY_ITMES 条数据
        val querySQL = "select * from " + Constant.TABLE_NAME + " where " + Constant._ID + ">" + (maxNum - MAX_HISTORY_ITMES)
        val cursor = mDataBase.rawQuery(querySQL, null)
        if (cursor.moveToFirst()) {
            do {
                myList.add(cursor.getString(cursor.getColumnIndex(Constant._TEXT)))
            } while (cursor.moveToNext())
        }
        cursor.close()

        //设置RecyclerView参数
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = QuotesAdapter(myList)
        recyclerView.adapter = adapter
    }

    fun showHistory() {
        mDrawerLayout!!.openDrawer(Gravity.RIGHT)
    }

    fun selectFontColor() {
        mColorPickerDialog!!.show()
        val window = mColorPickerDialog!!.dialog.window
        window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.background_color)))
    }

    fun selectFontSize() {
        val builder = AlertDialog.Builder(this)
        val layoutView = LayoutInflater.from(this).inflate(R.layout.seekbar_select_layout, null)
        builder.setView(layoutView)
        layoutView.setBackgroundColor(resources.getColor(R.color.background_color))
        val refreshTimeSeekBar = layoutView.findViewById<SeekBar>(R.id.seekbar_select_layout_seekbar)
        refreshTimeSeekBar.max = 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            refreshTimeSeekBar.min = 10
        }
        refreshTimeSeekBar.progress = appConfigSP.getInt("字体大小:", 10)
        val textView = layoutView.findViewById<TextView>(R.id.seekbar_select_layout_textview)
        textView.text = "字体大小:${appConfigSP.getInt("字体大小:", 10)}"
        refreshTimeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                textView.text = "字体大小:" + (i + 1)
                //                Log.d("字体大小", ": "+ (i+1));
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        builder.setPositiveButton("确认") { _, _ ->
            appConfigSPEditor.putInt("字体大小:", refreshTimeSeekBar.progress + 1)
            appConfigSPEditor.apply()
            headlineTextView.textSize = refreshTimeSeekBar.progress.toFloat()
            remoteViews!!.setTextViewTextSize(R.id.quotes_textview, TypedValue.COMPLEX_UNIT_SP, refreshTimeSeekBar.progress + 1.toFloat())
            appWidgetManager.updateAppWidget(componentName, remoteViews)
        }
        builder.setNegativeButton("取消") { _, _ -> }
        val alertDialog = builder.create()
        alertDialog.show()
        val window = alertDialog.window
        window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.background_color)))
    }

    fun selectRefreshTime() {
        val builder = AlertDialog.Builder(this)
        val layoutView = LayoutInflater.from(this).inflate(R.layout.seekbar_select_layout, null)
        builder.setView(layoutView)
        val refreshTimeSeekBar = layoutView.findViewById<SeekBar>(R.id.seekbar_select_layout_seekbar)
        //一天1440分钟，96等分，一等分15分钟，seekbar数值：0~95
        refreshTimeSeekBar.max = 95
        val textView = layoutView.findViewById<TextView>(R.id.seekbar_select_layout_textview)
        val defaultValue = appConfigSP.getInt("当前刷新间隔(分钟):", 15)
        textView.text = "当前刷新间隔(分钟):$defaultValue"
        refreshTimeSeekBar.progress = defaultValue / 15
        refreshTimeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                textView.text = "当前刷新间隔(分钟):" + (i + 1) * 15
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
//                stepTime[0] = seekBar.getProgress();
            }
        })
        builder.setPositiveButton("确认") { _, _ ->
            val newRefreshTime = (refreshTimeSeekBar.progress + 1) * 15
            appConfigSPEditor.putInt("当前刷新间隔(分钟):", newRefreshTime)
            appConfigSPEditor.apply()
            val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(12345)
            val componentName = ComponentName(this@ScrollingActivity, UpdateService::class.java)
            val jobInfo = JobInfo.Builder(12345, componentName)
                    .setPeriodic(newRefreshTime * 60 * 1000.toLong())
                    .build()
            jobScheduler.schedule(jobInfo)
        }
        builder.setNegativeButton("取消") { _, _ -> }
        val alertDialog = builder.create()
        alertDialog.show()
        val window = alertDialog.window
        window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.background_color)))
    }

    fun selectHitokotoType() {
//        ArrayList selection = new ArrayList();
//        String[] types = {"随机","动画、漫画","游戏","文学","影视","诗词","网易云"};
        val TypeBuilder = AlertDialog.Builder(this)
        TypeBuilder.setTitle("选择一言句子类型")
        val layoutView = LayoutInflater.from(this).inflate(R.layout.hitokoto_type_layout, null)
        TypeBuilder.setView(layoutView)
        val randomCheckbox = layoutView.findViewById<CheckBox>(R.id.random_checkbox)
        val abCheckbox = layoutView.findViewById<CheckBox>(R.id.animation_checkbox)
        val cCheckbox = layoutView.findViewById<CheckBox>(R.id.game_checkbox)
        val dCheckbox = layoutView.findViewById<CheckBox>(R.id.literature_checkbox)
        val hCheckbox = layoutView.findViewById<CheckBox>(R.id.film_checkbox)
        val iCheckbox = layoutView.findViewById<CheckBox>(R.id.poetry_checkbox)
        val jCheckbox = layoutView.findViewById<CheckBox>(R.id.neteasemusic_checkbox)
        val checkBoxGroup: MutableList<CheckBox> = ArrayList()
        checkBoxGroup.add(randomCheckbox)
        checkBoxGroup.add(abCheckbox)
        checkBoxGroup.add(cCheckbox)
        checkBoxGroup.add(dCheckbox)
        checkBoxGroup.add(hCheckbox)
        checkBoxGroup.add(iCheckbox)
        checkBoxGroup.add(jCheckbox)
        for (i in checkBoxGroup) {
            i.isChecked = appConfigSP.getBoolean(i.text.toString(), false)
        }
        val listener = CompoundButton.OnCheckedChangeListener { compoundButton, isCheck ->
            if (compoundButton.isChecked) {
                if (compoundButton.text == "随机") {
//                        Log.d("TAG", "onCheckedChanged: " + compoundButton.getText());
                    abCheckbox.isChecked = false
                    cCheckbox.isChecked = false
                    dCheckbox.isChecked = false
                    hCheckbox.isChecked = false
                    iCheckbox.isChecked = false
                    jCheckbox.isChecked = false
                } else {
                    randomCheckbox.isChecked = false
                    //                        Log.d("TAG", "onCheckedChanged: "+compoundButton.getText());
                }
            }
            appConfigSPEditor.putBoolean(compoundButton.text.toString(), isCheck)
        }
        randomCheckbox.setOnCheckedChangeListener(listener)
        abCheckbox.setOnCheckedChangeListener(listener)
        cCheckbox.setOnCheckedChangeListener(listener)
        dCheckbox.setOnCheckedChangeListener(listener)
        hCheckbox.setOnCheckedChangeListener(listener)
        iCheckbox.setOnCheckedChangeListener(listener)
        jCheckbox.setOnCheckedChangeListener(listener)
        TypeBuilder.setPositiveButton("确定") { _, _ -> appConfigSPEditor.apply() }
        TypeBuilder.setNegativeButton("取消") { _, _ -> }
        val alertDialog = TypeBuilder.create()
        alertDialog.show()
        val window = alertDialog.window
        window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.background_color)))
    }

    fun feedBack() {
//        Intent intent = getPackageManager().getLaunchIntentForPackage("com.coolapk.market");
//        if (intent != null) {
//            startActivity(intent);
//        }else
//            Toast.makeText(this,"你还没有安装「酷安」app,请先安装",Toast.LENGTH_SHORT).show();
        val mailAddress = arrayOf("developer.liang@outlook.com")
        composeEmail(mailAddress, "「相顾无言」问题反馈")
    }

    fun composeEmail(addresses: Array<String>?, subject: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    //获取表中最后一条数据的id
    private val maxDbId:
    //        Log.i(TAG, "getMaxDbId:cursor ->"+maxId);
            Int
        private get() {   //获取表中最后一条数据的id
            val querySQL = "select max(" + Constant._ID + ") from " + Constant.TABLE_NAME
            val cursor = mDataBase.rawQuery(querySQL, null)
            cursor.moveToFirst()
            val maxId = cursor.getInt(0)
            //        Log.i(TAG, "getMaxDbId:cursor ->"+maxId);
            cursor.close()
            return maxId
        }

    override fun onDestroy() {
        super.onDestroy()
        mDataBase.close()
        unregisterReceiver(broadcast)

//        JobScheduler jobScheduler = (JobScheduler)this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        ComponentName componentName = new ComponentName(this,UpdateService.class);
//        int duration = appConfigSP.getInt("当前刷新间隔(分钟):",15);
//        JobInfo jobInfo = new JobInfo.Builder(12345, componentName)
//                .setPeriodic(duration * 60 * 1000)
//                .build();
//        int ret = jobScheduler.schedule(jobInfo);
//        if (ret == JobScheduler.RESULT_SUCCESS) {
//            Log.d(TAG, "Job scheduled successfully");
//        } else {
//            Log.d(TAG, "Job scheduling failed");
//        }

//        Log.d(TAG, "onDestroy: ");
    }

    private fun dayOrNightMode() {
        val currentNightMode = applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO ->                 // Night mode is not active, we're using the light theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Configuration.UI_MODE_NIGHT_YES ->                 // Night mode is active, we're using dark theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> {
            }
        }
    }

    inner class myBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.deepquotes.broadcast.updateTextView") {
                headlineTextView.text = intent.getStringExtra("quote")
                updateHistoryQuotes()
                //                Log.d("广播", intent.getAction());
//                Toast.makeText(context, "已更新", Toast.LENGTH_SHORT).show();
            }
        }
    }

    companion object {
        private const val TAG = "ScrollingActivity"
        private const val DEFAULT_DEEPQUOTE = "与买桂花同载酒，终不似，少年游"

        //设置最大历史记录条目的保存量
        private const val MAX_HISTORY_ITMES = 100
    }
}