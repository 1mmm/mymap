package com.example.wanglei.mymap;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedHelper {

    private Context mContext;

    public SharedHelper() {
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }


    //定义一个保存数据的方法
    public void save(String u,String key) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString( u,key);
        editor.apply();
    }

    //定义一个读取SP文件的方法
    public Map<String, String> read(String u) {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        data.put(u, sp.getString(u, ""));
        return data;
    }
}