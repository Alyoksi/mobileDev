package com.alyoksi.lab2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String MAX_PTS = "MAX_PTS";
    public static final String PLAYER_PTS = "PLAYER_PTS";
    public static final String BOT_PTS = "BOT_PTS";
    private TextView result, showPlayerPts, showBotPts;
    private ImageButton rockButton, paperButton, scissorsButton;
    public static int maxPts;
    public static int playerPts, botPts;
    Random random = new Random();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        rockButton = findViewById(R.id.rockButton);
        paperButton = findViewById(R.id.paperButton);
        scissorsButton = findViewById(R.id.scissorsButton);

        showPlayerPts = findViewById(R.id.playerPts);
        showBotPts = findViewById(R.id.botPts);

        loadData();

        result.setText("Max pts : " + maxPts);
        showPlayerPts.setText(String.valueOf("Player pts : " + playerPts));
        showBotPts.setText(String.valueOf("Bot pts : " + botPts));

        rockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rockClick(v, random.nextInt(1000) % 3);
            }
        });
        paperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paperClick(v, random.nextInt(1000) % 3);
            }
        });
        scissorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scissorsClick(v, random.nextInt(1000) % 3);
            }
        });
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        maxPts = sharedPreferences.getInt(MAX_PTS, 3);
        playerPts = sharedPreferences.getInt(PLAYER_PTS, 0);
        botPts = sharedPreferences.getInt(BOT_PTS, 0);
    }

    private void showWinLose(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(text)
                .setMessage("Хотите продолжить?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putInt(MainActivity.PLAYER_PTS, 0);
                        editor.putInt(MainActivity.BOT_PTS, 0);
                        editor.apply();

                        botPts = 0;
                        playerPts = 0;

                        showPlayerPts.setText(String.valueOf("Player pts : " + playerPts));
                        showBotPts.setText(String.valueOf("Bot pts : " + botPts));
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putInt(MainActivity.PLAYER_PTS, 0);
                        editor.putInt(MainActivity.BOT_PTS, 0);
                        editor.apply();

                        finishAffinity();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    void rockClick(View v, int botChoice){
        if(botChoice == 0){
            Toast.makeText(this, "Ничья", Toast.LENGTH_SHORT).show();
        }
        else if(botChoice == 1){
            botPts++;
            saveScore(v);
            if(botPts == maxPts){
                showWinLose("К сожалению, Вы проиграли...");
                return;
            }
            showBotPts.setText(String.valueOf("Bot pts : " + botPts));
            Toast.makeText(this, "Бот победил", Toast.LENGTH_SHORT).show();
        }
        else{
            playerPts++;
            saveScore(v);
            if(playerPts == maxPts){
                showWinLose("Поздравляю, Вы победили!!!");
                return;
            }
            showPlayerPts.setText(String.valueOf("Player pts : " + playerPts));
            Toast.makeText(this, "Игрок победил", Toast.LENGTH_SHORT).show();
        }
    }
    void paperClick(View v, int botChoice){
        if(botChoice == 0){
            playerPts++;
            if(playerPts == maxPts){
                showWinLose("Поздравляю, Вы победили!!!");
                return;
            }
            saveScore(v);
            showPlayerPts.setText(String.valueOf("Player pts : " + playerPts));
            Toast.makeText(this, "Игрок победил", Toast.LENGTH_SHORT).show();
        }
        else if(botChoice == 1){
            Toast.makeText(this, "Ничья", Toast.LENGTH_SHORT).show();
        }
        else{
            botPts++;
            if(botPts == maxPts){
                showWinLose("К сожалению, Вы проиграли...");
                return;
            }
            saveScore(v);
            showBotPts.setText(String.valueOf("Bot pts : " + botPts));
            Toast.makeText(this, "Бот победил", Toast.LENGTH_SHORT).show();
        }
    }
    void scissorsClick(View v, int botChoice){
        if(botChoice == 0){
            botPts++;
            if(botPts == maxPts){
                showWinLose("К сожалению, Вы проиграли...");
                return;
            }
            saveScore(v);
            showBotPts.setText(String.valueOf("Bot pts : " + botPts));
            Toast.makeText(this, "Бот победил", Toast.LENGTH_SHORT).show();
        }
        else if(botChoice == 1){
            playerPts++;
            if(playerPts == maxPts){
                showWinLose("Поздравляю, Вы победили!!!");
                return;
            }
            saveScore(v);
            showPlayerPts.setText(String.valueOf("Player pts : " + playerPts));
            Toast.makeText(this, "Игрок победил", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Ничья", Toast.LENGTH_SHORT).show();
        }
    }

    void saveScore(View v){
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(MainActivity.PLAYER_PTS, playerPts);
        editor.putInt(MainActivity.BOT_PTS, botPts);
        editor.apply();
    }

    public void openSettings(View v){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}