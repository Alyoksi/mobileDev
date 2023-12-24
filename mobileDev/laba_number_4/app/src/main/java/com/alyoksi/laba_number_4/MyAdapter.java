package com.alyoksi.laba_number_4;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    Context context;
    File[] filesAndFolders;

    public MyAdapter(Context context, File[] filesAndFolders){
        this.context = context;
        this.filesAndFolders = filesAndFolders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());

        holder.imageView.setImageResource(R.drawable.image_icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, selectedFile.getPath(), Toast.LENGTH_SHORT).show();

                /* Открываем картинку с описанием */
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                File[] listFile = selectedFile.listFiles();
                assert Objects.requireNonNull(listFile).length == 2;
                for(File file : listFile){
                    if(Objects.equals(getFileExtension(file.getName()), ".txt")) {
                        try {
                            String description = readFile(file);
                            if(description.equals(""))
                                intent.putExtra("description", "this is a new file");
                            else
                                intent.putExtra("description", description);
                        } catch (IOException e) {
                            Toast.makeText(context, "Не удалось открыть файл", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    else{
                        intent.putExtra("file", file.getPath());
                    }
                }
                context.startActivity(intent);
            }
        });
    }

    private String readFile(File file) throws IOException {
        StringBuilder fileContents = new StringBuilder((int)file.length());

        try (Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append(System.lineSeparator());
            }
            return fileContents.toString();
        }
    }

    private String getFileExtension(String mystr) {
        int index = mystr.indexOf('.');
        return index == -1? null : mystr.substring(index);
    }

    @Override
    public int getItemCount() {
        return filesAndFolders.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView){
            super(itemView);

            textView = itemView.findViewById(R.id.name_view);
            imageView = itemView.findViewById(R.id.icon_view);
        }
    }
}
