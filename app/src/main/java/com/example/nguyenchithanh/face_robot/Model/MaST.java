package com.example.nguyenchithanh.face_robot.Model;

public class MaST {
    public String MaST;
    public  String KeyAPI;

    public MaST(String maST, String keyAPI) {
        MaST = maST;
        KeyAPI = keyAPI;
    }

    public String getMaST() {
        return MaST;
    }

    public void setMaST(String maST) {
        MaST = maST;
    }

    public String getKeyAPI() {
        return KeyAPI;
    }

    public void setKeyAPI(String keyAPI) {
        KeyAPI = keyAPI;
    }
}
