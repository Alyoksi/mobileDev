package com.alyoksi.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText input = findViewById(R.id.input);
        Button change = findViewById(R.id.change);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inp = input.getText().toString();
                if(inp.equals("")) return;

                int num = Integer.parseInt(inp);
                if(num == 0) return;

                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt(MainActivity.MAX_PTS, num);
                editor.putInt(MainActivity.PLAYER_PTS, 0);
                editor.putInt(MainActivity.BOT_PTS, 0);

                editor.apply();
            }
        });

    }
    public void returnMain(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}