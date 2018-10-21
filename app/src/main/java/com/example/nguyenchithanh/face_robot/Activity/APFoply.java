package com.example.nguyenchithanh.face_robot.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nguyenchithanh.face_robot.R;

public class APFoply extends AppCompatActivity {
    Button ap,lms,tt;
    String LMS = "http://lms.poly.edu.vn";
    String AP = "http://ap.poly.edu.vn/index.php";
    String TT = "https://caodang.fpt.edu.vn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apfoply);
        ap = (Button) findViewById(R.id.AP);
        lms = (Button) findViewById(R.id.LMS);
        tt = (Button) findViewById(R.id.TinTuc);

    }
    public void AP(View view){
        Intent i = new Intent(APFoply.this,Webview.class);
        i.putExtra("getweb",AP);
        startActivity(i);
    }
    public void LMS(View view){
        Intent i = new Intent(APFoply.this,Webview.class);
        i.putExtra("getweb",LMS);
        startActivity(i);
    }
    public void TinTuc(View view){
        Intent i = new Intent(APFoply.this,Webview.class);
        i.putExtra("getweb",TT);
        startActivity(i);
    }
}
