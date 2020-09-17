package com.deepquotes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.fonts.Font;
import android.os.Bundle;

import com.dingmouren.colorpicker.ColorPickerDialog;
import com.dingmouren.colorpicker.OnColorPickerListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.deepquotes.Quotes.UPDATE_TEXT;

public class ScrollingActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private DrawerLayout mDrawerLayout;
    private TextView headlineTextView;

    private ColorPickerDialog mColorPickerDialog;

    private RemoteViews remoteViews;
    private AppWidgetManager appWidgetManager;
    private ComponentName componentName;

    private Handler handler;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        mDrawerLayout = findViewById(R.id.drawer_layout);

//        TextView historyTextView = findViewById(R.id.history_textview);
//        TextView fontSizeTextView = findViewById(R.id.font_size_textview);
//        final TextView refreshTimeTextView = findViewById(R.id.refresh_time_textview);
        final Switch isEnableHitokoto = findViewById(R.id.is_enable_hitokoto);
        final TextView hitokotoType = findViewById(R.id.hitokoto_type);
        headlineTextView = findViewById(R.id.headline_text_view);
        headlineTextView.setTextColor(sharedPreferences.getInt("fontColor",Color.WHITE));
        TextView updateNowTextView = findViewById(R.id.update_now);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        remoteViews = new RemoteViews(getApplicationContext().getPackageName(),R.layout.quotes_layout);
        appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        componentName = new ComponentName(getApplicationContext(), QuotesWidgetProvider.class);

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case UPDATE_TEXT:
                        if (msg.obj != null) {
                            String textMessage = msg.obj.toString();
                            headlineTextView.setText(textMessage);

                            remoteViews.setTextViewText(R.id.quotes_textview, textMessage);
                            remoteViews.setTextColor(R.id.quotes_textview, sharedPreferences.getInt("fontColor", Color.WHITE));
                            remoteViews.setTextViewTextSize(R.id.quotes_textview, COMPLEX_UNIT_SP, sharedPreferences.getInt("字体大小:", 10));
                            appWidgetManager.updateAppWidget(componentName, remoteViews);
                        }
                        break;
                    default:break;

                }
            }
        };


        updateNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("刷新","u click updata");

                if (sharedPreferences.getBoolean("isEnableHitokoto",false)) {   //启用一言
                    if (sharedPreferences.getBoolean("随机",false))             //随机一言
                        getDeepQuotes(new Random().nextInt(4), "");
                    else {
                        StringBuffer stringBuffer = new StringBuffer("?");
                        if(sharedPreferences.getBoolean("动画、漫画",false)) stringBuffer.append("c=a&c=b");
                        if(sharedPreferences.getBoolean("游戏",false)) stringBuffer.append("&c=c");
                        if(sharedPreferences.getBoolean("文学",false)) stringBuffer.append("&c=d");
                        if(sharedPreferences.getBoolean("影视",false)) stringBuffer.append("&c=h");
                        if(sharedPreferences.getBoolean("诗词",false)) stringBuffer.append("&c=i");
                        if(sharedPreferences.getBoolean("网易云",false)) stringBuffer.append("&c=j");
                        Log.d("TAG","stringBuffer "+stringBuffer);
                        getDeepQuotes(new Random().nextInt(4),stringBuffer.toString());
                    }
                }else {     //关闭一言
                    getDeepQuotes(new Random().nextInt(3),"");
                }

//                remoteViews.setTextViewText(R.id.quotes_textview,"控件更新: "+ Math.random());
//                remoteViews.setTextColor(R.id.quotes_textview,sharedPreferences.getInt("fontColor",Color.WHITE));
//                remoteViews.setTextViewTextSize(R.id.quotes_textview,COMPLEX_UNIT_SP,sharedPreferences.getInt("字体大小:",10));
//                appWidgetManager.updateAppWidget(componentName,remoteViews);
            }
        });




        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


