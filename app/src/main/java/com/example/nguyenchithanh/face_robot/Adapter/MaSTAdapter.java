package com.example.nguyenchithanh.face_robot.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nguyenchithanh.face_robot.Model.MaST;
import com.example.nguyenchithanh.face_robot.R;

import java.util.List;

public class MaSTAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    public List<MaST> maSTList;

    public MaSTAdapter(Context context, int layout, List<MaST> maSTList) {
        this.context = context;
        this.layout = layout;
        this.maSTList = maSTList;
    }

    @Override
    public int getCount() {
        return maSTList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
private class  ViewHolder{
        TextView txtMaST,txtmassv,txtten,txtsdt;
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
           holder.txtMaST = (TextView) View.findViewById(R.id.tvmast);

           View.setTag(holder);

       }
       else {
           holder = (ViewHolder) View.getTag();
       }
       MaST maST = maSTList.get(position);
           holder.txtMaST.setText(maST.getMaST());

           return View;
    }
}
