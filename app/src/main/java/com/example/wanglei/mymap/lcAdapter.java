package com.example.wanglei.mymap;

/**
 * Created by 2mmm on 2018/1/25.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.wanglei.mymap.Main2Activity.user;
import static com.example.wanglei.mymap.MainActivity.h1;
import static com.example.wanglei.mymap.MainActivity.h2;
import static com.example.wanglei.mymap.MainActivity.h3;
import static com.example.wanglei.mymap.MainActivity.h4;
import static com.example.wanglei.mymap.MainActivity.h5;

/**
 * Created by 2mmm on 2017/9/14.
 */

public class lcAdapter extends BaseAdapter {
    private LinkedList<lc> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public String base_url="http://39.107.93.96/";
    private final OkHttpClient client = new OkHttpClient();

    public lcAdapter(Context context,LinkedList<lc> data){
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }
    public final class Zujian{
        public TextView name;
        public TextView jd;
        public TextView wd;
        public TextView jg;
        public TextView ms;
        public Button xx;
        public Button js;

    }

    @Override
    public int getCount() {
        return data.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Zujian zujian=null;
        zujian=new Zujian();
        //获得组件，实例化组件
        convertView=layoutInflater.inflate(R.layout.list_lc, parent,false);
        zujian.name=(TextView)convertView.findViewById(R.id.id);
        zujian.jd=(TextView)convertView.findViewById(R.id.jd);
        zujian.wd=(TextView)convertView.findViewById(R.id.wd);
        zujian.jg=(TextView)convertView.findViewById(R.id.jg);
        zujian.ms=(TextView)convertView.findViewById(R.id.ms);
        zujian.xx=(Button) convertView.findViewById(R.id.xx);
        zujian.js=(Button) convertView.findViewById(R.id.js);

        //绑定数据
        String h=data.get(position).getname()+"";
        zujian.name.setText(h);
        h=data.get(position).getjd()+"";
        zujian.jd.setText(h);
        h=data.get(position).getwd()+"";
        zujian.wd.setText(h);
        h=data.get(position).getjg()+"";
        zujian.jg.setText(h);
        h=data.get(position).getms()+"";
        zujian.ms.setText(h);
        zujian.xx.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                h1=data.get(position).getjd()+"";
                h2=data.get(position).getwd()+"";
                h3="\n价格:"+data.get(position).getjg()+"";
                h4="\n剩余车位:"+data.get(position).getms()+"";
                h5=data.get(position).getname()+"";
            }
        });
        zujian.js.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Random rand=new Random();
                final int num=rand.nextInt(100);
                Toast.makeText(context, "成功在"+data.get(position).getname()+"号停车场预约"+num+"号停车位", Toast.LENGTH_LONG).show();
                new Thread() {
                    public void run() {
                        try{
                            FormBody.Builder pa = new FormBody.Builder();
                            pa.add("id",data.get(position).getname()+"");
                            pa.add("pid",user+"");
                            pa.add("type",1+"");
                            pa.add("cw",num+"");
                            post(pa, "ass.php");
                        }catch (Exception e) {
                        }
                    }
                }.start();
            }
        });
        return convertView;
    }

    String post(FormBody.Builder pa, String UR) throws Exception {
        //post方法接收一个RequestBody对象
        //create方法第一个参数都是MediaType类型，create方法的第二个参数可以是String、File、byte[]或okio.ByteString

        Request request = new Request.Builder()
                .url(base_url+UR)
                .post(pa.build())
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        else {
            return response.body().string();

        }
    }
}