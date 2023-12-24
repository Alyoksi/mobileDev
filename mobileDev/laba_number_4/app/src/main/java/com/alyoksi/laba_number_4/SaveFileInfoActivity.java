package com.alyoksi.laba_number_4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SaveFileInfoActivity extends AppCompatActivity {
    EditText fileName, fileTag, fileDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_file_info);

        fileName = findViewById(R.id.fileName);
        fileTag = findViewById(R.id.fileTag);
        fileDesc = findViewById(R.id.fileDesc);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fileName.getText().toString().trim();
                String tag = fileTag.getText().toString().trim();
                String desc = fileDesc.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("name", name);
                editor.putString("tag", tag);
                editor.putString("desc", desc);

                editor.apply();
            }
        });

    }
}