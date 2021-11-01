package com.vibe_viroma.cropnutrient.content.champs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.vibe_viroma.cropnutrient.R;
import com.vibe_viroma.cropnutrient.content.MainActivity;
import com.vibe_viroma.cropnutrient.login.signin;
import com.vibe_viroma.cropnutrient.tools.Cte;
import com.vibe_viroma.cropnutrient.tools.PrefManager;

import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Analyse extends AppCompatActivity {

    private TextView tv_analyse;
    private CircularProgressBar circularProgressBar;
    private int duration= 6000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);

        getSupportActionBar().setTitle("Résultats de l'analyse ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        duration = duration + new Random().nextInt(1000);
        initView();
        setupCircleProgress();
    }

    private void initView(){
        circularProgressBar = findViewById(R.id.circularProgressBar);
        tv_analyse= findViewById(R.id.tv_analyse);

    }

    private void setupCircleProgress(){
// Set Progress
        //circularProgressBar.setProgress(65f);
// or with animation
        circularProgressBar.setProgressWithAnimation(1f, (long)duration);
        circularProgressBar.setIndeterminateMode(true);
// Set Progress Max
        circularProgressBar.setProgressMax(300f);

// Set ProgressBar Color
        circularProgressBar.setProgressBarColor(Color.BLACK);
// or with gradient
        circularProgressBar.setProgressBarColorStart(Color.GRAY);
        circularProgressBar.setProgressBarColorEnd(Color.GREEN);
        circularProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

// Set background ProgressBar Color
        circularProgressBar.setBackgroundProgressBarColor(Color.GRAY);
// or with gradient
        circularProgressBar.setBackgroundProgressBarColorStart(Color.WHITE);
        circularProgressBar.setBackgroundProgressBarColorEnd(Color.GREEN);
        circularProgressBar.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

// Set Width
        circularProgressBar.setProgressBarWidth(7f); // in DP
        circularProgressBar.setBackgroundProgressBarWidth(3f); // in DP

// Other
        circularProgressBar.setRoundBorder(true);
        circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_analyse.setText("<<< Resultats de l'analyse >>>");
                circularProgressBar.setIndeterminateMode(false);
                tv_analyse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showResultat();
                    }
                });
                showResultat();
            }
        }, duration);
    }

    private void showResultat(){
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setContentText("Selon nos analyses, ce domaine est en manque de :\n-NPB\n-NKS\n-KSB\nRisque de vérité: 95%")
                .showContentText(true)
                .setTitleText("Analyse terminée en "+(duration/1000)+" s")
                .setCancelText("Nos conseils")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        onBackPressed();
                    }
                })
                .setConfirmText("OK")
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return true;
    }
}
