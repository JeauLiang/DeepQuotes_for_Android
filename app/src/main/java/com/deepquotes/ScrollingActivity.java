package com.deepquotes;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();


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

/**
    public void selectHitokotoType(View v){
//        ArrayList selection = new ArrayList();
//        String[] types = {"随机","动画、漫画","游戏","文学","影视","诗词","网易云"};
        AlertDialog.Builder TypeBuilder = new AlertDialog.Builder(this);
        TypeBuilder.setTitle("选择一言句子类型");
        View layoutView = LayoutInflater.from(this).inflate(R.layout.hitokoto_type_layout,null);
        TypeBuilder.setView(layoutView);

        final CheckBox randomCheckbox = layoutView.findViewById(R.id.random_type);
        final CheckBox abCheckbox = layoutView.findViewById(R.id.animation_type);
        final CheckBox cCheckbox = layoutView.findViewById(R.id.game_type);
        final CheckBox dCheckbox = layoutView.findViewById(R.id.literature_type);
        final CheckBox hCheckbox = layoutView.findViewById(R.id.film_type);
        final CheckBox iCheckbox = layoutView.findViewById(R.id.poetry_type);
        final CheckBox jCheckbox = layoutView.findViewById(R.id.neteasemusic);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    switch (compoundButton.getId()) {
                        case R.id.random_type:
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
            }
        };

        randomCheckbox.setOnCheckedChangeListener(listener);
        abCheckbox.setOnCheckedChangeListener(listener);
        cCheckbox.setOnCheckedChangeListener(listener);
        dCheckbox.setOnCheckedChangeListener(listener);
        hCheckbox.setOnCheckedChangeListener(listener);
        iCheckbox.setOnCheckedChangeListener(listener);
        jCheckbox.setOnCheckedChangeListener(listener);

        TypeBuilder.show();
        //v.setBackgroundColor(Color.RED);
    }
 **/
    public void selectHitokotoType(View v){
        String[] hitokotoType = {"随机","动画、漫画","游戏","文学","影视","诗词","网易云"};
        AlertDialog.Builder selectHitokotoTypeBuilder = new AlertDialog.Builder(this);
        ArrayList<String> selecetType = new ArrayList<>();
        final boolean checkedItems[] = {false};
        selectHitokotoTypeBuilder.setMultiChoiceItems(hitokotoType, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                
            }
        });
        selectHitokotoTypeBuilder.show();

    }

    public void selectFontStyle(View v){
        final String[] styles = {"加粗","斜体"};
        final AlertDialog.Builder selectFontStyleBuilder = new AlertDialog.Builder(this);
        final ArrayList selectedStyles = new ArrayList();

        selectFontStyleBuilder.setMultiChoiceItems(styles, null, new DialogInterface.OnMultiChoiceClickListener() {
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


}
