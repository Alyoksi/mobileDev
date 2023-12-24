package com.alyoksi.laba_number_4;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class ViewImageActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        imageView = findViewById(R.id.curImage);
        textView = findViewById(R.id.description);

        String file = getIntent().getStringExtra("file");
        String description = getIntent().getStringExtra("description");
        if(file == null)
            textView.setText("File is null");
        else
            textView.setText(description);

        Uri uri = Uri.parse("file://" + file);
        imageView.setImageURI(uri);
    }
}