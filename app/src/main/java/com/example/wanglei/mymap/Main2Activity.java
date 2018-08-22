package com.example.wanglei.mymap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Main2Activity extends AppCompatActivity {
    public TextView btn;
    public EditText ans,code;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static boolean yy;
    public  GestureDetector detector;
    public String base_url="http://120.79.159.180/";
    private final OkHttpClient client = new OkHttpClient();
    public LinearLayout a;
    String tt="";

    static String sspp="";
    static String user;
    static String mla,mlt;
    static double target=0;
    static SharedHelper sh;
    static String tft;

    private Context mContext;
    static int pre,a1,a2,a3,a4,a5;
    public class erro {
        private String mla;
        private String mlt;
        erro(String mla,String mlt){
            this.mla = mla;
            this.mlt = mlt;
        }
    }
    final Handler hand = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            //这里的话如果接受到信息码是123
            switch (msg.what) {
                case 0:
                    if (sh.read(user+"tt").get(user+"tt").equals("")) {

                        sh.save(user+"tt", "1");
                    }
                    if (sh.read(user+"le").get(user+"le").equals("")) {

                        sh.save(user+"le", "()");
                    }
                    Toast.makeText(Main2Activity.this, tt, Toast.LENGTH_SHORT).show();
                    Toast.makeText(Main2Activity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    Toast.makeText(Main2Activity.this, "账号不存在", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(Main2Activity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(Main2Activity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    break;
                case 1111:
                    Toast.makeText(Main2Activity.this, tt, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            SysApplication.getInstance().exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        SysApplication.getInstance().addActivity(this);
        a=(LinearLayout)  findViewById(R.id.a);
        a1=1;a2=1;a3=1;a4=1;a5=1;yy=false;
        pre=4;

        mContext = getApplicationContext();
        sh = new SharedHelper(mContext);

        btn=(TextView) findViewById(R.id.btnThree);

        ans=(EditText) findViewById(R.id.ans);
        if (sh.read("username").get("username")!=null) ans.setText(sh.read("username").get("username"));
        sh.save("main","1");
        // 这一步必须要做,否则不会显示.
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread()
                {
                    @Override
                    public void run() {
                        try {
                            FormBody.Builder pa=new  FormBody.Builder();
                            pa.add("id",ans.getText().toString());
                            sh.save("username",ans.getText().toString());
                            tt=post(pa,"login.php");
                            hand.sendEmptyMessage(1111);
                            Gson gson = new Gson();
                            mla = gson.fromJson(tt,erro.class).mlt;
                            mlt=gson.fromJson(tt,erro.class).mla;
                            user=ans.getText().toString();
                            hand.sendEmptyMessage(0);
                        }
                        catch (Exception e)
                        {
                            hand.sendEmptyMessage(2);
                        }
                    }


                }.start();



            }
        });
    }
    String post(FormBody.Builder pa,String UR) throws Exception {
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