//        refreshTimeTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                seekBarSelect("当前刷新间隔(分钟):",119);
//            }
//        });
//        fontSizeTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                seekBarSelect("字体大小:",25);
//            }
//        });



        if (sharedPreferences.getBoolean("isEnableHitokoto",false)){
//            hitokotoType.setClickable(false);
//            hitokotoType.setTextColor(Color.GRAY);
            isEnableHitokoto.setChecked(true);
        }else {
//            hitokotoType.setClickable(true);
//            hitokotoType.setTextColor(Color.BLACK);
            isEnableHitokoto.setChecked(false);
            hitokotoType.setClickable(false);
            hitokotoType.setTextColor(Color.GRAY);
        }

        isEnableHitokoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck){
                    if (isEnableHitokoto.isChecked()){
                        hitokotoType.setClickable(true);
                        hitokotoType.setTextColor(Color.BLACK);
                        sharedPreferencesEditor.putBoolean("isEnableHitokoto",true);
                        sharedPreferencesEditor.apply();
                    }
                }else {
                    hitokotoType.setClickable(false);
                    hitokotoType.setTextColor(Color.GRAY);
                    sharedPreferencesEditor.putBoolean("isEnableHitokoto",false);
                    sharedPreferencesEditor.apply();
                }
            }
        });


        List<String> myList = new ArrayList<>();
        myList.add("123");
        myList.add("456");
        myList.add("789");
        myList.add("000");
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        QuotesAdapter adapter = new QuotesAdapter(myList);
        recyclerView.setAdapter(adapter);

        mColorPickerDialog = new ColorPickerDialog(this,
                sharedPreferences.getInt("fontColor",Color.BLACK),
                false,
                new OnColorPickerListener() {
                    @Override
                    public void onColorCancel(ColorPickerDialog dialog) {

                    }

                    @Override
                    public void onColorChange(ColorPickerDialog dialog, int color) {

                    }

                    @Override
                    public void onColorConfirm(ColorPickerDialog dialog, int color) {
                        Log.w("color",String.valueOf(color));
                        headlineTextView.setTextColor(color);

                        sharedPreferencesEditor.putInt("fontColor",color);
                        sharedPreferencesEditor.apply();

                        remoteViews.setTextColor(R.id.quotes_textview,color);
                        appWidgetManager.updateAppWidget(componentName,remoteViews);
                    }
                }
        );



    }

    public void showHistory(View view){
        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void selectFontColor(View view){

       mColorPickerDialog.show();


    }

    public void selectFontSize(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layoutView = LayoutInflater.from(this).inflate(R.layout.seekbar_select_layout,null);
        builder.setView(layoutView);

        final SeekBar refreshTimeSeekBar = layoutView.findViewById(R.id.seekbar_select_layout_seekbar);
        refreshTimeSeekBar.setMax(25);
        refreshTimeSeekBar.setProgress(sharedPreferences.getInt("字体大小:",0));
        final TextView textView = layoutView.findViewById(R.id.seekbar_select_layout_textview);
        textView.setText("字体大小:" + sharedPreferences.getInt("字体大小:",0));
//        final int[] stepTime = new int[1];

        refreshTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText("字体大小:"+(i+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                stepTime[0] = seekBar.getProgress();
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedPreferencesEditor.putInt("字体大小:",refreshTimeSeekBar.getProgress()+1);
                sharedPreferencesEditor.apply();

                remoteViews.setTextViewTextSize(R.id.quotes_textview,COMPLEX_UNIT_SP,refreshTimeSeekBar.getProgress()+1);
                appWidgetManager.updateAppWidget(componentName,remoteViews);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void selectRefreshTime(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layoutView = LayoutInflater.from(this).inflate(R.layout.seekbar_select_layout,null);
        builder.setView(layoutView);

        final SeekBar refreshTimeSeekBar = layoutView.findViewById(R.id.seekbar_select_layout_seekbar);
        refreshTimeSeekBar.setMax(119);
        refreshTimeSeekBar.setProgress(sharedPreferences.getInt("当前刷新间隔(分钟):",0));
        final TextView textView = layoutView.findViewById(R.id.seekbar_select_layout_textview);
        textView.setText("当前刷新间隔(分钟):" + sharedPreferences.getInt("当前刷新间隔(分钟):",0));
//        final int[] stepTime = new int[1];

        refreshTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText("当前刷新间隔(分钟):"+(i+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                stepTime[0] = seekBar.getProgress();
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedPreferencesEditor.putInt("当前刷新间隔(分钟):",refreshTimeSeekBar.getProgress()+1);
                sharedPreferencesEditor.apply();

//                remoteViews.setTextViewTextSize(R.id.quotes_textview,COMPLEX_UNIT_SP,refreshTimeSeekBar.getProgress()+1);
//                appWidgetManager.updateAppWidget(componentName,remoteViews);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

//
//    public void seekBarSelect(final String text,int MaxProgress){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View layoutView = LayoutInflater.from(this).inflate(R.layout.seekbar_select_layout,null);
//        builder.setView(layoutView);
//
//        final SeekBar refreshTimeSeekBar = layoutView.findViewById(R.id.seekbar_select_layout_seekbar);
//        refreshTimeSeekBar.setMax(MaxProgress);
//        refreshTimeSeekBar.setProgress(sharedPreferences.getInt(text,0));
//        final TextView textView = layoutView.findViewById(R.id.seekbar_select_layout_textview);
//        textView.setText(text + sharedPreferences.getInt(text,0));
//        final int[] stepTime = new int[1];
//
//        refreshTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                textView.setText(text+(i+1));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                stepTime[0] = seekBar.getProgress();
//            }
//        });
//        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                sharedPreferencesEditor.putInt(text,refreshTimeSeekBar.getProgress());
//                sharedPreferencesEditor.apply();
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        builder.show();
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void selectHitokotoType(View v){
//        ArrayList selection = new ArrayList();
//        String[] types = {"随机","动画、漫画","游戏","文学","影视","诗词","网易云"};
        AlertDialog.Builder TypeBuilder = new AlertDialog.Builder(this);
        TypeBuilder.setTitle("选择一言句子类型");
        View layoutView = LayoutInflater.from(this).inflate(R.layout.hitokoto_type_layout,null);
        TypeBuilder.setView(layoutView);

//        final RadioGroup radioGroup1 = layoutView.findViewById(R.id.id1111);
//        final RadioGroup radioGroup2 = layoutView.findViewById(R.id.id2222);

//        RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                switch (radioGroup.getId()){
//                    case R.id.id1111:
//                        radioGroup2.clearCheck();
//                        break;
//                    case R.id.id2222:
//                        radioGroup1.clearCheck();
//                        break;
//                    default:
//                        throw new IllegalStateException("Unexpected value: " + radioGroup.getId());
//                }
//            }
//        };

//        radioGroup1.setOnCheckedChangeListener(listener);
//        radioGroup2.setOnCheckedChangeListener(listener);


        final CheckBox randomCheckbox = layoutView.findViewById(R.id.random_checkbox);
        final CheckBox abCheckbox = layoutView.findViewById(R.id.animation_checkbox);
        final CheckBox cCheckbox = layoutView.findViewById(R.id.game_checkbox);
        final CheckBox dCheckbox = layoutView.findViewById(R.id.literature_checkbox);
        final CheckBox hCheckbox = layoutView.findViewById(R.id.film_checkbox);
        final CheckBox iCheckbox = layoutView.findViewById(R.id.poetry_checkbox);
        final CheckBox jCheckbox = layoutView.findViewById(R.id.neteasemusic_checkbox);

        List<CheckBox> checkBoxGroup = new ArrayList<CheckBox>();
        checkBoxGroup.add(randomCheckbox);
        checkBoxGroup.add(abCheckbox);
        checkBoxGroup.add(cCheckbox);
        checkBoxGroup.add(dCheckbox);
        checkBoxGroup.add(hCheckbox);
        checkBoxGroup.add(iCheckbox);
        checkBoxGroup.add(jCheckbox);

        for (CheckBox i:checkBoxGroup){
            i.setChecked (sharedPreferences.getBoolean(i.getText().toString(),false));
        }



        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
//                sharedPreferencesEditor.putBoolean(compoundButton)
//                Log.i("11111111111111111", compoundButton.id);
                if (compoundButton.isChecked()) {
                    if (compoundButton.getText().equals("随机")) {
                        Log.d("TAG", "onCheckedChanged: " + compoundButton.getText());
                        abCheckbox.setChecked(false);
                        cCheckbox.setChecked(false);
                        dCheckbox.setChecked(false);
                        hCheckbox.setChecked(false);
                        iCheckbox.setChecked(false);
                        jCheckbox.setChecked(false);
                    }
                    else{
                        randomCheckbox.setChecked(false);
                        Log.d("TAG", "onCheckedChanged: "+compoundButton.getText());
                    }
                 }
                sharedPreferencesEditor.putBoolean(compoundButton.getText().toString(),isCheck);
            }
        };

        randomCheckbox.setOnCheckedChangeListener(listener);
        abCheckbox.setOnCheckedChangeListener(listener);
        cCheckbox.setOnCheckedChangeListener(listener);
        dCheckbox.setOnCheckedChangeListener(listener);
        hCheckbox.setOnCheckedChangeListener(listener);
        iCheckbox.setOnCheckedChangeListener(listener);
        jCheckbox.setOnCheckedChangeListener(listener);

        TypeBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedPreferencesEditor.apply();
            }
        });
        TypeBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        TypeBuilder.show();
        //v.setBackgroundColor(Color.RED);
    }

 /**
    public void selectHitokotoType(View view){
        final String[] hitokotoType = {"随机","动画、漫画","游戏","文学","影视","诗词","网易云"};
        AlertDialog.Builder selectHitokotoTypeBuilder = new AlertDialog.Builder(this);
        final ArrayList<String> selecetType = new ArrayList<>();
        boolean checkedItems[] = new boolean[8];
        for (int m=0;m<hitokotoType.length;m++){
            checkedItems[m]=sharedPreferences.getBoolean(hitokotoType[m],false);
        }
        selectHitokotoTypeBuilder.setMultiChoiceItems(hitokotoType, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isCheck) {

                if (isCheck){
                    selecetType.add(hitokotoType[i]);
                }else {
                    selecetType.remove(hitokotoType[i]);
                }
                sharedPreferencesEditor.putBoolean(hitokotoType[i],isCheck);
            }
        });
        selectHitokotoTypeBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedPreferencesEditor.apply();
            }
        });
        selectHitokotoTypeBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("positiveButton i:"," "+i);
            }
        });
        selectHitokotoTypeBuilder.show();

    }
 **/

    public void selectFontStyle(View v){
        final String[] styles = {"加粗","斜体"};
        final AlertDialog.Builder selectFontStyleBuilder = new AlertDialog.Builder(this);
        final ArrayList selectedStyles = new ArrayList();

        boolean checkItems[] = new boolean[2];
        for (int i=0;i<checkItems.length;i++)
            checkItems[i] = sharedPreferences.getBoolean(styles[i],false);

        selectFontStyleBuilder.setMultiChoiceItems(styles, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isCheck) {
                if (isCheck)
                    selectedStyles.add(styles[i]);
                else selectedStyles.remove(styles[i]);
            }
        });
        selectFontStyleBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        selectFontStyleBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        selectFontStyleBuilder.show();


    }

    public void feedBack(View view){
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.coolapk.market");
        if (intent != null) {
//            intent.putExtra("type", "110");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else
            Toast.makeText(this,"你没有安装「酷安」app,请先安装",Toast.LENGTH_SHORT).show();
    }

    private void getDeepQuotes(int seed,String postParam){
        switch (seed){
            case 0:
                getDeepQuote();
                break;
            case 1:
                getDeepQuote2();
                break;
            case 2:
                getDeepQuote3(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            String responseStr = response.body().string();
                            JSONObject responseJSON = new JSONObject(responseStr);
                            responseStr = responseJSON.getString("txt");
                            
                            Log.d("DeepQuote3",responseStr);

                            Message message = new Message();
                            message.what = UPDATE_TEXT;
                            message.obj = responseStr;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            default:
                getHitokotoQuote(postParam,new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            String responseStr = response.body().string();
                            JSONObject responseJSON = new JSONObject(responseStr);
                            responseStr = responseJSON.getString("hitokoto");
                            Log.d("hikotoko",responseStr);

                            Message message = new Message();
                            message.what = UPDATE_TEXT;
                            message.obj = responseStr;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });



        }
    }

    private void getDeepQuote(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data=null;
                try {
                    Document document = Jsoup.connect("http://www.nows.fun/").get();
                    Elements element = document.select("div.main-wrapper");
                    for (int i=0;i<element.size();i++) {
                        data = element.get(i).select("span").text();
                    }

                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    message.obj = data;
                    handler.sendMessage(message);

                    Log.d("DeepQuote1", data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //return data;
    }

    private void getDeepQuote2(){
        //https://www.nihaowua.com/home.html
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("https://www.nihaowua.com/home.html").get();
                    Elements element = document.select("div.post97");
                    String data = null;
                    for (int i=0;i<element.size();i++){
                        data = element.get(i).select("font").text();
                        Log.d("DeepQuote2",data);
                    }

                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    message.obj = data;
                    handler.sendMessage(message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void getDeepQuote3(Callback callback){
        //https://www.apicp.cn/API/yan/api.php
        //↑↑↑↑↑↑↑2020.9.17已失效

        //新接口：https://data.zhai78.com/openOneBad.php
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://data.zhai78.com/openOneBad.php").build();
        client.newCall(request).enqueue(callback);
    }

    private void getHitokotoQuote(String postParam,Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://v1.hitokoto.cn/"+postParam).build();
        client.newCall(request).enqueue(callback);

    }


}
