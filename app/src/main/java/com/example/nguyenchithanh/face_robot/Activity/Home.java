package com.example.nguyenchithanh.face_robot.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.nguyenchithanh.face_robot.R;

public class Home extends AppCompatActivity {
    ImageView imvHCM,imvHN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        imvHCM = (ImageView)findViewById(R.id.hcm);
        imvHN = (ImageView)findViewById(R.id.HN);
        imvHCM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this,MainActivity.class);
                startActivity(i);
            }
        });
        imvHN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this,APFoply.class);
                startActivity(i);
            }
        });
    }
}
