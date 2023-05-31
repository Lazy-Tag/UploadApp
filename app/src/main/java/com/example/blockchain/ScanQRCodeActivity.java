package com.example.blockchain;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.client.android.Intents;

import java.util.ArrayList;
import java.util.List;

public class ScanQRCodeActivity extends AppCompatActivity {
    private static final String TAG = "ScanQRCodeActivity";
    private static final int REQUEST_CODE_SCAN = 100;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200;
    List<String> wifiInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        setTitle("扫描二维码");
        Button scanButton = findViewById(R.id.scan_button);
        Button wifiButton = findViewById(R.id.wifi_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWifiInfo();
            }
        });

        setActionBar(new Toolbar(this));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getWifiInfo() {
        wifiInfo.clear();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();

        List<ScanResult> scanResults = wifiManager.getScanResults();
        for (ScanResult scanResult : scanResults) {
            String bssid = scanResult.BSSID;
            int rssi = scanResult.level;
            wifiInfo.add("Mac地址: " + bssid + "\n" + " 信号强度: " + Integer.toString(rssi));
        }
        TextView textView = findViewById(R.id.result_text_view);
        textView.append("Wi-Fi指纹扫描成功！\n");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startQRCodeScanActivity();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
        }
    }

    private void startQRCodeScanActivity() {
        Intent intent = new Intent(this, MyCaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(Intents.Scan.RESULT)) {
                String qrCodeResult = data.getStringExtra(Intents.Scan.RESULT);
                StringBuilder result = new StringBuilder(qrCodeResult);
                result.append("wifi指纹信息: \n");
                for (String wifi : wifiInfo) {
                    result.append(wifi).append("\n");
                }
                TextView textView = findViewById(R.id.result_text_view);
                textView.append(result);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRCodeScanActivity();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
