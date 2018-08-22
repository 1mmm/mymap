package com.example.wanglei.mymap;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.wanglei.mymap.Main2Activity.mla;
import static com.example.wanglei.mymap.Main2Activity.mlt;
import static com.example.wanglei.mymap.Main2Activity.user;

public class MainActivity extends AppCompatActivity {
    private MapView myMapView = null;//地图控件
    private double up=30.551,down=30.539,left=114.363,right=114.383;
    private BaiduMap myBaiduMap;//百度地图对象
    String tt="";

    private View inflate;
    private TextView choosePhoto;
    private TextView ac,ca;
    private Dialog dialog;
    BitmapDescriptor mbitmap;
    static public  String h1=null,h2=null,h3=null,h4=null,h5=null;
    private LocationClient mylocationClient;//定位服务客户对象
    private MylocationListener mylistener;//重写的监听类
    private Context context;
    public JSONArray qqq;
    public String base_url="http://120.79.159.180/";
    private final OkHttpClient client = new OkHttpClient();
    static public double myLatitude;//纬度，用于存储自己所在位置的纬度
    static public double myLongitude;//经度，用于存储自己所在位置的经度
    private float myCurrentX;

    private BitmapDescriptor myIconLocation1;//图标1，当前位置的箭头图标
//    private BitmapDescriptor myIconLocation2;//图表2,前往位置的中心图标

    private MyOrientationListener myOrientationListener;//方向感应器类对象

    private MyLocationConfiguration.LocationMode locationMode;//定位图层显示方式
//    private MyLocationConfiguration.LocationMode locationMode2;//定位图层显示方式

