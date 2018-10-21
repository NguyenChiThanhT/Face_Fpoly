//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.example.nguyenchithanh.face_robot.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nguyenchithanh.face_robot.Model.MaST;
import com.example.nguyenchithanh.face_robot.Model.SinhVien;
import com.example.nguyenchithanh.face_robot.R;
import com.example.nguyenchithanh.face_robot.helper.LogHelper;
import com.example.nguyenchithanh.face_robot.helper.SampleApp;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class IdentificationActivity extends AppCompatActivity {
   Server server;
    TextToSpeech TTS;
    ListView lvidentification;
   String gmail;
    ArrayList<SinhVien> arrayListSinhvien;
    SinhvienAdapter adapter;
    // Background task of face identification.
    private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]> {
        private boolean mSucceed = true;
        String mPersonGroupId;
        IdentificationTask(String personGroupId) {
            this.mPersonGroupId = personGroupId;
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... params) {
            String logString = "Request: Identifying faces ";
            for (UUID faceId: params) {
                logString += faceId.toString() + ", ";
            }
            logString += " in group " + mPersonGroupId;
            addLog(logString);

            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Getting person group status...");

                TrainingStatus trainingStatus = faceServiceClient.getLargePersonGroupTrainingStatus(
                        this.mPersonGroupId);     /* personGroupId */
                if (trainingStatus.status != TrainingStatus.Status.Succeeded) {
                    publishProgress("Person group training status is " + trainingStatus.status);
                    mSucceed = false;
                    return null;
                }

                publishProgress("Identifying...");

                // Start identification.
                return faceServiceClient.identityInLargePersonGroup(
                        this.mPersonGroupId,   /* personGroupId */
                        params,                  /* faceIds */
                        1);  /* maxNumOfCandidatesReturned */
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background detection task on screen.a
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(IdentifyResult[] result) {
            // Show the result on screen when detection is done.
            setUiAfterIdentification(result, mSucceed);
        }
    }

    String mPersonGroupId;
    String mMaST;
    String prefname="my_data";
    String prefname2="data_gmail";
//    mPersonGroupId = "2c719dbd-5472-d233-e409-dac5c13e7a65";
    boolean detected;

   // FaceListAdapter mFaceListAdapter;

   // PersonGroupListAdapter mPersonGroupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        lvidentification = (ListView) findViewById(R.id.lvidentification);
        arrayListSinhvien = new ArrayList<>();
        adapter = new SinhvienAdapter(this,R.layout.item_identification, arrayListSinhvien);
        lvidentification.setAdapter(adapter);

        //texttospeech(av);
           getPersonGroup();
        detected = false;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.progress_dialog_title));

        LogHelper.clearIdentificationLog();




        Bundle extras = getIntent().getExtras();
        if(extras == null){
            getGmail();
        }
        else{
            bmp = (Bitmap) extras.getParcelable("bmp_Image");
            //ImageView imageView = (ImageView) findViewById(R.id.image);
            //imageView.setImageBitmap(bmp);
            // imageView.setImageBitmap(bmp);
            // Clear the identification result.

            // Clear the information panel.

            // Start detecting in image.
            detect(bmp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void setUiBeforeBackgroundTask() {
        progressDialog.show();
    }

    // Show the status of background detection task on screen.
    private void setUiDuringBackgroundTask(String progress) {
        progressDialog.setMessage(progress);


    }

    // Show the result on screen when detection is done.
    private void setUiAfterIdentification(IdentifyResult[] result, boolean succeed) {
        progressDialog.dismiss();

//        setAllButtonsEnabledStatus(true);
//        setIdentifyButtonEnabledStatus(false);

        if (succeed) {
            // Set the information about the detection result.


            if (result != null) {

                for(IdentifyResult identifyResult: result){
                    if(identifyResult.candidates.size() > 0){
                        String key = identifyResult.candidates.get(0).personId.toString();
                        GetData2(key);
                    }
                    else {
                        //Toast.makeText(this, "Unknown Person", Toast.LENGTH_SHORT).show();
                        DiaLogFail();
                    }
                }

            }

        }
    }

    // Background task of face detection.
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Detecting...");

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        null);
            }  catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background detection task on screen.
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            progressDialog.dismiss();

           // setAllButtonsEnabledStatus(true);

            if (result != null) {
                // Set the adapter of the ListView which contains the details of detected faces.
//                mFaceListAdapter = new FaceListAdapter(result);
//                ListView listView = (ListView) findViewById(R.id.list_identified_faces);
//                listView.setAdapter(mFaceListAdapter);

                if (result.length == 0) {
                    detected = false;

                } else {
                    detected = true;

                }
            } else {
                detected = false;
            }
            sosanh(result);
           // refreshIdentifyButtonEnabledStatus();
        }
    }

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The image selected to detect.
    private Bitmap bmp;

    // Progress dialog popped up when communicating with server.
    ProgressDialog progressDialog;
    private void getGmail() {
        SharedPreferences pre=getSharedPreferences
                (prefname2,MODE_PRIVATE);
        gmail =pre.getString("gmail", "");
        Getgmail(gmail);
    }
    private void getPersonGroup() {
        SharedPreferences pre=getSharedPreferences
                (prefname,MODE_PRIVATE);
        mPersonGroupId =pre.getString("mPersonGroupId", "");
        mMaST =pre.getString("MaST", "");

    }
    // Start detecting in image.
    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        //setAllButtonsEnabledStatus(false);

        // Start a background task to detect faces in the image.
        new DetectionTask().execute(inputStream);
    }

    public void sosanh(Face[] result) {
        // Start detection task only if the image to detect is selected.
        if (detected && mPersonGroupId != null) {
            // Start a background task to identify faces in the image.
            List<UUID> faceIds = new ArrayList<>();
            for (Face face:  result) {
                faceIds.add(face.faceId);
            }

           // setAllButtonsEnabledStatus(false);

            new IdentificationTask(mPersonGroupId).execute(
                    faceIds.toArray(new UUID[faceIds.size()]));
        } else {
            // Not detected or person group exists.

        }
    }


    private void GetData2(final String keyperson) {
        RequestQueue requestQuee = Volley.newRequestQueue(this);
        String urldata = "http://" + server.localhost + "/haha/android/getnameperson.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urldata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String MaSSV;
                String Ten;
                String SDT;
                if(response !=null){
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0 ; i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String av = jsonObject.getString("MaSSV");
                            MaSSV =  jsonObject.getString("MaSSV");
                            Ten = jsonObject.getString("Ten");
                            SDT = jsonObject.getString("SDT");
                            arrayListSinhvien.add(new SinhVien(MaSSV,Ten,SDT));
                            texttospeech(Ten);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(IdentificationActivity.this,av, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<String,String>();
                param.put("KeyPerson",keyperson);
                return param;
            }
        };
        requestQuee.add(stringRequest);
    }
    private void Getgmail(final String gmail) {

        RequestQueue requestQuee = Volley.newRequestQueue(this);
        String urldata = "http://" + server.localhost + "/haha/android/getgmail.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urldata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String MaSSV;
                String Ten;
                String SDT;
                if(response !=null){
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0 ; i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String av = jsonObject.getString("MaSSV");
                            MaSSV =  jsonObject.getString("MaSSV");
                            Ten = jsonObject.getString("Ten");
                            SDT = jsonObject.getString("SDT");
                            arrayListSinhvien.add(new SinhVien(MaSSV,Ten,SDT));
                            texttospeech(Ten);
                            adapter.notifyDataSetChanged();
                           // Toast.makeText(IdentificationActivity.this,av, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<String,String>();
                param.put("KeyPerson",gmail);
                return param;
            }
        };
        requestQuee.add(stringRequest);


    }
    public void texttospeech(final String Ten){
        final String talk = "Xin Chào bạn" + Ten;
        TTS = new TextToSpeech(IdentificationActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // nếu status ko bị lỗi (thì đọc được)
                if (status != TextToSpeech.ERROR)
                {
                    // set Locale Language
                    TTS.setLanguage(Locale.getDefault());
                    // Speak Now
                    TTS.speak(talk, TextToSpeech.QUEUE_FLUSH , null);
                    // set EditText null

                }
            }
        });
    }

    //dailog fail
    private void DiaLogFail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Không tìm thấy người giống bạn!");
        builder.setMessage("Xin mời Login lại!!!");
        builder.setCancelable(true);

        final AlertDialog alertDialog= builder.create();

        alertDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                alertDialog.dismiss();
                Intent intent = new Intent(IdentificationActivity.this, Login_Face_Gmail.class);
                startActivity(intent);
                finish();

            }
        }, 3000);
        final String talkv = "Xin lỗi Hệ Thống Không Nhận Dạng Được Bạn ";
        TTS = new TextToSpeech(IdentificationActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // nếu status ko bị lỗi (thì đọc được)
                if (status != TextToSpeech.ERROR)
                {
                    // set Locale Language
                    TTS.setLanguage(Locale.getDefault());
                    // Speak Now
                    TTS.speak(talkv, TextToSpeech.QUEUE_FLUSH , null);
                    // set EditText null

                }
            }
        });

    }


    //adapter
    public class SinhvienAdapter extends BaseAdapter {
        private Context context;
        private int layout;
        public List<SinhVien> SVList;

        public SinhvienAdapter(Context context, int layout, List<SinhVien> SVList) {
            this.context = context;
            this.layout = layout;
            this.SVList = SVList;
        }

        @Override
        public int getCount() {
            return SVList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
        private class  ViewHolder{
            TextView txtmassv,txtten,txtsdt;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View View, ViewGroup parent) {
            ViewHolder holder;
            if(View == null){
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View = inflater.inflate(layout,null);

                holder.txtmassv = (TextView) View.findViewById(R.id.txtmassv);
                holder.txtten = (TextView) View.findViewById(R.id.txtten);
                holder.txtsdt = (TextView) View.findViewById(R.id.txtsdt);
                View.setTag(holder);

            }
            else {
                holder = (ViewHolder) View.getTag();
            }
            SinhVien sv = SVList.get(position);

            holder.txtmassv.setText(sv.getMaSSV());
            holder.txtten.setText(sv.getTen());
            holder.txtsdt.setText(sv.getSDT());
            return View;
        }
    }

    private void addLog(String log) {
        LogHelper.addIdentificationLog(log);
    }





}
