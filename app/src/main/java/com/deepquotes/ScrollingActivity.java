package com.deepquotes;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.LocaleData;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        TextView historyTextView = findViewById(R.id.history_textview);
        TextView fontSizeTextView = findViewById(R.id.font_size_textview);
        TextView refreshTimeTextView = findViewById(R.id.refresh_time_textview);
        final Switch isEnableHitokoto = findViewById(R.id.is_enable_hitokoto);
        final TextView hitokotoType = findViewById(R.id.hitokoto_type);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        refreshTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBarSelect("当前刷新间隔(分钟):",119);
            }
        });
        fontSizeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBarSelect("字体大小:",25);
            }
        });



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
    }

    public void showHistory(View view){
        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void seekBarSelect(final String text,int MaxProgress){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layoutView = LayoutInflater.from(this).inflate(R.layout.seekbar_select_layout,null);
        builder.setView(layoutView);

        final SeekBar refreshTimeSeekBar = layoutView.findViewById(R.id.seekbar_select_layout_seekbar);
        refreshTimeSeekBar.setMax(MaxProgress);
        refreshTimeSeekBar.setProgress(sharedPreferences.getInt(text,0));
        final TextView textView = layoutView.findViewById(R.id.seekbar_select_layout_textview);
        textView.setText(text + sharedPreferences.getInt(text,0));
        final int[] stepTime = new int[1];

        refreshTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(text+(i+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                stepTime[0] = seekBar.getProgress();
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedPreferencesEditor.putInt(text,refreshTimeSeekBar.getProgress());
                sharedPreferencesEditor.apply();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }



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

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
//                sharedPreferencesEditor.putBoolean(compoundButton)
//                Log.i("11111111111111111", compoundButton.id);
                if (compoundButton.isChecked()) {
                    switch (compoundButton.getId()) {
                        case R.id.random_checkbox:
                            abCheckbox.setChecked(false);
                            cCheckbox.setChecked(false);
                            dCheckbox.setChecked(false);
                            hCheckbox.setChecked(false);
                            iCheckbox.setChecked(false);
                            jCheckbox.setChecked(false);
                            break;
                        default:
                            randomCheckbox.setChecked(false);
                            break;
                    }
                 }
                sharedPreferencesEditor.putBoolean(String.valueOf(compoundButton.getId()),isCheck);
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


}
