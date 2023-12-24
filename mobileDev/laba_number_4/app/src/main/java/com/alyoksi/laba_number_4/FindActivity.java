package com.alyoksi.laba_number_4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class FindActivity extends AppCompatActivity {

    private final String workingDir = Environment.getExternalStorageDirectory().getPath()+"/Pictures/MyApp";
    private final File workingDirFile = new File(workingDir);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        EditText findText = findViewById(R.id.findText);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = findText.getText().toString();
                if(input.length() == 0) return;

                Intent intent = new Intent(FindActivity.this, FileListActivity.class);
                intent.putExtra("input", input);
                startActivity(intent);
            }
        });
    }
}