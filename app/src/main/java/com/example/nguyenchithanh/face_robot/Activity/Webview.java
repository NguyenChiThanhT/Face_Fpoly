package com.example.nguyenchithanh.face_robot.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.nguyenchithanh.face_robot.R;

public class Webview extends AppCompatActivity {
    String getweb;
    private WebView view;

    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
         view= (WebView)findViewById(R.id.webview2);
        view.getSettings().setUserAgentString(USER_AGENT);
        Bundle extras = getIntent().getExtras();
        getweb = extras.getString("getweb");

        view.setWebViewClient(new WebViewClient());
        view.loadUrl(getweb);
    }
}