    private LinearLayout myLinearLayout1; //经纬度搜索区域1
    private LinearLayout myLinearLayout2; //地址搜索区域2
    final Handler handed = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 5:
                    Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1111:
                    Log.d( "handd: ",qqq.toString());
                    break;


            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        this.context = this;
        initView();
        initLocation();
        initmapEvent();
        mbitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.markersr);
    }
    private void initView() {
        myMapView = (MapView) findViewById(R.id.baiduMapView);

        myBaiduMap = myMapView.getMap();
        //根据给定增量缩放地图级别
        MapStatusUpdate msu= MapStatusUpdateFactory.zoomTo(18.0f);
        myBaiduMap.setMapStatus(msu);
    }

    private void initLocation() {
        locationMode = MyLocationConfiguration.LocationMode.NORMAL;

        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        mylocationClient = new LocationClient(this);
        mylistener = new MylocationListener();

        //注册监听器
        mylocationClient.registerLocationListener(mylistener);
        //配置定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等
        LocationClientOption mOption = new LocationClientOption();
        //设置坐标类型
        mOption.setCoorType("bd09ll");
        //设置是否需要地址信息，默认为无地址
        mOption.setIsNeedAddress(true);
        //设置是否打开gps进行定位
        mOption.setOpenGps(true);
        //设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
        int span = 1000;
        mOption.setScanSpan(span);
        //设置 LocationClientOption
        mylocationClient.setLocOption(mOption);

        //初始化图标,BitmapDescriptorFactory是bitmap 描述信息工厂类.
        myIconLocation1 = BitmapDescriptorFactory.fromResource(R.drawable.location);
//        myIconLocation2 = BitmapDescriptorFactory.fromResource(R.drawable.icon_target);

        //配置定位图层显示方式,三个参数的构造器
        MyLocationConfiguration configuration
                = new MyLocationConfiguration(locationMode, true, myIconLocation1);
        //设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效，参见 setMyLocationEnabled(boolean)
        myBaiduMap.setMyLocationConfigeration(configuration);

        myOrientationListener = new MyOrientationListener(context);
        //通过接口回调来实现实时方向的改变
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                myCurrentX = x;
            }
        });

    }
    public void initmapEvent()
    {
    /*
     * 地图长按事件
     */


    /*
     * 设置marker点击事件
     */
        BaiduMap.OnMarkerClickListener mMarkerlis=new BaiduMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(final Marker marker) {
                final makerInfo Info=(makerInfo) marker.getExtraInfo().get("marker");
                OverlayOptions overlayoptions=null;
                BitmapDescriptor bitm=BitmapDescriptorFactory
                        .fromResource(R.drawable.markersg);
                overlayoptions = new MarkerOptions()//
                        .position(Info.getLatlng())// 设置marker的位置
                        .icon(bitm)// 设置marker的图标
                        .zIndex(9);// 設置marker的所在層級

                final Marker mk=(Marker)myBaiduMap.addOverlay(overlayoptions);
                dialog = new Dialog(MainActivity.this,R.style.ActionSheetDialogStyle);
                //填充对话框的布局
                dialog.setCanceledOnTouchOutside(false);
                inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
                //初始化控件
                choosePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
                choosePhoto.setText(Info.getContent());
                ac = (TextView) inflate.findViewById(R.id.ac);
                ac.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "在"+myLatitude+","+myLongitude+"成功接受任务"+Info.gettid(), Toast.LENGTH_LONG).show();
                        new Thread() {
                            public void run() {
                                try{
                                    FormBody.Builder pa = new FormBody.Builder();
                                    pa.add("id",Info.gettid()+"");
                                    pa.add("mla",myLongitude+"");
                                    pa.add("mlt",myLatitude+"");
                                    post(pa, "ass.php");
                                }catch (Exception e) {
                                }
                            }
                        }.start();
                        mk.remove();
                        dialog.dismiss();
                    }
                });
                ca = (TextView) inflate.findViewById(R.id.ca);
                ca.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mk.remove();
                        dialog.dismiss();
                    }
                });
                //将布局设置给Dialog
                dialog.setContentView(inflate);
                //获取当前Activity所在的窗体
                Window dialogWindow = dialog.getWindow();
                //设置Dialog从窗体底部弹出
                dialogWindow.setGravity( Gravity.BOTTOM);
                //获得窗体的属性
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
                dialogWindow.setAttributes(lp);
                dialog.show();//显示对话框
                return true;
            }

        };
        myBaiduMap.setOnMarkerClickListener(mMarkerlis);
    }
    public void addOverlay(makerInfo Info)
    {
        OverlayOptions overlayoptions=null;
        Marker marker=null;
        overlayoptions = new MarkerOptions()//
                .position(Info.getLatlng())// 设置marker的位置
                .icon(mbitmap)// 设置marker的图标
                .zIndex(9);// 設置marker的所在層級

        marker=(Marker) myBaiduMap.addOverlay(overlayoptions);

        Bundle bundle=new Bundle();
        bundle.putSerializable("marker", Info);
        marker.setExtraInfo(bundle);
    }
    /*
     *创建菜单操作
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            /*
             *第一个功能，返回自己所在的位置，箭头表示
             */
            case R.id.menu_item_mylocation://返回当前位置
                changemyself();
                break;

            /*
             *第二个功能，根据经度和纬度前往位置
             */
            case R.id.menu_item_llsearch://根据经纬度搜索地点
                /*myLinearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
                //经纬度输入区域1可见
                myLinearLayout1.setVisibility(View.VISIBLE);
                final EditText myEditText_lg = (EditText) findViewById(R.id.editText_lg);
                final EditText myEditText_la = (EditText) findViewById(R.id.editText_la);
                Button button_ll = (Button) findViewById(R.id.button_llsearch);

                button_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final double mylg = Double.parseDouble(myEditText_lg.getText().toString());
                        final double myla = Double.parseDouble(myEditText_la.getText().toString());
                        getLocationByLL(myla, mylg);
                        //隐藏前面经纬度输入区域
                        myLinearLayout1.setVisibility(View.GONE);
//                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                        //隐藏输入法键盘
                        InputMethodManager imm =(InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                });*/
                CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
                builder.setTitle("任 务 目 录");
                new Thread() {
                    public void run() {
                        try {
                            FormBody.Builder pa = new FormBody.Builder();

                            tt = post(pa, "task.php");
                            JSONObject result = new JSONObject(tt);
                            qqq = result.getJSONArray("data");
                            handed.sendEmptyMessage(1111);

                        } catch (Exception e) {
                        }
                    }
                }.start();
                delay(2000);
                List<lc> mData=getData();
                builder.setad(new lcAdapter(this, (LinkedList<lc>) mData));
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (h1!=null&&h2!=null)
                                {
                                    String[] a1=h1.split(",");
                                    String[] a2=h2.split(",");
                                    float a11=Float.parseFloat(a1[0]);
                                    float a22=Float.parseFloat(a2[0]);
                                    getLocationByLL(a22,a11);
                                    myBaiduMap.clear();
                                    for (int i=0;i<a1.length;i++) {
                                        a11 = Float.parseFloat(a1[i]);
                                        a22 = Float.parseFloat(a2[i]);
                                        Log.d(a11 + " " + a22, "onClick: ");
                                        makerInfo marker=new makerInfo(a11,a22,"taskid:"+i+h3+h4,i+"",h5);
                                        addOverlay(marker);
                                    }
                                }
                                dialog.dismiss();
                                h1=null;h2=null;
                                //设置你的操作事项
                            }

                });

                builder.setNegativeButton("暂时离开",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                h1=null;h2=null;
                            }
                        });
                builder.create().show();
                break;

            /*
             *第三个功能，根据地址名前往所在的位置
             */
            case R.id.menu_item_sitesearch://根据地址搜索
                new Thread() {
                    public void run() {
                        try {
                            FormBody.Builder pa = new FormBody.Builder();

                            tt = post(pa, "task.php");
                            JSONObject result = new JSONObject(tt);
                            qqq = result.getJSONArray("data");
                            handed.sendEmptyMessage(1111);

                        } catch (Exception e) {
                        }
                    }
                }.start();
                delay(2000);
                try {
                    myBaiduMap.clear();
                    int tnum=0;
                    for (int i = 0; i < qqq.length(); i++) {
                        JSONObject jo1 = qqq.getJSONObject(i);
                        {
                            String[] a1=jo1.getString("jd").split(",");
                            String[] a2=jo1.getString("wd").split(",");
                            String a3=jo1.getString("personID");
                            String a4="\nprice:"+jo1.getString("jg");
                            String a5="\ndesc:"+jo1.getString("ms");
                            float a11=Float.parseFloat(a1[0]);
                            float a22=Float.parseFloat(a2[0]);
                            getLocationByLL(a22,a11);

                            for (int j=0;j<a1.length;j++) {
                                a11 = Float.parseFloat(a1[j]);
                                a22 = Float.parseFloat(a2[j]);
                                Log.d(a11 + " " + a22, "onClick: ");
                                makerInfo marker=new makerInfo(a11,a22,"taskid:"+tnum+a4+a5,tnum+"",a3);
                                tnum=tnum+1;
                                addOverlay(marker);
                            }
                        }
                    }}
                catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void delay(int ms){
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /*
     *根据经纬度前往
     */
    public void getLocationByLL(double la, double lg)
    {
        //地理坐标的数据结构
        LatLng latLng = new LatLng(la, lg);
        //描述地图状态将要发生的变化,通过当前经纬度来使地图显示到该位置
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        myBaiduMap.setMapStatus(msu);
    }
    public List<lc> getData(){
        List<lc> list=new LinkedList<lc>();
        try {

            for (int i = 0; i < qqq.length(); i++) {
                JSONObject jo1 = qqq.getJSONObject(i);
                {
                    Log.d(jo1.getString("personID"), "getData: ");
                    list.add(new lc(jo1.getString("personID"),jo1.getString("jd"),jo1.getString("wd"),jo1.getString("jg"),jo1.getString("ms")));  }
        }}
        catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
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
    /*
     *定位请求回调接口
     */
    public void changemyself()
    {

        myLatitude = nextDouble(down,up);
        myLongitude = nextDouble(left,right);

        MyLocationData data = new MyLocationData.Builder()
                .direction(myCurrentX)//设定图标方向
                .accuracy(0.0f)//getRadius 获取定位精度,默认值0.0f
                .latitude(myLatitude)//百度纬度坐标
                .longitude(myLongitude)//百度经度坐标
                .build();
        //设置定位数据, 只有先允许定位图层后设置数据才会生效，参见 setMyLocationEnabled(boolean)
        myBaiduMap.setMyLocationData(data);
            getLocationByLL(myLatitude, myLongitude);
        new Thread() {
            public void run() {
            try{
                FormBody.Builder pa = new FormBody.Builder();
                pa.add("id",user);
                pa.add("mla",myLongitude+"");
                pa.add("mlt",myLatitude+"");
                tt = post(pa, "registor.php");
            }catch (Exception e) {
            }
            }
        }.start();

    }
    public static double nextDouble(final double min, final double max)  {

        if (min == max) {
            return min;
        }
        return min + ((max - min) * new Random().nextDouble());
    }
    public class MylocationListener implements BDLocationListener
    {
        //定位请求回调接口
        private boolean isFirstIn=true;
        //定位请求回调函数,这里面会得到定位信息
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (isFirstIn) {
            //BDLocation 回调的百度坐标类，内部封装了如经纬度、半径等属性信息
            //MyLocationData 定位数据,定位数据建造器
            /*
            * 可以通过BDLocation配置如下参数
            * 1.accuracy 定位精度
            * 2.latitude 百度纬度坐标
            * 3.longitude 百度经度坐标
            * 4.satellitesNum GPS定位时卫星数目 getSatelliteNumber() gps定位结果时，获取gps锁定用的卫星数
            * 5.speed GPS定位时速度 getSpeed()获取速度，仅gps定位结果时有速度信息，单位公里/小时，默认值0.0f
            * 6.direction GPS定位时方向角度
            * */
                myLatitude = nextDouble(down,up);
                myLongitude = nextDouble(left,right);
            if (mla!=null) {
                myLatitude=Float.parseFloat(mla);
                myLongitude=Float.parseFloat(mlt);
            }
            else changemyself();
            MyLocationData data = new MyLocationData.Builder()
                    .direction(myCurrentX)//设定图标方向
                    .accuracy(bdLocation.getRadius())//getRadius 获取定位精度,默认值0.0f
                    .latitude(myLatitude)//百度纬度坐标
                    .longitude(myLongitude)//百度经度坐标
                    .build();
            //设置定位数据, 只有先允许定位图层后设置数据才会生效，参见 setMyLocationEnabled(boolean)
            myBaiduMap.setMyLocationData(data);

            //判断是否为第一次定位,是的话需要定位到用户当前位置

                //根据当前所在位置经纬度前往
                getLocationByLL(myLatitude, myLongitude);
                isFirstIn = false;
                //提示当前所在地址信息
//                Toast.makeText(context, bdLocation.getAddrStr(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*
    *定位服务的生命周期，达到节省
    */
    @Override
    protected void onStart() {
        super.onStart();
        //开启定位，显示位置图标
        myBaiduMap.setMyLocationEnabled(true);
        if(!mylocationClient.isStarted())
        {
            mylocationClient.start();
        }
        myOrientationListener.start();
    }
    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        myBaiduMap.setMyLocationEnabled(false);
        mylocationClient.stop();
        myOrientationListener.stop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        myMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        myMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        myMapView.onDestroy();
    }
}
