package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MT19097_MainActivity extends AppCompatActivity {
    private Button buttonAccelerometer;
    private Button buttonGPS;
    private Button buttonWifi;
    private Button buttonAudio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt19097_activity_main);
        buttonAccelerometer = (Button) findViewById(R.id.button);
        buttonGPS = (Button) findViewById(R.id.button2);
        buttonWifi = (Button) findViewById(R.id.button3);
        buttonAudio = (Button) findViewById(R.id.button4);

        buttonAccelerometer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), MT19097_Main2Activity.class);
                startActivity(i);
            }
        });

        buttonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MT19097_Main3Activity.class);
                startActivity(i);
            }
        });

        buttonWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MT19097_Main4Activity.class);
                startActivity(i);
            }
        });

        buttonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MT19097_Main5Activity.class);
                startActivity(i);
            }
        });
    }
}
