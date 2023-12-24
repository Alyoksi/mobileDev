package com.alyoksi.lab4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

public class FileListActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView noFilesText = findViewById(R.id.notfile_textview);

        String path = getIntent().getStringExtra("path");
        File root = new File(path);
        File[] fileAndFolders = root.listFiles();

        if(fileAndFolders == null || fileAndFolders.length == 0){
            noFilesText.setVisibility(View.VISIBLE);
            return;
        }
        // Toast.makeText(this, "NICE", Toast.LENGTH_SHORT).show();
        noFilesText.setVisibility(View.INVISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileAdapter(getApplicationContext(), fileAndFolders));
    }

}
