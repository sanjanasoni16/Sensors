package com.example.sensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MT19097_Main5Activity extends AppCompatActivity {

    private SQLiteDatabase sqLiteDatabase;
    Button btrecord,btstopRecord,btplay,btstop;
    String path="";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    TextView textView;

    final int REQUEST_PERMISSION_CODE=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt19097_activity_main5);
        //textView = (TextView) findViewById(R.id.textAudio);
        sqLiteDatabase=openOrCreateDatabase("audioo",0,null);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+"accelerometer");
        sqLiteDatabase.execSQL(
                "create table if not exists " + "audioo" +
                        "(audio_file blob,timestamp varchar(50))"
        );


        sqLiteDatabase.execSQL("Delete from audioo");

        if(!checkPermissionFromDevice())
            requestPermission();

        btplay=(Button)findViewById(R.id.btnPlay);
        btrecord=(Button)findViewById(R.id.btnStartRecord);
        btstop=(Button)findViewById(R.id.btnStop);
        btstopRecord=(Button)findViewById(R.id.btnStopRecord);




        btrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermissionFromDevice()){


                    path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+
                            UUID.randomUUID().toString()+"_audio_record.3gp";
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    btplay.setEnabled(false);
                    btstop.setEnabled(false);

                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(path);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] buffer =new byte[1024];
                    int read=0;
                    while (true) {
                        try {
                            if (!((read = fis.read(buffer)) != -1)) break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        baos.write(buffer, 0, read);
                    }
                    try {
                        baos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] fileByteArray = baos.toByteArray();



                    ContentValues cv = new ContentValues();
                    cv.put("filename", path);
                    cv.put("blob", fileByteArray);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                    String format = simpleDateFormat.format(new Date());
                    sqLiteDatabase.execSQL("insert into audioo values('"+cv+"','"+format+"' )");

                    Toast.makeText(MT19097_Main5Activity.this,"Recording....",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    requestPermission();
                }
            }
        });

        btstopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                btstopRecord.setEnabled(false);
                btplay.setEnabled(true);
                btrecord.setEnabled(true);
                btstop.setEnabled(false);
            }
        });

        btplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btstop.setEnabled(true);
                btstopRecord.setEnabled(false);
                btrecord.setEnabled(false);


                mediaPlayer= new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(MT19097_Main5Activity.this,"Playing....",Toast.LENGTH_SHORT).show();
            }
        });


        btstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btstopRecord.setEnabled(false);
                btrecord.setEnabled(true);
                btstop.setEnabled(false);
                btplay.setEnabled(true);
                if(mediaPlayer!= null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setupMediaRecorder();
                }
            }
        });

        //Button fetch= (Button) findViewById(R.id.fetchAudio);
        /*fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAccData();
            }
        });
*/

    }




    private void setupMediaRecorder()
    {
        mediaRecorder= new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(path);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result= ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result== PackageManager.PERMISSION_GRANTED &&
                record_audio_result== PackageManager.PERMISSION_GRANTED;
    }
    public void fetchAccData()
    {
        Cursor cursor=sqLiteDatabase.rawQuery("select * from audioo",null);
        while(cursor.moveToNext())
        {
            textView.append("\n"+"X: "+cursor.getString(0).toString()+" y: "+cursor.getString(1).toString() +" timestamp: "+cursor.getString(2).toString());
        }
    }
}
