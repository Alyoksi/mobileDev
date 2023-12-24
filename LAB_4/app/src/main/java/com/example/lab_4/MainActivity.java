package com.example.lab_4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Gallery simpleGallery;
    CustomizedGalleryAdapter customGalleryAdapter;
    ImageView selectedImageView;

    int[] images = {
            R.drawable.graduation,
            R.drawable.dre2001,
            R.drawable.care,
            R.drawable.getrich,
            R.drawable.illmatic,
            R.drawable.blueprint,
            R.drawable.yeezus,
            R.drawable.goodkid,
            R.drawable.paris,
            R.drawable.faces
    };
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleGallery = (Gallery) findViewById(R.id.languagesGallery);

        selectedImageView = (ImageView) findViewById(R.id.imageView);

        customGalleryAdapter = new CustomizedGalleryAdapter(getApplicationContext(), images);


        simpleGallery.setAdapter(customGalleryAdapter);

        simpleGallery.setOnItemClickListener((parent, view, position, id) -> {
            // Какое бы изображение ни было выбрано, оно устанавливается в selectedImageView
            // позиция будет указывать на расположение изображения
            selectedImageView.setImageResource(images[position]);
        });

        // Находим кнопку "Search" по ее идентификатору
        Button searchButton = findViewById(R.id.buttonSearch);

        // Привязываем обработчик нажатия на кнопку
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchButtonClick();
            }
        });

        // Находим кнопку "Camera" по ее идентификатору
        Button cameraButton = findViewById(R.id.buttonCamera);

        // Привязываем обработчик нажатия на кнопку
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }

    // Обработчик нажатия на кнопку "Search"
    public void onSearchButtonClick() {
        EditText editTextTag = findViewById(R.id.editTextTag);
        String tag = editTextTag.getText().toString();
        customGalleryAdapter.updateImagesByTag(tag);
    }

    // Метод для вызова камеры
    private void dispatchTakePictureIntent() {
        // Создание намерения для захвата изображения с помощью встроенного приложения камера
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            customGalleryAdapter.saveImageToInternalStorage(imageBitmap);
        }
    }

}