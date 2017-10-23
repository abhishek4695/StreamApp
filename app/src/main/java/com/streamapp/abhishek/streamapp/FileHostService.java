package com.streamapp.abhishek.streamapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class FileHostService extends Service {

    NotificationManager notificationMgr;
    NotificationCompat.Builder builder;
    public static final String EXTRA_IP = "com.streamapp.abhishek.streamapp.extra.IP";
    public static final String EXTRA_MIME = "com.streamapp.abhishek.streamapp.extra.MIME";
    public static final String EXTRA_FILEPATH = "com.streamapp.abhishek.streamapp.extra.FILEPATH";
    String IP,MIME,FilePath;
    File file;
    WebServer webServer;

    public FileHostService() {
    }

    public class LocalBinder extends Binder {
        FileHostService getService() {
            return FileHostService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate(){
        notificationMgr =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);
        webServer = new WebServer();
        StartServer();
        StarttheActivity();
        super.onCreate();
    }

    private void StartServer() {
        try {
            webServer.start();
        } catch (IOException ioe) {
            Log.w("Httpd", "The server could not start.");
        }
        Log.w("Httpd", "Web server initialized.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Bundle bundle = intent.getExtras();

        IP = bundle.getString(EXTRA_IP);
        MIME = bundle.getString(EXTRA_MIME);
        FilePath = bundle.getString(EXTRA_FILEPATH);

        return START_STICKY;
    }

    public void StarttheActivity(){
        Intent startServer = new Intent(FileHostService.this,ShareActivity.class);
        startServer.putExtra(ShareActivity.EXTRA_IP1,IP);
        startServer.putExtra(ShareActivity.EXTRA_MIME1,MIME);
        startServer.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(startServer);
    }

    public void showNotification(){
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        builder.setContentTitle("Server Running")
                .setContentText(FilePath)
                .setSmallIcon(R.drawable.ic_menu_share)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent);

        Notification notification = builder.build();
        notificationMgr.notify(1, notification);

    }

    public void StopNotification(){
        notificationMgr.cancel(1);
        if(webServer.isAlive()) {
            webServer.stop();
        }
    }

    private class WebServer extends NanoHTTPD {

        public WebServer() {
            super(8080);
        }

        @Override
        public Response serve(IHTTPSession session) {
            //String answer = "";
            FileInputStream fis = null;
            file = new File(FilePath);
            long y = 0;
            try {
                // Open file from SD Card
                /*FileReader index = new FileReader(root.getAbsolutePath() +
                        "/www/index.txt");
                BufferedReader reader = new BufferedReader(index);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    answer += line;
                }
                reader.close();*/

                y = file.length();
                fis = new FileInputStream(file);
            } catch (IOException ioe) {
                Log.w("Httpd", ioe.toString());
            }


            return newFixedLengthResponse(/*answer + x*/Status.OK,MIME,fis,y);
        }

    }

}
