package com.alyoksi.laba_number_4;


import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final String workingDir = Environment.getExternalStorageDirectory().getPath()+"/Pictures/MyApp";
    private final File workingDirFile = new File(workingDir);
    public static final String SHARED_PREFS = "sharedPrefs";

    ImageButton capture, toggleFlash, flipCamera;
    PreviewView previewview;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(result){
                startCamera(cameraFacing);
            }
        }
    });

    ImageCapture lastImageCapture;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton storage = findViewById(R.id.storage);
        if(!Environment.isExternalStorageManager()){
            try{
                Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityIfNeeded(intent, 101);
            }
            catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityIfNeeded(intent, 101);
            }
        }

        previewview = findViewById(R.id.cameraPreview);
        capture = findViewById(R.id.capture);
        toggleFlash = findViewById(R.id.toggleFlash);
        flipCamera = findViewById(R.id.flipCamera);

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            activityResultLauncher.launch(android.Manifest.permission.CAMERA);
        } else{
            startCamera(cameraFacing);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", "");
        editor.putString("tag", "");
        editor.putString("desc", "");

        editor.apply();

        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraFacing == CameraSelector.LENS_FACING_BACK)
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                else
                    cameraFacing = CameraSelector.LENS_FACING_BACK;
                startCamera(cameraFacing);
            }
        });

        storage.setOnClickListener(new View.OnClickListener() {
            /* SDK >= 33 */
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, FindActivity.class);
                boolean success = true;
                if(!workingDirFile.exists()){
                    success = workingDirFile.mkdirs();
                }
                if(success) {
                    intent.putExtra("path", workingDir);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String tag = sharedPreferences.getString("tag", "");
        String desc = sharedPreferences.getString("desc", "");


        if(name.equals("")) return;


        if(tag.equals("")){
            tag = "All";
        }

        File file = null;
        file = new File(workingDir + "/" + tag, name+".jpg");
        if(file.exists()){
            Toast.makeText(MainActivity.this, "Файл с таким названием уже существует", Toast.LENGTH_SHORT).show();
        }

        boolean success = file.mkdirs();
        if(!success) return;


        file = new File(workingDir + "/" + tag + "/" + name+".jpg", name+".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        lastImageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Image saved!", Toast.LENGTH_SHORT).show();
                    }
                });
                startCamera(cameraFacing);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Couldn't save image!", Toast.LENGTH_SHORT).show();
                    }
                });
                startCamera(cameraFacing);
            }
        });

        //creating the instance of file
        File path = new File(workingDir + "/" + tag + "/" + name + ".jpg", name+".txt");

        try {
            //passing file instance in filewriter
            FileWriter wr = new FileWriter(path);

            //calling writer.write() method with the string
            wr.write(desc);
            //flushing the writer
            wr.flush();
            //closing the writer
            wr.close();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Не удалось сохранить файл с описанием...", Toast.LENGTH_SHORT).show();
        }

        clearData();

    }

    private void clearData(){
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", "");
        editor.putString("tag", "");
        editor.putString("desc", "");

        editor.apply();
    }

    public void startCamera(int cameraFacing){
        int aspectRatio = aspectRatio(previewview.getWidth(), previewview.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                capture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastImageCapture = imageCapture;
                        takePicture(imageCapture);
                    }
                });

                toggleFlash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFlashIcon(camera);
                    }
                });

                preview.setSurfaceProvider(previewview.getSurfaceProvider());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture(ImageCapture imageCapture){

        Intent intent = new Intent(this, SaveFileInfoActivity.class);
        startActivity(intent);

    }

    private void setFlashIcon(Camera camera){
        if(camera.getCameraInfo().hasFlashUnit()){
            if(camera.getCameraInfo().getTorchState().getValue() == 0){
                camera.getCameraControl().enableTorch(true);
                toggleFlash.setImageResource(R.drawable.flash_on);
            } else {
                camera.getCameraControl().enableTorch(false);
                toggleFlash.setImageResource(R.drawable.flash_off);
            }
        } else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Фонарик не доступен", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private int aspectRatio(int width, int height){
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if(Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)){
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

}

