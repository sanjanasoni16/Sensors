package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MT19097_Main3Activity extends AppCompatActivity {

    Button button;
    TextView textView1, textView2;
    TextView textView;
    private Button button1;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt19097_activity_main3);
        createDb();
        button = (Button) findViewById(R.id.locationbutton);
        textView1 = (TextView) findViewById(R.id.LatitudeTextview);
        textView2 = (TextView) findViewById(R.id.LongitudeTextview);
        textView = (TextView) findViewById(R.id.fetchtext);
        button1 = (Button) findViewById(R.id.fetch);
        ActivityCompat.requestPermissions(MT19097_Main3Activity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MT19097_GPSjava g = new MT19097_GPSjava(getApplicationContext());
                Location l = g.getLocation();
                if(l != null){
                    double latitude = l.getLatitude();
                    double longitude = l.getLongitude();
                    String lat = Double.toString(latitude);
                    String longt = Double.toString(longitude);
                    textView1.setText(lat);
                    textView2.setText(longt);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                    String format = simpleDateFormat.format(new Date());
                    sqLiteDatabase.execSQL("insert into gps values('"+lat+"','"+longt+"','"+format+"' )");// if the change is below 2, it is just plain noise

                    Toast.makeText(getApplicationContext(),"Latitude:"+latitude+"\n Longitude:"+longitude,Toast.LENGTH_LONG).show();
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });



    }

    private void createDb() {

        sqLiteDatabase=openOrCreateDatabase("gps",0,null);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+"accelerometer");
        sqLiteDatabase.execSQL(
                "create table if not exists " + "gps" +
                        "(lat varchar(50), lon varchar(50), timestamp varchar(50))"
        );

        //sqLiteDatabase
        //      .execSQL("create table if not exists "+
        //            "accelerometer(x varchar(50),y varchar(50),z varchar(50),id varchar(50))");
        sqLiteDatabase.execSQL("Delete from gps");

    }

    public void fetchData()
    {
        Cursor cursor=sqLiteDatabase.rawQuery("select * from gps",null);
        while(cursor.moveToNext())
        {
            textView.append("\n"+"X: "+cursor.getString(0).toString()+" y: "+cursor.getString(1).toString() +" timestamp: "+cursor.getString(2).toString());
        }
    }


}
