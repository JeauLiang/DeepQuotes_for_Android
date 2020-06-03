package com.deepquotes;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        SeekBar fontSize = findViewById(R.id.font_size);


        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView freshTimeItem = findViewById(R.id.fresh_time_item);
        freshTimeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.Builder dialog = new DatePickerDialog.Builder(ScrollingActivity.this);
                dialog.setTitle("请选择刷新时间");
                dialog.show();
            }
        });

        final Switch isenableHitokoto = findViewById(R.id.is_enable_hitokoto);
        final Spinner quotesType  = findViewById(R.id.quotes_type);
        final TextView textType = findViewById(R.id.quotes_type_text);

        if (!isenableHitokoto.isChecked()){
            quotesType.setEnabled(false);
            quotesType.setSelected(false);
            textType.setTextColor(Color.GRAY);
        }else {
            quotesType.setEnabled(true);
            textType.setTextColor(Color.BLACK);
        }

        isenableHitokoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck){
                    if (isenableHitokoto.isChecked()){
                        quotesType.setEnabled(true);
                        textType.setTextColor(Color.BLACK);
                    }
                }else {
                    quotesType.setEnabled(false);
                    quotesType.setSelected(false);
                    textType.setTextColor(Color.GRAY);
                }
            }
        });



        Spinner typeSpinner = findViewById(R.id.quotes_type);
        ArrayAdapter typeAdapter = new ArrayAdapter(this,R.layout.quotes_type_layout);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ScrollingActivity.this,"u selected"+view,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



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


}
