package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MT19097_Main2Activity extends AppCompatActivity implements SensorEventListener {


    private float lastX, lastY, lastZ;
    private TextView textView;
    private Button button1;
    private float changex = 0;
    private float changey = 0;
    private float changez = 0;
    private Sensor accelerometer;
    private TextView cX, cY, cZ;
    private Vibrator vibr;
    private float vThres = 0;
    private SQLiteDatabase sqLiteDatabase;
    private SensorManager sensorM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt19097_activity_main2);
        initviews();
        createdb();
        button1 = (Button)findViewById(R.id.fetch);
        textView = (TextView) findViewById(R.id.fetchtext);
        sensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vThres = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        vibr = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });

    }



    private void createdb() {
        sqLiteDatabase=openOrCreateDatabase("accelerometer",0,null);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+"accelerometer");
        sqLiteDatabase.execSQL(
                "create table if not exists " + "accelerometer" +
                        "(x varchar(50), y varchar(50), z varchar(50), timestamp varchar(50))"
        );

        //sqLiteDatabase
          //      .execSQL("create table if not exists "+
            //            "accelerometer(x varchar(50),y varchar(50),z varchar(50),id varchar(50))");
        sqLiteDatabase.execSQL("Delete from accelerometer");


    }
    public void fetchData()
    {
        Cursor cursor=sqLiteDatabase.rawQuery("select * from accelerometer",null);
        while(cursor.moveToNext())
        {
            textView.append("\n"+"X: "+cursor.getString(0).toString()+" y: "+cursor.getString(1).toString()+" z: "+cursor.getString(2).toString() +" timestamp: "+cursor.getString(3).toString());
        }
    }

    public void initviews() {
        cX = (TextView) findViewById(R.id.currentX);
        cY = (TextView) findViewById(R.id.currentY);
        cZ = (TextView) findViewById(R.id.currentZ);
    }

    protected void onResume() {
        super.onResume();
        sensorM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorM.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        dispclean();
        // display the current x,y,z accelerometer values
        dispcurr();
        // display the max x,y,z accelerometer values

        // get the change of the x,y,z values of the accelerometer
        changex = Math.abs(lastX - sensorEvent.values[0]);
        changey = Math.abs(lastY - sensorEvent.values[1]);
        changez = Math.abs(lastZ - sensorEvent.values[2]);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String format = simpleDateFormat.format(new Date());
        sqLiteDatabase.execSQL("insert into accelerometer values('"+sensorEvent.values[0]+"','"+sensorEvent.values[1]+"','"+sensorEvent.values[2]+"','"+format+"')");// if the change is below 2, it is just plain noise
        if (changex < 2)
            changex = 0;
        if (changey < 2)
            changey = 0;
        if ((changex > vThres) || (changey > vThres) || (changez > vThres)) {
            vibr.vibrate(50);
        }
    }
    public void dispclean() {
        cX.setText("0.0");
        cY.setText("0.0");
        cZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void dispcurr() {
        cX.setText( Float.toString(changex));
        cY.setText( Float.toString(changey));
        cZ.setText( Float.toString(changez));
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
