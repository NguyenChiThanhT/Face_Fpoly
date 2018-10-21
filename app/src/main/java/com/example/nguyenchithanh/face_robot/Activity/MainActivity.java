package com.example.nguyenchithanh.face_robot.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.nguyenchithanh.face_robot.Adapter.MaSTAdapter;
import com.example.nguyenchithanh.face_robot.Model.MaST;
import com.example.nguyenchithanh.face_robot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Server server;
    String urlgetdata = "http://"+server.localhost+"/haha/android/getMaST.php";
    ListView lvSinhvien;

    ArrayList<MaST> arrayListSinhvien;
    MaSTAdapter adapter;
    String prefname="my_data";
    //String key="e360f749-08d8-4a3e-a112-74098ef6bd75";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvSinhvien = (ListView) findViewById(R.id.lv);
        arrayListSinhvien = new ArrayList<>();
        adapter = new MaSTAdapter(this,R.layout.item_mast, arrayListSinhvien);
        lvSinhvien.setAdapter(adapter);

        GetData(urlgetdata);

        lvSinhvien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pre = getSharedPreferences(prefname, MODE_PRIVATE);
                SharedPreferences.Editor editor=pre.edit();
                editor.clear();
                String MaST = adapter.maSTList.get(position).getMaST();
                String KeyAPI = adapter.maSTList.get(position).getKeyAPI();
                editor.putString("mPersonGroupId", KeyAPI);
                editor.putString("MaST", MaST);
                editor.commit();
                Intent i = new Intent(MainActivity.this,Login_Face_Gmail.class);
                startActivity(i);
                //Toast.makeText(GetData.this, Lop+" is available", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void GetData(String url) {
        RequestQueue requestQuee = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0; i<response.length();i++){
                            try {
                                JSONObject object = response.getJSONObject(i);

                                arrayListSinhvien.add(new MaST(
                                        object.getString("MaST"),
                                        object.getString("KeyAPI")
                                ));
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
//                        Toast.makeText(MainActivity.this,, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQuee.add(jsonArrayRequest);

    }
}