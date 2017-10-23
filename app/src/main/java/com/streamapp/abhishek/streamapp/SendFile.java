package com.streamapp.abhishek.streamapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;


public class SendFile extends Activity {
    TextView textView;
    Button Send;
    private static final int REQUEST_CHOOSER = 1234;

    String FilePath,MIMEType,ip;
    File file;
    int apState;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        textView = (TextView) findViewById(R.id.status);
        Send = (Button) findViewById(R.id.Send);
        final WifiManager wm = (WifiManager) SendFile.this.getApplicationContext().getSystemService(WIFI_SERVICE);

        try {
            apState = (Integer) wm.getClass().getMethod("getWifiApState").invoke(wm);
        }catch (Exception e){
            e.printStackTrace();
        }


        //imageView = (ImageView) findViewById(R.id.img_qr_code_image);
        if(apState == 13) {
            //ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            ip = "192.168.43.1";
        }
        else{
            ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        }

        Intent getContentIntent = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        startActivityForResult(intent, REQUEST_CHOOSER);


        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startFileServer = new Intent(SendFile.this,FileHostService.class);
                //startFileServer.setAction(FileServer.ACTION_FOO);
                startFileServer.putExtra(FileHostService.EXTRA_IP,ip);
                startFileServer.putExtra(FileHostService.EXTRA_MIME,MIMEType);
                startFileServer.putExtra(FileHostService.EXTRA_FILEPATH,file.getAbsolutePath());
                startService(startFileServer);
                finish();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {

                    final Uri uri = data.getData();

                    // Get the File path from the Uri
                    FilePath = FileUtils.getPath(this, uri);

                    textView.setText(FilePath);

                    ContentResolver cR = SendFile.this.getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String fileext = mime.getExtensionFromMimeType(cR.getType(uri));
                    MIMEType = cR.getType(uri);

                    // Alternatively, use FileUtils.getFile(Context, Uri)
                    if (FilePath != null && FileUtils.isLocal(FilePath)) {
                        file = new File(FilePath);
                    }
                }
                break;
        }
    }

}
