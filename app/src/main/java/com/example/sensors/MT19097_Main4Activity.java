package com.example.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MT19097_Main4Activity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listViewnew;
    private Button button1;
    private  int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt19097_activity_main4);
        button1 = findViewById(R.id.scanbtn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanit();

            }
        });

        listViewnew = findViewById(R.id.wifiList);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()){
            Toast.makeText(this,"Wifi is disabled! Please turn on wifi",Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        listViewnew.setAdapter(adapter);
        scanit();

    }

    private  void scanit(){
        arrayList.clear();
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this,"Scanning Wi-Fi",Toast.LENGTH_SHORT).show();


    }
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results){


                int rssi = wifiManager.getConnectionInfo().getRssi();
                int level1 = WifiManager.calculateSignalLevel(rssi, 5);
                int level = WifiManager.calculateSignalLevel(scanResult.level, 5);


                if (level <= 0 && level >= -50) {
                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+" Excellent");
                    adapter.notifyDataSetChanged();

                } else if (level < -50 && level >= -70) {
                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+"Good Signal");
                    adapter.notifyDataSetChanged();
                }
                else if (level < -70 && level >= -80) {

                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+"Low Signal");
                    adapter.notifyDataSetChanged();

                } else if (level < -80 && level >= -100) {
                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+"Very Weak Signal");
                    adapter.notifyDataSetChanged();

                } else {
                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+"No signal");
                    adapter.notifyDataSetChanged();
                }
            }
            int rssi = wifiManager.getConnectionInfo().getRssi();
            int level = WifiManager.calculateSignalLevel(rssi, 5);
        }
    };
}

