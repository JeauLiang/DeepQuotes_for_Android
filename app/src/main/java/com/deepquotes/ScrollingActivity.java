package com.deepquotes;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        TextView fontSizeTextView = findViewById(R.id.font_size_textview);
        TextView refreshTimeTextView = findViewById(R.id.refresh_time_textview);
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
                seekBarSelect("当前刷新间隔(分钟):",120);
            }
        });
        fontSizeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBarSelect("字体大小:",25);
            }
        });

        final Switch isenableHitokoto = findViewById(R.id.is_enable_hitokoto);
        final TextView textType = findViewById(R.id.hitokoto_type);

        if (!isenableHitokoto.isChecked()){
            textType.setClickable(false);
            textType.setTextColor(Color.GRAY);
        }else {
            textType.setClickable(true);
            textType.setTextColor(Color.BLACK);
        }


        isenableHitokoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck){
                    if (isenableHitokoto.isChecked()){
                        textType.setClickable(true);
                        textType.setTextColor(Color.BLACK);
                    }
                }else {
                    textType.setClickable(false);
                    textType.setTextColor(Color.GRAY);
                }
            }
        });

        textType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder TypeBuilder = new AlertDialog.Builder(ScrollingActivity.this);
                TypeBuilder.setTitle("选择一言句子类型");
                View layoutView = LayoutInflater.from(ScrollingActivity.this).inflate(R.layout.quotes_type_layout,null);
                TypeBuilder.setView(layoutView);

                TypeBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                TypeBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                TypeBuilder.show();
            }
        });




    }

    public void seekBarSelect(final String text,int MaxProgress){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("请选择刷新间隔");
        View layoutView = LayoutInflater.from(this).inflate(R.layout.seekbar_select_layout,null);
        builder.setView(layoutView);
        SeekBar refreshTimeSeekBar = layoutView.findViewById(R.id.time_seekbar);
        refreshTimeSeekBar.setMax(MaxProgress);
        final TextView textView = layoutView.findViewById(R.id.time_show_textview);
        textView.setText(text);
        refreshTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(text+i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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


    public void selectHitokotoType(){
        ArrayList selection = new ArrayList();
        String[] types = {"随机","动画、漫画","游戏","文学","影视","诗词","网易云"};
        AlertDialog.Builder TypeBuilder = new AlertDialog.Builder(ScrollingActivity.this);
        TypeBuilder.setMultiChoiceItems(types, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {

            }
        });

    }





}
