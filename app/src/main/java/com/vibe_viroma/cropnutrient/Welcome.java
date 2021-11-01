package com.vibe_viroma.cropnutrient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.vibe_viroma.cropnutrient.content.MainActivity;
import com.vibe_viroma.cropnutrient.login.login;
import com.vibe_viroma.cropnutrient.tools.MyBounceInterpolator;

public class Welcome extends AppCompatActivity {
    private TextView mContentView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mAuth= FirebaseAuth.getInstance();
        initViews();
        goToNextAct();
    }

    private void initViews(){
        mContentView= findViewById(R.id.content);
    }


    public void goToNextAct(){
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.4, 20);
        myAnim.setInterpolator(interpolator);
        mContentView.startAnimation(myAnim);


        Thread th= new Thread(){
            @Override
            public void run() {
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    if(mAuth.getCurrentUser()==null){
                        startActivity(new Intent(Welcome.this, login.class));
                    }else {
                        startActivity(new Intent(Welcome.this, MainActivity.class));
                    }
                    finish();
                }
            }
        };
        th.start();
    }

}
