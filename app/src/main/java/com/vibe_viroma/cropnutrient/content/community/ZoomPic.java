package com.vibe_viroma.cropnutrient.content.community;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;
import com.vibe_viroma.cropnutrient.R;

import java.io.File;

public class ZoomPic extends AppCompatActivity {

    private String imageLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_pic);
        TouchImageView timg= findViewById(R.id.imageVw);
        imageLink= getIntent().getExtras().getString("PATH", "");
        if(imageLink.startsWith("http")){
            Glide.with(this).load(imageLink).into(timg);
        }else{
            timg.setImageURI(Uri.fromFile(new File(imageLink)));
        }
    }
}
