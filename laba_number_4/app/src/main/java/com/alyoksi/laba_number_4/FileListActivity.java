package com.alyoksi.laba_number_4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


public class FileListActivity extends AppCompatActivity {

    private final String workingDir = Environment.getExternalStorageDirectory().getPath()+"/Pictures/MyApp";
    private final File workingDirFile = new File(workingDir);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView noFilesText = findViewById(R.id.no_files_found);

        // String path = getIntent().getStringExtra("path");
        String input = getIntent().getStringExtra("input");

        File[] files = new File[0];
        // tag
        assert input != null;
        if(input.charAt(0) == '@'){
            if(input.length() > 1){
                files = getFilesByTag(input.substring(1));
                if(files == null){
                    Toast.makeText(FileListActivity.this, "Файлов с таким тегом не было найдено", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        // description
        else if(input.charAt(0) == '#'){
            if(input.length() > 1){
                List<File> ListFiles = null;
                try {
                    ListFiles = getFilesByDescription(input.substring(1));
                } catch (IOException e) {
                    Toast.makeText(FileListActivity.this, "Не удалось найти файлы с нужным описанием", Toast.LENGTH_SHORT).show();
                }

                assert ListFiles != null;
                files = new File[ListFiles.size()];
                for(int i = 0; i < ListFiles.size(); ++i)
                    files[i] = ListFiles.get(i);

            }
        }
        // file name
        else{
            List<File> ListFiles = getFilesByNames(input);
            files = new File[ListFiles.size()];

            for(int i = 0; i < ListFiles.size(); ++i)
                files[i] = ListFiles.get(i);
        }


        if(files.length == 0){
            noFilesText.setVisibility(View.VISIBLE);
            return;
        }

        noFilesText.setVisibility(View.INVISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(), files));
    }

    private File[] getFilesByTag(String tag) {
        File[] tmp = workingDirFile.listFiles();
        assert tmp != null;
        for(File file : tmp){
            if(file.getName().equals(tag))
                return file.listFiles();
        }
        return null;
    }

    private List<File> getFilesByDescription(String desc) throws IOException {
        File[] tags = workingDirFile.listFiles();
        List<File> res = new ArrayList<File>();

        assert tags != null;
        for(File tag : tags){
            File[] dirs = tag.listFiles();

            /* Директория с картинкой и ее описанием */
            assert dirs != null;
            for(File dir : dirs){
                File[] files = dir.listFiles();

                boolean flag = false;
                /* Картинка или описание? */
                assert files != null;
                for(File file : files){
                    if(Objects.equals(getFileExtension(file.getName()), ".txt")){
                        String content = readFile(file);

                        /* Если параметр - подстрока описания картинки */
                        if(content.contains(desc))
                            flag = true;
                    }
                }
                if(flag)
                    res.add(dir);
            }
        }
        return res;
    }

    private List<File> getFilesByNames(String name) {
        File[] tags = workingDirFile.listFiles();
        List<File> res = new ArrayList<File>();

        assert tags != null;
        for(File tag : tags){
            File[] files = tag.listFiles() ;
            assert files != null;
            for(File file : files){
                if(file.getName().equals(name)){
                    res.add(file);
                }
            }
        }
        return res;
    }

    private String getFileExtension(String mystr) {
        int index = mystr.indexOf('.');
        return index == -1? null : mystr.substring(index);
    }

    /* From file to string */
    private String readFile(File file) throws IOException {
        StringBuilder fileContents = new StringBuilder((int)file.length());

        try (Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append(System.lineSeparator());
            }
            return fileContents.toString();
        }
    }
}