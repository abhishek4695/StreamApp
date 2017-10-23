package com.streamapp.abhishek.streamapp;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import android.os.Handler;
import java.util.logging.LogRecord;

import static com.streamapp.abhishek.streamapp.R.color.white;

public class AudioPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {
    String IP;
    TextView Info;
    MediaPlayer mediaPlayer;
    MediaController mediaController;
    ImageView AlbumArt;
    String TrackInfo;
    int flag;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        Intent intent = getIntent();
        IP = intent.getStringExtra(ReceiveFile.EXTRA_MESSAGE);
        AlbumArt = (ImageView) findViewById(R.id.AlbumArt);
        AlbumArt.setImageResource(R.drawable.img);
        AlbumArt.setScaleType(ImageView.ScaleType.FIT_XY);
        Info = (TextView) findViewById(R.id.Info);
        TrackInfo = " ";
        flag = 0;
        mediaController = new MediaController(this);
        /*Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mediaPlayer.pause();
                    Play.setEnabled(true);
                    Pause.setEnabled(false);
            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                Play.setEnabled(false);
                Pause.setEnabled(true);
            }
        });

        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.release();
                finish();
            }
        });*/
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(AudioPlayer.this);
        PlayMusic();
    }
    void PlayMusic(){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource("http://" + IP);

        } catch (IOException e) {
            e.printStackTrace();

        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.main_audio_view));
        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaController.hide();
        mediaPlayer.stop();
        mediaPlayer.release();
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        mediaController.show(5000);
        return false;
    }
}
