package com.streamapp.abhishek.streamapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageViewer extends AppCompatActivity {

    String IP;
    TextView Header;
    ImageView imageView;
    File file;
    //int x = 0;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Intent intent = getIntent();
        IP = intent.getStringExtra(ReceiveFile.EXTRA_MESSAGE);
        Header = (TextView) findViewById(R.id.ImgHead);
        imageView = (ImageView) findViewById(R.id.imageView);
        Header.setText("Tap Image to stretch" );
        new ImageLoadTask("http://" + IP, imageView).execute();
        imageView.setDrawingCacheEnabled(true);




        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(x == 0){
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    x = 1;
                }
                else{
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    x = 0;
                }*/
                try {

                    File cachePath = new File(ImageViewer.this.getCacheDir(), "images");
                    cachePath.mkdirs(); // don't forget to make the directory
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                    imageView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File imagePath = new File(ImageViewer.this.getCacheDir(), "images");
                File newFile = new File(imagePath, "image.png");
                Uri contentUri = FileProvider.getUriForFile(ImageViewer.this, "com.streamapp.abhishek.streamapp.fileprovider", newFile);

                if (contentUri != null) {

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));

                }
            }
        });


    }

    class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                /*InputStream input2 = new BufferedInputStream(urlConnection.openStream(),
                        8192);
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "Image.jpg");

                byte data[] = new byte[1024];

                int count = 0;
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                input2.close();*/
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                connection.disconnect();
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);


        }

    }



}
