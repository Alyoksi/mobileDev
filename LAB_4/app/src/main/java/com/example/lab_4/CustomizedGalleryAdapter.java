package com.example.lab_4;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomizedGalleryAdapter extends BaseAdapter {

    private final Context context;
    private String currentImagePath;
    private int[] images;
    private final HashMap<Integer, List<String>> imageTags;
    private List<Bitmap> imageBitmaps = new ArrayList<>(); // Список для хранения объекта Bitmap (фото)

    public CustomizedGalleryAdapter(Context c, int[] images) {
        context = c;
        this.images = images;
        this.imageTags = new HashMap<>(); // Инициализируем HashMap
        // Добавляем теги для каждого изображения
        imageTags.put(R.drawable.graduation, Arrays.asList("kanye", "rap", "goat"));
        imageTags.put(R.drawable.dre2001, Arrays.asList("dre", "rap"));
        imageTags.put(R.drawable.goodkid, Arrays.asList("gkmc", "goat"));
    }

    // Метод для обновления отображаемых изображений по выбранным тегам
    // Проходим по всем изображениям и добавляем те, которые соответствуют выбранному тегу
    // Обновляем массив images с отфильтрованными изображениями
    public void updateImagesByTag(String tag) {
        List<Integer> filteredImages = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : imageTags.entrySet()) {
            if (entry.getValue().contains(tag)) {
                filteredImages.add(entry.getKey());
            }
        }
        this.images = filteredImages.stream().mapToInt(i->i).toArray();
        notifyDataSetChanged(); // Обновляем отображение
    }

    // возвращает количество изображений
    public int getCount() {
        return images.length;
    }

    // возвращает элемент, в нашем примере это изображение
    public Object getItem(int position) {
        return position;
    }

    // возвращает ID элемента
    public long getItemId(int position) {
        return position;
    }

    // возвращает представление ImageView
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
        } else {
            imageView = (ImageView) convertView;
        }

        if (position < images.length) {
            imageView.setImageResource(images[position]);
        } else {
            if (currentImagePath != null) {
                Bitmap imageBitmap = loadImageFromInternalStorage(currentImagePath);
                if (imageBitmap != null) {
                    imageView.setImageBitmap(imageBitmap);
                    imageView.invalidate(); // Добавляем эту строку для обновления отображения ImageView
                }
            }
        }

        imageView.setLayoutParams(new Gallery.LayoutParams(200, 200));
        return imageView;
    }


    public Bitmap loadImageFromInternalStorage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                return bitmap;
            }
        }
        return null;
    }

    String saveImageToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        // Получаем или создаем папку для хранения изображений
        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        if (!directory.exists()) directory.mkdirs(); // Проверяем наличие каталога и создаем его, если не существует
        // Создаем файл для сохранения изображения
        File myPath = new File(directory, "my_image.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Сохраняем Bitmap в файл
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            currentImagePath = myPath.getAbsolutePath(); // Сохраняем путь в глобальной переменной
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return currentImagePath; // Возвращаем путь к сохраненному файлу
    }
}
