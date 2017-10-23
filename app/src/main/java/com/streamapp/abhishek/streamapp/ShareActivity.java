package com.streamapp.abhishek.streamapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.QRCode;


public class ShareActivity extends AppCompatActivity {
    public static final String EXTRA_MIME1 = "com.streamapp.abhishek.streamapp.extra.MIME1";
    public static final String EXTRA_IP1 = "com.streamapp.abhishek.streamapp.extra.IP1";
    public final static int WIDTH=500;
    String MIME1,IP1;
    ImageView QRCODE;
    TextView Info;
    Thread thread;
    FileHostService mService;
    boolean mBound = false;
    private ServiceConnection mConnection;
    Button startn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        MIME1 = "mime";
        IP1 = "ip";
        startn = (Button) findViewById(R.id.StartNotif);
        Info = (TextView) findViewById(R.id.Info);
        getID();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    synchronized (this) {
                        wait(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = null;

                                try {

                                    bitmap = encodeAsBitmap(MIME1 + "#" + IP1 + ":8080");
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                                QRCODE.setImageBitmap(bitmap);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }

            }
        });
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                FileHostService.LocalBinder binder = (FileHostService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };
        Intent intent2 = new Intent(this, FileHostService.class);
        bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);

        startn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.showNotification();
                MIME1 = mService.MIME;
                IP1 = mService.IP;
                Info.setText("Access file on devices without StreamApp by entering : http://" +
                        IP1 + ":8080 in any web browser (Make sure host device and receiving device are on the same network)" + "\n" +
                        "Tap the QR Code to stop Streaming and return to StreamApp home");
                thread.start();
            }
        });




        QRCODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShareActivity.this,"Stopping Server",Toast.LENGTH_LONG).show();
                mService.StopNotification();
                stopService(new Intent(ShareActivity.this,FileHostService.class));
                thread.interrupt();
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        // set the string passed from the service to the original intent
        setIntent(intent);
        ProcessExtras();

    }


    private void ProcessExtras() {
        Intent intent = getIntent();
        MIME1 = intent.getStringExtra(EXTRA_MIME1);
        IP1 = intent.getStringExtra(EXTRA_IP1);
    }

    private void getID() {
        QRCODE=(ImageView) findViewById(R.id.img_qrcode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, FileHostService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    }

}
