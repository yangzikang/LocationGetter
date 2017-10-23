package com.yqb.locationgetter;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.password;

public class MainActivity extends Activity {

    String uid;
    String sid;
    double longitude;
    double dimension;
    String date;

    TextView text;
    boolean isWorking;
    EditText editText;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            findLocation();
        }
    };

    OkHttpClient mHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        isWorking = true;
        Intent intent = getIntent();
        uid = intent.getStringExtra("usrID");
        Log.d("uid",uid);
        sid = "1";
        longitude = 1.00000;
        dimension = 1.00000;
        date = new Date().toString();
        Log.d("daate",date);
        Button btn = (Button) findViewById(R.id.button);
        text = (TextView) findViewById( R.id.text );
        editText = (EditText)findViewById(R.id.editText);
        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = editText.getText().toString();
                try{
                    int intTime = Integer.parseInt(time)*1000;
                    beginFindLocation(intTime);
                } catch (Exception e){
                    beginFindLocation(3000);
                }
            }
        } );
        Button btn2 = (Button)findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWorking = false;
            }
        });

    }

    private void beginFindLocation(final int time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isWorking) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    private void findLocation() {
        Location location = LocationUtils.getInstance(MainActivity.this).showLocation();
        if (location != null) {
            String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
            text.setText(address);
            Log.w("yangzikang", new Date().toString());
            dimension = location.getLatitude();
            longitude = location.getLongitude();
            date = new Date().toString();
            httpUrlConnectionPost();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isWorking = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationUtils.getInstance(this).removeLocationUpdatesListener();
    }


    public void httpUrlConnectionPost() {

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("uid", uid);
        builder.add("sid", sid);
        builder.add("longitude", String.valueOf(longitude));
        builder.add("dimension", String.valueOf(dimension));
        builder.add("createtime", date);
        RequestBody formBody = builder.build();



        Request request = new Request.Builder()
                .url("http://39.106.46.169/IOTtest/savegps.do")
                .post(formBody)
                .build();
        try {
            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {}

                @Override
                public void onResponse(Call call, Response response) throws IOException {}
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("yangzikang","post");
    }

}
