package com.plawyue.switchdns;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ResolveInfo;
import android.net.DnsResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xbill.DNS.Cache;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.jar.Attributes;


public class MainActivity extends AppCompatActivity {
    TextView logoutput;
    ScrollView textscrol;
    TextView Nowspeed;
    double max_speed=0;
    String max_server="";
    Boolean iscomparing=false;
    final String CHANNEL_ID = "noti1";
    final String CHANNEL_NAME = "channel_name_1";
    ArrayList<String> Dns_list = new ArrayList<String>(Arrays.asList("218.201.96.130","211.138.180.2","221.131.143.69","210.22.70.3","202.106.0.20","222.172.200.68","202.101.224.69","202.98.192.67","61.132.163.68","208.67.222.222","180.76.76.76","114.114.114.110","114.114.114.114","8.8.8.8","8.8.4.4","218.102.23.228","211.136.192.6","223.5.5.5","168.126.63.1","168.126.63.2","168.95.1.1","168.95.192.1","208.67.222.222","208.67.220.220","168.126.63.1","168.95.192.1","84.200.69.80","119.29.29.29"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start_btn=findViewById(R.id.button_start);
        logoutput=findViewById(R.id.textView);
        textscrol=findViewById(R.id.textscrol);
        Nowspeed=findViewById(R.id.Now_speed);




        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new NetWorkListenerUtils(this,mHnadler).startShowNetSpeed();
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                if(iscomparing==false){
                    iscomparing=true;
                    start_btn.setText("?????????");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(String Dns:Dns_list){
                            if(iscomparing) {
                                String Ipresult = Dnsviaser(Dns, "ctest-dl-lp1.cdn.nintendo.net", Dns_list.size() - Dns_list.indexOf(Dns),true);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textscrol.fullScroll(ScrollView.FOCUS_DOWN);
                                    }
                                });
                                if (Ipresult != null) {
                                    outoftime(Ipresult,true,Dns);

//                                    String ipupresult=Dnsviaser(Dns,"ctest-ul-lp1.cdn.nintendo.net",1,false);
//                                    outoftime(ipupresult,false);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textscrol.fullScroll(ScrollView.FOCUS_DOWN);
                                    }
                                });
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                start_btn.setText("????????????");
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);

                            }
                        });
                        logoutput.post(new Runnable() {
                            @Override
                            public void run() {
                                logoutput.append(max_server+"??????????????????DNS  "+String.format("%.2f", max_speed)+" MB/S"+"\n"+"------------------------------------"+"\n");


                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textscrol.fullScroll(textscrol.FOCUS_DOWN);
                                showNormalDialog(max_server+"  ??????  "+String.format("%.2f", max_speed)+" MB/S");
                            }
                        });
                        iscomparing=false;

                    }
                }).start();
            }
            }
        });


    }
    private void SpeedCompare(String IP,boolean isdownload,String usingdns){
        if(isdownload) {
            String url = "http://" + IP + "/30m";
            try {
                URL murl = new URL(url);
                HttpURLConnection mconn = (HttpURLConnection) murl.openConnection();
                mconn.setRequestProperty("user-agent", "Nintendo NX");
                mconn.setRequestProperty("host", "ctest-dl-lp1.cdn.nintendo.net");
                mconn.setRequestMethod("GET");

                mconn.connect();

                int fileSize = mconn.getContentLength();
                System.out.println(fileSize);

                long startTime = System.currentTimeMillis();
                InputStream input = mconn.getInputStream();
                BufferedReader mbuff = new BufferedReader(new InputStreamReader(input));
                while (mbuff.read() != -1) {

                }
                long endTime = System.currentTimeMillis();
                double usedTime = (endTime - startTime) / 1000.0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logoutput.append("????????????  " + String.format("%.2f", (30.0 / usedTime)) + " MB/S" + "\n" + "------------------------------------" + "\n");
                        if ((30.0 / usedTime) > max_speed) {
                            max_speed = (30.0 / usedTime);
                            max_server =usingdns;
                        }

                    }
                });
                notifyh("??????"+usingdns,String.format("%.2f", (30.0 / usedTime)) + " MB/S",1);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            String url = "http://" + IP + "/1m";
            try {
                URL murl = new URL(url);
                HttpURLConnection mconn = (HttpURLConnection) murl.openConnection();
                mconn.setRequestProperty("user-agent", "Nintendo NX");
                mconn.setRequestProperty("host", "ctest-ul-lp1.cdn.nintendo.net");
                mconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                mconn.setRequestMethod("POST");
                mconn.setDoOutput(true);
                mconn.setUseCaches(false);
                mconn.connect();



                long startTime = System.currentTimeMillis();
                OutputStream outputStream = mconn.getOutputStream();
                for(int i=1;i!=1048576;i++){
                    outputStream.write(1);
                }
                long endTime = System.currentTimeMillis();
                double usedTime = (endTime - startTime) / 1000.0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logoutput.append("????????????  " + String.format("%.2f", (1.0 / usedTime)) + " MB/S" + "\n" + "------------------------------------" + "\n");


                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private String Dnsviaser(String Dns,String Domain,int lost,boolean isdownload){
        if(isdownload) {
            try {
                Resolver resolver = new SimpleResolver(Dns);
                Lookup lookup = new Lookup(Domain, Type.A);
                resolver.setTimeout(3);

                System.out.println(resolver.getTimeout());
                lookup.setResolver(resolver);
                Cache cache = new Cache();
                lookup.setCache(cache);
                lookup.run();

                if (lookup.getResult() == Lookup.SUCCESSFUL) {
                    Record[] record = lookup.getAnswers();

                    System.out.println(record[0].rdataToString());
                    logoutput.post(new Runnable() {
                        @Override
                        public void run() {
                            //record[0].rdataToString()
                            logoutput.append("?????????DNS: "+Dns  + "\n");
                            Button start_btn=findViewById(R.id.button_start);
                            start_btn.setText("????????? ??????"+lost+"???");



                        }
                    });

                    return record[0].rdataToString();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            logoutput.append("?????????DNS: "+ "\n"+Dns + " DNS??????????????? " + "\n" + "------------------------------------" + "\n");
                            Button start_btn=findViewById(R.id.button_start);
                            start_btn.setText("????????? ??????"+lost+"???");
                            textscrol.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    System.out.println("???");
                    return null;
                }

            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        else {
            try {
                Resolver resolver = new SimpleResolver(Dns);
                Lookup lookup = new Lookup(Domain, Type.A);
                resolver.setTimeout(3);

                System.out.println(resolver.getTimeout());
                lookup.setResolver(resolver);
                Cache cache = new Cache();
                lookup.setCache(cache);
                lookup.run();

                if (lookup.getResult() == Lookup.SUCCESSFUL) {
                    Record[] record = lookup.getAnswers();
                    return record[0].rdataToString();
                } else {
                    System.out.println("???");
                    return null;
                }

            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    return  null;
    }
    private void outoftime(String Ipresult,boolean isdownload,String usingdns){
        final ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<String> call = new Callable<String>() {
            @Override
            public String call() throws Exception {
                //????????????????????????
                SpeedCompare(Ipresult,isdownload,usingdns);
                return "????????????!";
            }
        };
        try {
            Future<String> future = exec.submit(call);
            String obj = future.get(30000, TimeUnit.MILLISECONDS); //?????????????????????????????? 1 ???
            System.out.println("??????????????????:" + obj);

        } catch (TimeoutException ex) {

            System.out.println("???????????????....");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logoutput.append("??????????????????"+"\n"+"------------------------------------"+"\n");


                }
            });

            ex.printStackTrace();
        } catch (Exception e) {

            System.out.println("????????????.");
            e.printStackTrace();
        }
        exec.shutdownNow();
        // ???????????????
        exec.shutdown();
        System.out.println(exec.isShutdown());
    }
    private Handler mHnadler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    Nowspeed.setText("??????????????? " + msg.obj.toString());
                    notifyh("????????????",msg.obj.toString(),2);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void showNormalDialog(String fastdns){
        /* @setIcon ?????????????????????
         * @setTitle ?????????????????????
         * @setMessage ???????????????????????????
         * setXXX????????????Dialog???????????????????????????????????????
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.logo);
        normalDialog.setTitle("??????DNS");
        normalDialog.setMessage(fastdns);
        normalDialog.setCancelable(false);
        normalDialog.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // ??????
        normalDialog.show();
    }
    private void notifyh(String main,String small,int id){
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //??????Android O??????????????????
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID+id,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            //???????????????IMPORTANCE_NOENE????????????????????????????????????????????????
            //????????????????????????
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder= new NotificationCompat.Builder(MainActivity.this,CHANNEL_ID+id);


        builder.setSmallIcon(R.drawable.logo)
                .setContentTitle(main)
                .setContentText(small)
                .setAutoCancel(true)
                .setSilent(true);

        mNotificationManager.notify(id, builder.build());
    }
}