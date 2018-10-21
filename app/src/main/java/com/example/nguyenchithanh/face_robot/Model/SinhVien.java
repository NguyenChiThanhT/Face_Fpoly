package com.example.nguyenchithanh.face_robot.Model;

public class SinhVien {
    public String MaSSV;
    public String Ten;
    public String SDT;

    public SinhVien(String maSSV, String ten, String SDT) {
        MaSSV = maSSV;
        Ten = ten;
        this.SDT = SDT;
    }

    public String getMaSSV() {
        return MaSSV;
    }

    public void setMaSSV(String maSSV) {
        MaSSV = maSSV;
    }

    public String getTen() {
        return Ten;
    }

    public void setTen(String ten) {
        Ten = ten;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }
}
