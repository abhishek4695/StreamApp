package com.streamapp.abhishek.streamapp;

import android.content.Intent;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.io.File;
import java.io.IOException;

public class ReceiveFile extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    QRCodeReaderView qrCodeReaderView;
    TextView textView;
    String MIME,IP;
    public static final String EXTRA_MESSAGE = "com.streamapp.abhishek.streamapp.IP_ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieve_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setAutofocusInterval(1000);
        qrCodeReaderView.setQRDecodingEnabled(true);
        textView = (TextView) findViewById(R.id.text);

    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        String[] parts = text.split("#");
        MIME = parts[0];
        IP = parts[1];
        textView.setText("MIME Type = " + MIME + " And IP = " + IP);
        /*if (MIME.equals("audio/mpeg")){
            Toast.makeText(ReceiveFile.this,"uigef",Toast.LENGTH_LONG).show();
            AudioPlay();
        }*/
        if(MIME.contains("audio/")){
            Intent intent = new Intent(ReceiveFile.this,AudioPlayer.class);
            intent.putExtra(EXTRA_MESSAGE,IP);
            startActivity(intent);
            finish();
        }

        if(MIME.contains("video/")){
            Intent intent = new Intent(ReceiveFile.this,MediaPlayActivity.class);
            intent.putExtra(EXTRA_MESSAGE,IP);
            startActivity(intent);
            finish();
        }

        if(MIME.contains("image/")){
            Intent intent = new Intent(ReceiveFile.this,ImageViewer.class);
            intent.putExtra(EXTRA_MESSAGE,IP);
            startActivity(intent);
            finish();
        }

        qrCodeReaderView.setQRDecodingEnabled(false);
    }

   /* void AudioPlay(){
        MediaPlayer mediaPlayer = new MediaPlayer();
        Toast.makeText(ReceiveFile.this,"Working1",Toast.LENGTH_LONG).show();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource("http://" + IP);

        } catch (IOException e) {
            e.printStackTrace();

        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                Toast.makeText(ReceiveFile.this,"Working",Toast.LENGTH_LONG).show();
            }
        });
    }
*/
    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
